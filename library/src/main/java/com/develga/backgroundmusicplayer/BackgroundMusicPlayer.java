package com.develga.backgroundmusicplayer;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.IOException;

public class BackgroundMusicPlayer {
    public static final String TAG = BackgroundMusicPlayer.class.getSimpleName();
    public static final int FADE_VOLUME = 0;
    public static final String KEY_VOLUME = "volume";
    public static final float VOLUME_STEP = 0.1f;

    private static final BackgroundMusicPlayer sInstance = new BackgroundMusicPlayer();

    private Handler mHandler = new RequestHandler();
    private String mMusicFileName;
    private float mCurrentVolume;
    private int mLastPlayedAt;
    private Context mPendingContext;
    private float mPendingVolume;
    private String mPendingMusicFileName;
    private OnStateChangeListener mListener;


    private BackgroundMusicPlayer() {
    }

    private MediaPlayer mMediaPlayer;

    public static RequestManager with(@NonNull Context context) {
        return new RequestManager(sInstance, context);
    }

    public static void setOnStateChangeListener(OnStateChangeListener listener) {
        sInstance.mListener = listener;
    }

    private void load(Context context, String fileName) {
        synchronized (this) {
            mMediaPlayer = new MediaPlayer();
            try {
                AssetFileDescriptor assetFileDescriptor = context.getAssets().openFd(fileName);
                mMediaPlayer.setDataSource(
                        assetFileDescriptor.getFileDescriptor(),
                        assetFileDescriptor.getStartOffset(),
                        assetFileDescriptor.getLength());
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mMediaPlayer.prepare();
                mMediaPlayer.setLooping(true);
                if (mCurrentVolume == 0) {
                    mCurrentVolume = VOLUME_STEP;
                }
                mMusicFileName = fileName;
                setVolume(mCurrentVolume);
                mMediaPlayer.seekTo(mLastPlayedAt);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void start(Context context, String fileName, float volume) {
        synchronized (this) {
            if (Log.isLoggable(TAG, Log.DEBUG)) {
                dumpStatus();
                Log.d(TAG, "start() file name:" + fileName + ", volume:" + volume);
            }
            if (mMusicFileName != null && !mMusicFileName.equals(fileName) && mMediaPlayer != null) {
                stop();
                mPendingMusicFileName = fileName;
                mPendingContext = context.getApplicationContext();
                mPendingVolume = volume;
                return;
            }


            if (mMediaPlayer == null) {
                load(context, fileName);
            }

            if (!mMediaPlayer.isPlaying()) {
                mMediaPlayer.start();
            }
            setVolumeFade(volume);
        }
    }

    public static void stop() {
        synchronized (sInstance) {
            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "stop()");
            }
            if (sInstance.mMediaPlayer == null) {
                return;
            }

            sInstance.mPendingMusicFileName = null;
            sInstance.mPendingContext = null;
            sInstance.mPendingVolume = 0;

            sInstance.setVolumeFade(0);
        }
    }

    private void dumpStatus() {
        if (Log.isLoggable(TAG, Log.VERBOSE)) {
            Log.v(TAG, "***********************");
            Log.v(TAG, "\tmedia player: " + mMediaPlayer);
            Log.v(TAG, "\tfile name: " + mMusicFileName);
            Log.v(TAG, "\tvolume: " + mCurrentVolume);
            Log.v(TAG, "\tpending file name: " + mPendingMusicFileName);
            Log.v(TAG, "***********************");
        }
    }

    private void setVolume(float volume) {
        synchronized (this) {
            if (Log.isLoggable(TAG, Log.DEBUG)) {
                dumpStatus();
                Log.d(TAG, "setVolume() " + volume);
            }
            mMediaPlayer.setVolume(volume, volume);
            mCurrentVolume = volume;
            if (mListener != null) {
                mListener.onStateChanged(mMusicFileName, mCurrentVolume);
            }
            if (mCurrentVolume == 0) {
                if (mPendingMusicFileName == null) {
                    mLastPlayedAt = mMediaPlayer.getCurrentPosition();
                } else {
                    mLastPlayedAt = 0;
                }
                mMediaPlayer.stop();
                mMediaPlayer.release();
                mMediaPlayer = null;
                if (mPendingMusicFileName != null) {
                    start(mPendingContext, mPendingMusicFileName, mPendingVolume);
                    mPendingMusicFileName = null;
                    mPendingContext = null;
                    mPendingVolume = 0;
                }
            }
        }
    }

    private void setVolumeFade(float volume) {
        synchronized (this) {
            if (Log.isLoggable(TAG, Log.DEBUG)) {
                dumpStatus();
                Log.d(TAG, "setVolumeFade() " + volume);
            }

            mHandler.removeMessages(FADE_VOLUME);
            Message message = mHandler.obtainMessage(FADE_VOLUME);
            Bundle data = new Bundle();
            data.putFloat(KEY_VOLUME, volume);
            message.setData(data);
            mHandler.sendMessageDelayed(message, 120);
        }
    }

    private class RequestHandler extends Handler {
        public RequestHandler() {
            super(Looper.getMainLooper());
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == FADE_VOLUME) {
                if (Log.isLoggable(TAG, Log.DEBUG)) {
                    dumpStatus();
                    Log.d(TAG, "handleMessage() " + msg);
                }

                Bundle data = msg.getData();
                float volume = data.getFloat(KEY_VOLUME);
                if (Math.abs(volume - mCurrentVolume) <= VOLUME_STEP) {
                    setVolume(volume);
                } else {
                    if (mCurrentVolume < volume) {
                        setVolume(mCurrentVolume + VOLUME_STEP);
                    } else {
                        setVolume(mCurrentVolume - VOLUME_STEP);
                    }
                    setVolumeFade(volume);
                }
            }
        }
    }

    public static class RequestManager {
        private BackgroundMusicPlayer mPlayer;
        private Context mContext;
        private String mFileName;
        private float mVolume;

        public RequestManager(BackgroundMusicPlayer player, Context context) {
            mPlayer = player;
            mContext = context;
            mVolume = 1.0f;
        }

        public void start() {
            if (mFileName == null) {
                throw new RuntimeException("File name is missing.");
            }
            mPlayer.start(mContext, mFileName, mVolume);
        }

        public RequestManager setFileName(String fileName) {
            mFileName = fileName;
            return this;
        }

        public RequestManager setVolume(float volume) {
            mVolume = volume;
            return this;
        }
    }

    public interface OnStateChangeListener {
        void onStateChanged(String fileName, float volume);
    }
}
