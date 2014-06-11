
package ch.szederkenyi.heidi.media;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;

import ch.szederkenyi.heidi.R;
import ch.szederkenyi.heidi.StaticContextApplication;

import java.util.EnumMap;

public class SoundManager {
    public enum MEDIA {
        LANG_SELECTION
    }

    private static final EnumMap<MEDIA, Integer> sSoundMap = new EnumMap<SoundManager.MEDIA, Integer>(
            MEDIA.class);
    static {
        sSoundMap.put(MEDIA.LANG_SELECTION, Integer.valueOf(R.raw.heidi_game_einleitung));
    }

    public static void play(MEDIA media) {
        if (isPhoneRinger()) {
            playSound(media);
        }
    }

    private static boolean isPhoneRinger() {
        final Context context = StaticContextApplication.getAppContext();
        AudioManager am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        return am.getRingerMode() == AudioManager.RINGER_MODE_NORMAL;
    }

    private static void playSound(MEDIA media) {
        final Integer resObject = sSoundMap.get(media);
        final Activity activity = StaticContextApplication.getCurrentActivity();
        final MediaPlayer player = MediaPlayer.create(activity, resObject.intValue());
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }
        });
        player.start();
    }
}
