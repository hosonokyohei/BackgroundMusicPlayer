package com.develga.backgroundmusicplayer.sample;

import android.os.Bundle;

import com.develga.backgroundmusicplayer.BackgroundMusicPlayer;

public class SmallBackgroundMusicActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Small Background Music");
    }

    @Override
    protected void onResume() {
        super.onResume();
        BackgroundMusicPlayer.with(this)
                .setFileName("a.mp3")
                .setVolume(0.6f)
                .start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        BackgroundMusicPlayer.stop();
    }
}
