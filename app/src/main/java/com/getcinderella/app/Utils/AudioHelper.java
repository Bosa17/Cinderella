package com.getcinderella.app.Utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Vibrator;
import android.util.Log;

import java.io.IOException;

import com.getcinderella.app.R;


public class AudioHelper {

    static final String LOG_TAG = AudioHelper.class.getSimpleName();

    private Context mContext;

    private MediaPlayer mPlayer;
    private Vibrator vibe;
    AudioManager audioManager ;


    public AudioHelper(Context context) {
        this.mContext = context.getApplicationContext();
        audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);

    }

    public void playRingtone() {
        int result = audioManager.requestAudioFocus(null,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);

        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            Log.d("AudioFocus", "Audio focus received");
        } else {
            Log.d("AudioFocus", "Audio focus NOT received");
        }
        vibe = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);

        long[] pattern = {0, 700, 1000};

        vibe.vibrate(pattern, 0);

        // Honour silent mode
        switch (audioManager.getRingerMode()) {
            case AudioManager.RINGER_MODE_NORMAL:
                mPlayer = new MediaPlayer();
                mPlayer.setAudioStreamType(AudioManager.STREAM_RING);

                try {
                    mPlayer.setDataSource(mContext,
                            Uri.parse("android.resource://" + mContext.getPackageName() + "/" + R.raw.bad_boy));
                    mPlayer.prepare();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Could not setup media player for ringtone");
                    mPlayer = null;
                    return;
                }
                mPlayer.setLooping(true);
                mPlayer.start();
                break;
        }
    }
    public void playMusic() {
        int result = audioManager.requestAudioFocus(null,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);

        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            Log.d("AudioFocus", "Audio focus received");
        } else {
            Log.d("AudioFocus", "Audio focus NOT received");
        }
        mPlayer = new MediaPlayer();
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mPlayer.setDataSource(mContext,
                    Uri.parse("android.resource://" + mContext.getPackageName() + "/" + R.raw.bad_boy));
            mPlayer.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Could not setup media player for ringtone");
            mPlayer = null;
            return;
        }
        mPlayer.setLooping(true);
        mPlayer.start();
    }

    public void stopRingtone() {
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
        if(vibe!=null)
            vibe.cancel();
    }

}
