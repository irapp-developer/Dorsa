package ir.dorsa.dorsaworld.other;

import android.content.Context;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.os.Handler;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by mehdi on 2/11/16 AD.
 */
public class SettingsContentObserver extends ContentObserver {
    private AudioManager audioManager;
    private Context mContext;
   /* private int currentVolumeSystem;
    private int currentVolumeMusic;
    private int currentVolumeRing;*/
    private JSONObject mKidSettings;

    public SettingsContentObserver(Context context,JSONObject kidSettings, Handler handler) {
        super(handler);
        mContext = context;
        mKidSettings=kidSettings;
        
        audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        
       
    }

    @Override
    public boolean deliverSelfNotifications() {
        return false;
    }

    @Override
    public void onChange(boolean selfChange) {
        
        AudioManager am =(AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        float percent= 0;
        try {
            int maxSOund=mKidSettings.getInt("maxSound");
            if(maxSOund!=-1) {
                percent = mKidSettings.getInt("maxSound") / 100.0f;


                int max_SoundSystem = (int) (am.getStreamMaxVolume(AudioManager.STREAM_SYSTEM) * percent);
                int max_SoundMusic = (int) (am.getStreamMaxVolume(AudioManager.STREAM_MUSIC) * percent);
                int max_SoundRing = (int) (am.getStreamMaxVolume(AudioManager.STREAM_RING) * percent);
                int max_SoundNotification = (int) (am.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION) * percent);

                AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
                audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, max_SoundSystem, 0);
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, max_SoundMusic, 0);
                audioManager.setStreamVolume(AudioManager.STREAM_RING, max_SoundRing, 0);
                audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, max_SoundNotification, 0);
            }else{
                am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        audioManager.adjustStreamVolume(AudioManager.STREAM_SYSTEM, AudioManager.ADJUST_SAME, 0);

    }
}
