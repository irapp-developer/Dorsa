package dorsa.psb.com.dorsa.other;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Handler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import dorsa.psb.com.dorsa.service.Broadcast_WatchDog;
import dorsa.psb.com.dorsa.service.Service_Launcher;
import dorsa.psb.com.dorsa.service.Service_StatusBar;

/**
 * Created by mehdi on 2/11/16 AD.
 */
public class Permisions {

    public static void setParrentPermisions(Context mContext) {
        //------ disable lock volume --------------------
        if (G.Permiss.lockVolume != null) {
            mContext.getApplicationContext().getContentResolver().unregisterContentObserver(G.Permiss.lockVolume);
            G.Permiss.lockVolume = null;
        }
        //------- disable check running apps ------------
        Intent intent = new Intent(mContext.getApplicationContext().getApplicationContext(), Broadcast_WatchDog.class);
        PendingIntent pi = PendingIntent.getBroadcast(mContext.getApplicationContext().getApplicationContext(), 0, intent, 0);
        pi.cancel();
        //------- disable lock status bar ----------------
        mContext.getApplicationContext().stopService(new Intent(mContext, Service_StatusBar.class));


//        mContext.startService(new Intent(mContext, Service_StatusBar.class));
//        mContext.getApplicationContext().getContentResolver().registerContentObserver(android.provider.Settings.System.CONTENT_URI, true, new Permisions.SettingsContentObserver(mContext, new Handler()));
        //getApplicationContext().getContentResolver().unregisterContentObserver(mSettingsContentObserver);

    }

    public static void setPermissionLauncher(Context mContext, JSONObject kidSettings) {
        try {


            //------- disable check running apps ------------
            Intent intent = new Intent(mContext.getApplicationContext().getApplicationContext(), Broadcast_WatchDog.class);
            PendingIntent pi = PendingIntent.getBroadcast(mContext.getApplicationContext().getApplicationContext(), 0, intent, 0);
            pi.cancel();
            //------- disable lock status bar ----------------
            mContext.getApplicationContext().stopService(new Intent(mContext, Service_StatusBar.class));

            //------- Enable max volume ---------------------
            if (kidSettings.getString("maxSound").equals("true")) {
                AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
                audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, 100, 0);
            }
            //------- Enable Lock volume ---------------------
            if (kidSettings.getString("lockVolume").equals("true")) {
                if (G.Permiss.lockVolume == null) {
                    G.Permiss.lockVolume = new SettingsContentObserver(mContext, new Handler());
                    mContext.getApplicationContext().getContentResolver().registerContentObserver(android.provider.Settings.System.CONTENT_URI, true, G.Permiss.lockVolume);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public static void setPermissionApps(Context mContext, JSONObject kidSettings) {
        try {
            //---------- status bar -----------
            if (kidSettings.getString("statusBar").equals("true")) {//enable status bar
                mContext.startService(new Intent(mContext, Service_StatusBar.class));
            }else{//disable status bar
                mContext.getApplicationContext().stopService(new Intent(mContext, Service_StatusBar.class));
            }
            //--------- start whatchdog apps Broadcast -----------
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            AlarmManager am=(AlarmManager)mContext.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(mContext.getApplicationContext().getApplicationContext(), Broadcast_WatchDog.class);
            PendingIntent pi = PendingIntent.getBroadcast(mContext, 0, intent,0);
            am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() , 1000, pi);
            //------- close launcher service ------------------
            mContext.getApplicationContext().stopService(new Intent(mContext, Service_Launcher.class));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

  

}
