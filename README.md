# BackgroundMusicPlayer

A background music player library for an Android project with multiple Activities.

- keeps playing on switching between your Activities
- stops playing when your app goes to the background
- resumes playing when your app comes back to the foreground
- smooth volume control


複数ActivityをもつAndroidアプリ向け、BGMプレイヤーライブラリです。

- Activity間を遷移しても鳴り続けます。
- アプリをバックグラウンドに回すと止まります。
- アプリをフォアグラウンドに戻すと再開します。
- ボリューム変化や楽曲変更の際にいい感じのフェードがかかります。

# Usage

Place your music files into `/assets` folder.

Add background music configurations in your Activities:

```java
public class BackgroundMusicActivity extends Activity {

    @Override
    protected void onResume() {
        super.onResume();
        BackgroundMusicPlayer.with(this)
                .setFileName("a.mp3")
                .setVolume(1.0f)
                .start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        BackgroundMusicPlayer.stop();
    }
}
```

# Sample Project

https://github.com/hosonokyohei/BackgroundMusicPlayer/tree/master/sample

---

# Sample Sounds

`a.mp3`
http://freesound.org/people/bronxio/sounds/242969/

`b.mp3`
http://freesound.org/people/Armjan88/sounds/234809/
