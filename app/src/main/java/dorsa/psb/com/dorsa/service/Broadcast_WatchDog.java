package dorsa.psb.com.dorsa.service;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

import dorsa.psb.com.dorsa.other.FetchDb;
import dorsa.psb.com.dorsa.other.G;

public class Broadcast_WatchDog extends BroadcastReceiver {
    public Broadcast_WatchDog() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> RunningTask = mActivityManager.getRunningTasks(1);
        ActivityManager.RunningTaskInfo ar = RunningTask.get(0);
        
        Log.d(G.LOG_TAG, "foreground app is :" + ar.baseActivity.getPackageName()) ;


        
/*        android.os.Process.killProcess(76);

        ActivityManager.killBackgroundProcesses("");*/
        
        CheckApps(context, ar.baseActivity.getPackageName());

    }
    
    
    private void CheckApps(Context mContext,String runningPackageName) {
        try {
            G.selectedKid.joKid = FetchDb.getSelectedKid(mContext);
            if (G.kids_app.AllSystemApps.size() == 0) {//if it was not loadded before
                G.kids_app.AllSystemApps = installedApps(mContext);
            }
            for (ResolveInfo ri : G.kids_app.AllSystemApps) {
                G.kids_app.AllAppResolver.add(ri);
            }
            JSONArray JArrKidApps = FetchDb.getKidApps(mContext, G.selectedKid.joKid.getString("ID"));
            if (G.kids_app.KidAppResolver.size() == 0) {// if it was not loaded before
                for (int i = 0; i < JArrKidApps.length(); i++) {
                    G.kids_app.KidAppResolver.add(JArrKidApps.getJSONObject(i).getString("packageName"));
                }
                G.kids_app.KidAppResolver.add("dorsa.psb.com.dorsa");
            }
            for (String pkgName : G.kids_app.KidAppResolver) {//if it was added before ,remove
                for (int i = 0; i < G.kids_app.AllAppResolver.size(); i++) {
                    if (G.kids_app.AllAppResolver.get(i).activityInfo.applicationInfo.packageName
                            .equals(pkgName)
                            ) {
                        G.kids_app.AllAppResolver.remove(i);
                        break;
                    }
                }
            }
            
            
            boolean isAccepted=false;
            for (String pkgName : G.kids_app.KidAppResolver) {//if it was added before ,remove
            if(runningPackageName.contains(pkgName) & !runningPackageName.equals("dorsa.psb.com.dorsa")){
                isAccepted=true;
                break;
            }
            }
            if(!isAccepted){
                Intent intent=new Intent(mContext,dorsa.psb.com.dorsa.launcher.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
            
            
            
            
        }catch (JSONException ex){
            
        }
            
    }
    private List<ResolveInfo> installedApps(Context mContext) {
        final Intent main_intent = new Intent(Intent.ACTION_MAIN, null);
        main_intent.addCategory(Intent.CATEGORY_LAUNCHER);
        return mContext.getPackageManager().queryIntentActivities(main_intent, 0);
    }
}
