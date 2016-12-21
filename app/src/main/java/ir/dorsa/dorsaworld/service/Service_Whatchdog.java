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
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;
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
import ir.dorsa.dorsaworld.other.HomeWatcher;

/**
 * Created by mehdi on 2/20/16 AD.
 */
public class Service_Whatchdog extends Service {
    private HomeWatcher mHomeWatcher;
    private boolean isAppWatcherRun = false;
    private boolean isAccesAppRun = false;
    private String AppPackageName = "";
    private String className="";
    private Broadcast_Network_Change networkBroadcast;
    private Broadcast_Recieve_Call receiveCallBroadcast;
    private PowerManager.WakeLock mWakeLock;
    private Timer mTimer = new Timer();


    private int secoundCounter = 0;
    private boolean isDailyLimitEnable = false;
    private int dailyLimitUsedSecound = 0;
    private int dailyLimitPerDay = 0;
    
    private int weeklyCheckCounter=0;
    private boolean isWeekEnable=false;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return (null);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(G.LOG_TAG, "watchDog Started");
        AppPackageName = intent.getStringExtra("package");
        className = intent.getStringExtra("className");
        String AccessInternet = intent.getStringExtra("AccessInternet");
        String ScreenMode = intent.getStringExtra("screen");
        String answerCall = intent.getStringExtra("call");


        noti();
        homeWatcher();
        Appwatcher();
        setDailyLimit();
        
        G.Permiss.DEFAULT_WIFI= Func.isWifiConnected(App.getContext());
        G.Permiss.DEFAULT_DATA=Func.isDataConnected(App.getContext());
        //--------- internet access --------------------------
        if(AccessInternet.equals("false")){
            Func.setInternetEnabled(App.getContext(), false);
        }else{
//            Func.setInternetEnabled(App.getContext(), true);
        }
        if (AccessInternet.equals("false")) NetworkWatcher();
        if (ScreenMode.equals("true")) setScreen();
        incomingCallWatcher(answerCall);//if parrent add access
        try {
            if(FetchDb.isSchedualWeekEnable(App.getContext(),G.selectedKid.joKid.getString("ID"),AppPackageName,className)!=0)isWeekEnable=true;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        G.isServiceWatchDog=true;
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(G.LOG_TAG, "watchDog Destroyed");
        //----- stop app watcher ------------------
        isAppWatcherRun = false;
        mTimer.cancel();
        //----- stop home watcher -----------------
        mHomeWatcher.stopWatch();
        stopForeground(true);
        //---- Stop network watcher ---------------
        if (networkBroadcast != null) getApplicationContext().unregisterReceiver(networkBroadcast);
        Func.setDataEnabled(App.getContext(),G.Permiss.DEFAULT_DATA);
        Func.setWifiEnabled(App.getContext(), G.Permiss.DEFAULT_WIFI);
      
        //---- Stop screen on ---------------
        if (mWakeLock != null) mWakeLock.release();
        //------ stop incomming call watcher
        if (receiveCallBroadcast != null)getApplicationContext().unregisterReceiver(receiveCallBroadcast);
        Service_Whatchdog.this.stopForeground(true);
        Service_Whatchdog.this.stopSelf();
        //------ set usedDailyLimit -----------------
        if (isDailyLimitEnable) {
           
            int totalUsedSecound=dailyLimitUsedSecound + secoundCounter;
           
            try {
                FetchDb.setKidDailyLimitLastUsed(App.getContext(), G.selectedKid.joKid.getString("ID"), AppPackageName,className, "" + Calendar.getInstance().get(Calendar.DAY_OF_WEEK), "" +totalUsedSecound);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        G.isServiceWatchDog=false;
        super.onDestroy();
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
        Notification note = builder.build();
        note.flags |= Notification.FLAG_NO_CLEAR;


        startForeground(1337, note);
    }

    private void Appwatcher() {
        isAppWatcherRun = true;

//        new Timer().schedule(new TimerAppwatcher(), 1000);
        mTimer.schedule(new TimerAppwatcher(), 1000);
        
       /* PackageManager pm = App.getContext().getPackageManager();
        Intent launchIntent = pm.getLaunchIntentForPackage(AppPackageName);
        App.getContext().startActivity(launchIntent);*/

    }

    private void homeWatcher() {
        mHomeWatcher = new HomeWatcher(this);
        mHomeWatcher.setOnHomePressedListener(new HomeWatcher.OnHomePressedListener() {
            @Override
            public void onHomePressed() {
                // do something here...
                Log.d("hg", "Home pressed");
                G.Watchdog.isAppRuned = 2;
                if (G.Watchdog.waitForBack != null) {
                    G.Watchdog.waitForBack.finish();
                    G.Watchdog.waitForBack = null;
                }

                startService(new Intent(App.getContext(), Service_Launcher.class));
                Service_Whatchdog.this.stopForeground(true);
                Service_Whatchdog.this.stopSelf();
            }

            @Override
            public void onHomeLongPressed() {
                Log.d(G.LOG_TAG, "Recent apps ruuning");
            }
        });
        mHomeWatcher.startWatch();

    }

    private void NetworkWatcher() {
        //------------- set networkstate change -------------
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        networkBroadcast = new Broadcast_Network_Change();
        getApplicationContext().registerReceiver(networkBroadcast, intentFilter);
    }

    private void incomingCallWatcher(String mode) {
        final IntentFilter intentFilter = new IntentFilter("android.intent.action.PHONE_STATE");
        receiveCallBroadcast = new Broadcast_Recieve_Call("WD-" + mode);
        getApplicationContext().registerReceiver(receiveCallBroadcast, intentFilter);
    }

    private void setScreen() {
        final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        this.mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
        this.mWakeLock.acquire();
    }

    private void setDailyLimit() {
        try {
            JSONObject dailyJo = FetchDb.getKidDailyLimit(App.getContext(), G.selectedKid.joKid.getString("ID"), AppPackageName,className);
         if(dailyJo!=null) {
             isDailyLimitEnable = dailyJo.getBoolean("enable");
             dailyLimitUsedSecound = dailyJo.getInt("usedSecound");
             dailyLimitPerDay = dailyJo.getInt("limitPerDay")*60*60;

             Log.d(G.LOG_TAG,"DL get daily limit:"+dailyLimitPerDay);
             Log.d(G.LOG_TAG,"DL get daily secoundCounter:"+secoundCounter);
             Log.d(G.LOG_TAG,"DL get daily dailyLimitUsedSecound:"+dailyLimitUsedSecound);
             int totalUsetSecound=dailyLimitUsedSecound + secoundCounter;
             Log.d(G.LOG_TAG,"DL get daily totalUsetSecound:"+totalUsetSecound);
         }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    class TimerAppwatcher extends TimerTask {

        @Override
        public void run() {
            //------------- check daily limit time -------------------------------------------------
            if(isDailyLimitEnable){
                if((dailyLimitUsedSecound+secoundCounter)>=dailyLimitPerDay){//times up
                    G.Watchdog.isAppRuned = 2;
                    if (G.Watchdog.waitForBack != null) {
                        G.Watchdog.waitForBack.finish();
                        G.Watchdog.waitForBack = null;
                    }

                    startService(new Intent(App.getContext(), Service_Launcher.class));
                    Service_Whatchdog.this.stopForeground(true);
                    Service_Whatchdog.this.stopSelf();
                    return;
                }else{
                    secoundCounter+=1;
                }
            }
            //------------- check weekly limit time -------------------------------------------------
            if(isWeekEnable) {
                if(weeklyCheckCounter>=30) {
                    weeklyCheckCounter=0;
                    try {
                        int isAllowToUseWeeklyApp = FetchDb.isSchedualWeekEnable(App.getContext(), G.selectedKid.joKid.getString("ID"), AppPackageName,className);
                        if (isAllowToUseWeeklyApp == -1) {//its iligal
                            G.Watchdog.isAppRuned = 2;
                            if (G.Watchdog.waitForBack != null) {
                                G.Watchdog.waitForBack.finish();
                                G.Watchdog.waitForBack = null;
                            }

                            startService(new Intent(App.getContext(), Service_Launcher.class));
                            Service_Whatchdog.this.stopForeground(true);
                            Service_Whatchdog.this.stopSelf();
                            return;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    weeklyCheckCounter+=1;
                }
            }
            //--------------------------------------------------------------------------------------
            if (isAppWatcherRun) {//repeat timer
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
                if (isAccesAppRun) {
                    CheckApps(App.getContext(), packageName);
                } else if (packageName.equals(AppPackageName)) {
                    isAccesAppRun = true;
                    //stop service waiting
                    App.getContext().stopService(new Intent(App.getContext(), Service_WaitForRunn.class));
                    G.Watchdog.isAppRuned = 1;
                }

                try {
                    new Timer().schedule(new TimerAppwatcher(), 1000);
                } catch (Exception ex) {

                }
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
                    if (G.kids_app.AllAppResolver.get(i).activityInfo.applicationInfo.packageName
                            .equals(pkgName)
                            ) {
                        G.kids_app.AllAppResolver.remove(i);
                        break;
                    }
                }
            }


            boolean isAccepted = false;
            if(G.kids_app.KidAppResolver.size()>0) {
                for (JSONObject jo : G.kids_app.KidAppResolver) {//if it was added before ,remove
                    String pkgName = jo.getString("packageName");


                    if (runningPackageName.contains(pkgName)
                            || runningPackageName.equals(getPackageName())
                            ) {
                        isAccepted = true;
                        break;
                    } else if (kidSettings.getString("call").equals("false") &
                            runningPackageName.equals("com.android.phone")) {
                        isAccepted = true;
                        break;
                    }
                }
            }else{
                if (runningPackageName.equals(getPackageName())) {
                    isAccepted = true;
                } else if (kidSettings.getString("call").equals("false") &
                        runningPackageName.equals("com.android.phone")) {
                    isAccepted = true;
                }
            }

            if (!isAccepted) {
                Log.d(G.LOG_TAG, "WD wants to kill " + runningPackageName);

                G.Watchdog.isAppRuned = 2;
                if (G.Watchdog.waitForBack != null) {
                    G.Watchdog.waitForBack.finish();
                    G.Watchdog.waitForBack = null;
                }

                startService(new Intent(App.getContext(), Service_Launcher.class));
                Service_Whatchdog.this.stopForeground(true);
                Service_Whatchdog.this.stopSelf();

                ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                am.killBackgroundProcesses(runningPackageName);


            }


        } catch (JSONException ex) {

        }

    }

    static class getRunningTasks {
        public static final int AID_APP = 10000;

        /**
         * offset for uid ranges for each user
         */
        public static final int AID_USER = 100000;

        public static String getForegroundApp() {
            File[] files = new File("/proc").listFiles();
            int lowestOomScore = Integer.MAX_VALUE;
            String foregroundProcess = null;

            for (File file : files) {
                if (!file.isDirectory()) {
                    continue;
                }

                int pid;
                try {
                    pid = Integer.parseInt(file.getName());
                } catch (NumberFormatException e) {
                    continue;
                }

                try {
                    String cgroup = read(String.format("/proc/%d/cgroup", pid));

                    String[] lines = cgroup.split("\n");

                    if (lines.length != 2) {
                        continue;
                    }

                    String cpuSubsystem = lines[0];
                    String cpuaccctSubsystem = lines[1];

                    if (!cpuaccctSubsystem.endsWith(Integer.toString(pid))) {
                        // not an application process
                        continue;
                    }

                    if (cpuSubsystem.endsWith("bg_non_interactive")) {
                        // background policy
                        continue;
                    }

                    String cmdline = read(String.format("/proc/%d/cmdline", pid));

                    if (cmdline.contains("com.android.systemui")) {
                        continue;
                    }

                    int uid = Integer.parseInt(
                            cpuaccctSubsystem.split(":")[2].split("/")[1].replace("uid_", ""));
                    if (uid >= 1000 && uid <= 1038) {
                        // system process
                        continue;
                    }

                    int appId = uid - AID_APP;
                    int userId = 0;
                    // loop until we get the correct user id.
                    // 100000 is the offset for each user.
                    while (appId > AID_USER) {
                        appId -= AID_USER;
                        userId++;
                    }

                    if (appId < 0) {
                        continue;
                    }

                    // u{user_id}_a{app_id} is used on API 17+ for multiple user account support.
                    // String uidName = String.format("u%d_a%d", userId, appId);

                    File oomScoreAdj = new File(String.format("/proc/%d/oom_score_adj", pid));
                    if (oomScoreAdj.canRead()) {
                        int oomAdj = Integer.parseInt(read(oomScoreAdj.getAbsolutePath()));
                        if (oomAdj != 0) {
                            continue;
                        }
                    }

                    int oomscore = Integer.parseInt(read(String.format("/proc/%d/oom_score", pid)));
                    if (oomscore < lowestOomScore) {
                        lowestOomScore = oomscore;
                        foregroundProcess = cmdline;
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return foregroundProcess;
        }

        private static String read(String path) throws IOException {
            StringBuilder output = new StringBuilder();
            BufferedReader reader = new BufferedReader(new FileReader(path));
            output.append(reader.readLine());
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                output.append('\n').append(line);
            }
            reader.close();
            return output.toString();
        }
    }

    private List<ResolveInfo> installedApps(Context mContext) {
        final Intent main_intent = new Intent(Intent.ACTION_MAIN, null);
        main_intent.addCategory(Intent.CATEGORY_LAUNCHER);
        return mContext.getPackageManager().queryIntentActivities(main_intent, 0);
    }


}
