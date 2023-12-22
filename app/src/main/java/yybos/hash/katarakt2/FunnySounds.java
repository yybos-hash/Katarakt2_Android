package yybos.hash.katarakt2;

import android.content.Context;
import android.media.MediaPlayer;

public class FunnySounds {

    private MediaPlayer mediaPlayer;

    public void playSound(Context context, int soundResourceId) {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }

        mediaPlayer = MediaPlayer.create(context, soundResourceId);
        mediaPlayer.start();
    }

    public void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
