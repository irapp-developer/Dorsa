package ir.dorsa.dorsaworld.service;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ResolveInfo;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import ir.dorsa.dorsaworld.App;
import ir.dorsa.dorsaworld.R;
import ir.dorsa.dorsaworld.other.FetchDb;
import ir.dorsa.dorsaworld.other.Func;
import ir.dorsa.dorsaworld.other.G;
import ir.dorsa.dorsaworld.other.Permisions;

public class Service_RunInBackground extends Service {
    private Timer mTimer=new Timer();
    private Broadcast_Recieve_Call receiveCallBroadcast;
    
    public Service_RunInBackground() {
    }

    @Override
    public void onDestroy() {
        G.isServiceRunInBackground=false;
        mTimer.cancel();
        //------ stop incomming call watcher
        if (receiveCallBroadcast != null)getApplicationContext().unregisterReceiver(receiveCallBroadcast);
        Permisions.setDisablePermisions(App.getContext());
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return (null);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String answerCall = intent.getStringExtra("call");
        
        noti();
        //load kids settings ----------
        try {
            G.selectedKid.joKid = FetchDb.getSelectedKid(this);
            G.selectedKid.joKid.getString("settings");
            JSONObject kidSettings= new JSONObject(G.selectedKid.joKid.getString("settings"));
             Permisions.setPermissionBackgroundProcess(App.getContext(),kidSettings);

           
            noti();
            G.isServiceRunInBackground=true;
            mTimer.schedule(new timerTask(), 1000);
            incomingCallWatcher(answerCall);
            
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return START_NOT_STICKY;
    }

   
    private void noti() {
        // Use NotificationCompat.Builder to set up our notification.
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        //icon appears in device notification bar and right hand corner of notification
        builder.setSmallIcon(R.mipmap.ic_launcher_kid);

        // This intent is fired when notification is clicked
        Intent intent = new Intent(this, ir.dorsa.dorsaworld.launcher.class);

//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pIntent = PendingIntent.getBroadcast(this, 234, intent, 0);

        // Set the intent that will fire when the user taps the notification.
        builder.setContentIntent(pIntent);

        // Large icon appears on the left of the notification
        builder.setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher_kid));

        // Content title, which appears in large type at the top of the notification
        builder.setContentTitle(this.getResources().getString(R.string.noti_titleـkid));

        // Content text, which appears in smaller text below the title
        builder.setContentText(this.getResources().getString(R.string.noti_descـkid));

        // The subtext, which appears under the text on newer devices.
        // This will show-up in the devices with Android 4.2 and above only
        Notification note =builder.build();
        note.flags |= Notification.FLAG_NO_CLEAR;


        startForeground(6574, note);
    }

    private void incomingCallWatcher(String mode) {
        final IntentFilter intentFilter = new IntentFilter("android.intent.action.PHONE_STATE");
        receiveCallBroadcast = new Broadcast_Recieve_Call("WD-" + mode);
        getApplicationContext().registerReceiver(receiveCallBroadcast, intentFilter);
    }
    
    class timerTask extends TimerTask {

        @Override
        public void run() {
            if(G.isServiceRunInBackground){

                String packageName = "";
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {

                    UsageStatsManager usm = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
                    long time = System.currentTimeMillis();
                    List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,
                            time - 1000 * 1000, time);
                    if (appList != null && appList.size() > 0) {
                        SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
                        for (UsageStats usageStats : appList) {
                            mySortedMap.put(usageStats.getLastTimeUsed(),
                                    usageStats);
                        }
                        if (mySortedMap != null && !mySortedMap.isEmpty()) {
                            packageName = mySortedMap.get(
                                    mySortedMap.lastKey()).getPackageName();
                       /* Log.d(G.LOG_TAG,"ED new package is :"+mySortedMap.get(
                                mySortedMap.lastKey()).getPackageName());*/

                        }
                    }

                } else {
                    ActivityManager mActivityManager = (ActivityManager) App.getContext().getSystemService(Context.ACTIVITY_SERVICE);
                    List<ActivityManager.RunningTaskInfo> RunningTask = mActivityManager.getRunningTasks(1);
                    ActivityManager.RunningTaskInfo ar = RunningTask.get(0);
                    packageName = ar.baseActivity.getPackageName();
                }
                if(packageName.length()>0) {
                    try {
                        CheckApps(App.getContext(), packageName);
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
                }

                mTimer=new Timer();
                mTimer.schedule(new timerTask(), 1000);
            }

        }
    }
    
    private void CheckApps(Context mContext, String runningPackageName) {

        try {
            G.selectedKid.joKid = FetchDb.getSelectedKid(mContext);
            JSONObject kidSettings = new JSONObject(G.selectedKid.joKid.getString("settings"));
            if (G.kids_app.AllSystemApps.size() == 0) {//if it was not loadded before
                G.kids_app.AllSystemApps = installedApps(mContext);
            }
            for (ResolveInfo ri : G.kids_app.AllSystemApps) {
                G.kids_app.AllAppResolver.add(ri);
            }
            JSONArray JArrKidApps = FetchDb.getKidApps(mContext, G.selectedKid.joKid.getString("ID"));
            if (G.kids_app.KidAppResolver.size() == 0) {// if it was not loaded before
                for (int i = 0; i < JArrKidApps.length(); i++) {
                    G.kids_app.KidAppResolver.add(JArrKidApps.getJSONObject(i));
                }
            }
            for (JSONObject jo : G.kids_app.KidAppResolver) {//if it was added before ,remove all list
                String pkgName = jo.getString("packageName");
                for (int i = 0; i < G.kids_app.AllAppResolver.size(); i++) {
                    if (pkgName.equals(G.kids_app.AllAppResolver.get(i).activityInfo.applicationInfo.packageName)) {
                        G.kids_app.AllAppResolver.remove(i);
                        break;
                    }
                }
            }


            boolean isAccepted = false;
            boolean accessInternet=false;
            for (JSONObject jo : G.kids_app.KidAppResolver) {//if it was added before ,remove
                String pkgName = jo.getString("packageName");


                if (runningPackageName.contains(pkgName)
                        || runningPackageName.equals(getPackageName())
                        ) {
                    accessInternet=jo.getBoolean("AccessInternet");
                    isAccepted = true;

                    break;
                } else if (kidSettings.getString("call").equals("false") &
                        runningPackageName.equals("com.android.phone")) {
                    isAccepted = true;
                    accessInternet=true;
                    break;
                }
            }

            if (isAccepted) {//set limit internet connections
                if(accessInternet & (!Func.isWifiConnected(App.getContext()) || !Func.isDataConnected(App.getContext()))){
                    Func.setInternetEnabled(App.getContext(), true);
                }else if (Func.isWifiConnected(App.getContext()) || Func.isDataConnected(App.getContext())){
                    Func.setInternetEnabled(App.getContext(), false);
                }
            }else{//set default internet connection
                if(Func.isDataConnected(App.getContext())!=G.Permiss.DEFAULT_DATA)Func.setDataEnabled(App.getContext(),G.Permiss.DEFAULT_DATA);
                if(Func.isWifiConnected(App.getContext())!=G.Permiss.DEFAULT_WIFI)Func.setWifiEnabled(App.getContext(), G.Permiss.DEFAULT_WIFI);
            }


        } catch (JSONException ex) {

        }

    }

    private List<ResolveInfo> installedApps(Context mContext) {
        final Intent main_intent = new Intent(Intent.ACTION_MAIN, null);
        main_intent.addCategory(Intent.CATEGORY_LAUNCHER);
        return mContext.getPackageManager().queryIntentActivities(main_intent, 0);
    }
}
