package dorsa.psb.com.dorsa.other;

import android.content.Context;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.os.Handler;
import android.util.Log;

/**
 * Created by mehdi on 2/11/16 AD.
 */
public class SettingsContentObserver extends ContentObserver {
    private AudioManager audioManager;
    private Context mContext;
    private int currentVolume;

    public SettingsContentObserver(Context context, Handler handler) {
        super(handler);
        mContext = context;
        audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        currentVolume= audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);
       
    }

    @Override
    public boolean deliverSelfNotifications() {
        return false;
    }

    @Override
    public void onChange(boolean selfChange) {
      
        
        AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM,currentVolume,0);
        Log.d(G.LOG_TAG, "last volume is :" + currentVolume);
        Log.d(G.LOG_TAG,"current volume is :"+audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM));
//        audioManager.adjustStreamVolume(AudioManager.STREAM_SYSTEM, AudioManager.ADJUST_SAME, 0);

    }
}
