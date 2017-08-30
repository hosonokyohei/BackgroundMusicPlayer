package com.develga.backgroundmusicplayer.sample;

import android.os.Bundle;

import com.develga.backgroundmusicplayer.BackgroundMusicPlayer;

public class BackgroundMusic2Activity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Background Music 2");
    }

    @Override
    protected void onResume() {
        super.onResume();
        BackgroundMusicPlayer.with(this)
                .setFileName("b.mp3")
                .start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        BackgroundMusicPlayer.stop();
    }
}
