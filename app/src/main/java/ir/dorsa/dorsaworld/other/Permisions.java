package ir.dorsa.dorsaworld.other;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import ir.dorsa.dorsaworld.Activity_WatchDog;
import ir.dorsa.dorsaworld.service.Service_Launcher;
import ir.dorsa.dorsaworld.service.Service_RunInBackground;
import ir.dorsa.dorsaworld.service.Service_StatusBar;
import ir.dorsa.dorsaworld.service.Service_WaitForRunn;
import ir.dorsa.dorsaworld.service.Service_Whatchdog;

/**
 * Created by mehdi on 2/11/16 AD.
 */
public class Permisions {

    public static void setDisablePermisions(Context mContext) {
        //------- disable check running apps ------------
        if(G.Watchdog.waitForBack!=null){
            G.Watchdog.waitForBack.finish();
            G.Watchdog.waitForBack=null;
        }
        mContext.stopService(new Intent(mContext, Service_WaitForRunn.class));
        mContext.stopService(new Intent(mContext, Service_Whatchdog.class));//stop service appWatcher
        mContext.stopService(new Intent(mContext, Service_RunInBackground.class));
        
        //------ disable lock volume --------------------
        if (G.Permiss.lockVolume != null) {
            mContext.getApplicationContext().getContentResolver().unregisterContentObserver(G.Permiss.lockVolume);
            G.Permiss.lockVolume = null;
        }
        //------- disable lock status bar ----------------
        mContext.getApplicationContext().stopService(new Intent(mContext, Service_StatusBar.class));
    }

    public static void setPermissionLauncher(Context mContext, JSONObject kidSettings) {
        try {
            //------- disable check running apps ------------
            if(G.Watchdog.waitForBack!=null){
                G.Watchdog.waitForBack.finish();
                G.Watchdog.waitForBack=null;
            }
            mContext.stopService(new Intent(mContext, Service_Whatchdog.class));
            mContext.stopService(new Intent(mContext, Service_RunInBackground.class));
            //------- disable lock status bar ----------------
            mContext.getApplicationContext().stopService(new Intent(mContext, Service_StatusBar.class));
            //------- Enable max volume ---------------------
            AudioManager am =(AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
            float percent=kidSettings.getInt("maxSound")/100.0f;
            
            int max_SoundSystem=(int)(am.getStreamMaxVolume(AudioManager.STREAM_SYSTEM)*percent);
            int max_SoundMusic=(int)(am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)*percent);
            int max_SoundRing=(int)(am.getStreamMaxVolume(AudioManager.STREAM_RING)*percent);
            
                AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
            Log.d(G.LOG_TAG,"Start Sound");
            if(audioManager.getRingerMode()!=AudioManager.RINGER_MODE_SILENT & audioManager.getRingerMode()!=AudioManager.RINGER_MODE_VIBRATE){
                Log.d(G.LOG_TAG,"Sound set");
                audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, max_SoundSystem, 0);
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, max_SoundMusic, 0);
                audioManager.setStreamVolume(AudioManager.STREAM_RING, max_SoundRing, 0); 
            }
               
            //------- Enable Lock volume ---------------------
            if (kidSettings.getString("lockVolume").equals("true")) {
                if (G.Permiss.lockVolume == null) {
                    if(audioManager.getRingerMode()==AudioManager.RINGER_MODE_SILENT || audioManager.getRingerMode()==AudioManager.RINGER_MODE_VIBRATE){
                        kidSettings.put("maxSound","-1");
                    }
                    G.Permiss.lockVolume = new SettingsContentObserver(mContext,kidSettings, new Handler());
                    mContext.getApplicationContext().getContentResolver().registerContentObserver(android.provider.Settings.System.CONTENT_URI, true, G.Permiss.lockVolume);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public static void setPermissionApps(Context mContext, JSONObject kidSettings,String packageName,String className,String AccessInternet) {
        try {
            //---------- status bar -----------
            if (kidSettings.getString("statusBar").equals("true")) {//enable status bar
                mContext.startService(new Intent(mContext, Service_StatusBar.class));
            }else{//disable status bar
                mContext.getApplicationContext().stopService(new Intent(mContext, Service_StatusBar.class));
            }
            //--------- start whatchdog apps Broadcast -----------
            Intent i=new Intent(mContext, Service_Whatchdog.class);
            i.putExtra("package", packageName);
            i.putExtra("className", className);
            i.putExtra("AccessInternet", AccessInternet);
            if (kidSettings.getString("screen").equals("true")) {
                i.putExtra("screen", "true");
            }else{
                i.putExtra("screen", "false");
            }
            if (kidSettings.getString("call").equals("false")) {
                i.putExtra("call", "true");
            }else{
                i.putExtra("call", "false");
            }
            mContext.startService(i);
            //------- start Activity wait for Back ----------
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                if(Func.isUsageStateEnable(mContext)){
                    G.Watchdog.isAppRuned=0;
                    Intent intnet= new Intent(mContext, Activity_WatchDog.class);
                    intnet.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intnet);
                }
            }else{
                G.Watchdog.isAppRuned=0;
                Intent intnet= new Intent(mContext, Activity_WatchDog.class);
                intnet.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intnet);
            }
           
            //------- start service wait for run app ----------
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){  
                if(Func.isUsageStateEnable(mContext)){
                    mContext.startService(new Intent(mContext, Service_WaitForRunn.class));
                }
            }else{
                mContext.startService(new Intent(mContext, Service_WaitForRunn.class));
            }
           
            //------- close launcher service ------------------
            mContext.getApplicationContext().stopService(new Intent(mContext, Service_Launcher.class));
            //------- start wait for run service ---------------
//            App.getContext().startService(new Intent(App.getContext(), Service_WaitForRunn.class));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void setPermissionBackgroundProcess(Context mContext, JSONObject kidSettings) {
        try {
            //------- Enable max volume ---------------------
            AudioManager am =(AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
            float percent=kidSettings.getInt("maxSound")/100.0f;

            int max_SoundSystem=(int)(am.getStreamMaxVolume(AudioManager.STREAM_SYSTEM)*percent);
            int max_SoundMusic=(int)(am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)*percent);
            int max_SoundRing=(int)(am.getStreamMaxVolume(AudioManager.STREAM_RING)*percent);

            AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
            audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, max_SoundSystem, 0);
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, max_SoundMusic, 0);
            audioManager.setStreamVolume(AudioManager.STREAM_RING, max_SoundRing, 0);
            //------- Enable Lock volume ---------------------
            if (kidSettings.getString("lockVolume").equals("true")) {
                if (G.Permiss.lockVolume == null) {
                    G.Permiss.lockVolume = new SettingsContentObserver(mContext,kidSettings, new Handler());
                    mContext.getApplicationContext().getContentResolver().registerContentObserver(android.provider.Settings.System.CONTENT_URI, true, G.Permiss.lockVolume);
                }
            }
            //------- disable check running apps ------------
            mContext.stopService(new Intent(mContext, Service_Whatchdog.class));
            //---------- status bar -----------
            if (kidSettings.getString("statusBar").equals("true")) {//enable status bar
                mContext.startService(new Intent(mContext, Service_StatusBar.class));
            }else{//disable status bar
                mContext.getApplicationContext().stopService(new Intent(mContext, Service_StatusBar.class));
            }
            //---------- start service watching apps -----------
//            mContext.startService(new Intent(mContext, Service_Watchdog_BGR.class));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
