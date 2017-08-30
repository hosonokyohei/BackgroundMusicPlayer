package com.develga.backgroundmusicplayer.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.develga.backgroundmusicplayer.BackgroundMusicPlayer;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        findViewById(R.id.open_background_music_activity_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openActivity(BackgroundMusicActivity.class);
            }
        });
        findViewById(R.id.open_small_background_music_activity_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openActivity(SmallBackgroundMusicActivity.class);
            }
        });
        findViewById(R.id.open_no_background_music_activity_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openActivity(NoBackgroundMusicActivity.class);
            }
        });
        findViewById(R.id.open_background_music_2_activity_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openActivity(BackgroundMusic2Activity.class);
            }
        });
    }

    private void openActivity(Class<? extends Activity> activityClass) {
        Intent intent = new Intent(this, activityClass);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        final TextView statusTextView = (TextView) findViewById(R.id.status_textview);
        BackgroundMusicPlayer.setOnStateChangeListener(new BackgroundMusicPlayer.OnStateChangeListener() {
            @Override
            public void onStateChanged(String fileName, float volume) {
                statusTextView.setText(fileName + " volume:" + String.format("%.1f", volume));
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        BackgroundMusicPlayer.setOnStateChangeListener(null);
    }
}
