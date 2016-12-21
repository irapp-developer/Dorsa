package ir.dorsa.dorsaworld.service;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.List;

import ir.dorsa.dorsaworld.other.G;

public class Broadcast_WatchDog extends BroadcastReceiver {
    public Broadcast_WatchDog() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> RunningTask = mActivityManager.getRunningTasks(1);
        ActivityManager.RunningTaskInfo ar = RunningTask.get(0);
        
        Log.d(G.LOG_TAG, "foreground app is :" + ar.baseActivity.getPackageName()) ;
        Log.d(G.LOG_TAG, "foreground app pacage is :" + ar.baseActivity.getClassName()) ;

        
/*        android.os.Process.killProcess(76);

        ActivityManager.killBackgroundProcesses("");*/
        
//        CheckApps(context, ar.baseActivity.getPackageName());

    }
    
    
}
