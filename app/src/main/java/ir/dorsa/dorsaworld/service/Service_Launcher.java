package ir.dorsa.dorsaworld.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import ir.dorsa.dorsaworld.Activity_GetPattern;
import ir.dorsa.dorsaworld.Activity_Get_Password;
import ir.dorsa.dorsaworld.App;
import ir.dorsa.dorsaworld.R;
import ir.dorsa.dorsaworld.adapter.Adapter_Launcher_Fragments_Apps;
import ir.dorsa.dorsaworld.other.CalTool;
import ir.dorsa.dorsaworld.other.FetchDb;
import ir.dorsa.dorsaworld.other.Func;
import ir.dorsa.dorsaworld.other.G;
import ir.dorsa.dorsaworld.other.Permisions;

public class Service_Launcher extends Service {
    private View pView;
    private WindowManager manager;
    private Intent mInent;
    private broadcastTimer timerBroadcast;
    private Broadcast_Airplane_Mode airplaneModeBroadcast;
    private Broadcast_Recieve_Call receiveCallBroadcast;
    private PowerManager.WakeLock mWakeLock;
    private TextView closeText;
    private boolean closePressed=false;
    
    private Handler mHandler=new Handler();

    private Tracker mTracker;
    
    public Service_Launcher() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        mInent = intent;
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        noti();
        return START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
//        Thread.setDefaultUncaughtExceptionHandler(new UnCaughtException(this));
       
        //load kids settings ----------
//        int a=5/0;
        


        manager = ((WindowManager) getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE));


        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_FULLSCREEN|
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|
                        
                        // this is to enable the notification to recieve touch events
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |

                        // Draws over status bar
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.RGBA_8888);
    
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.TOP;
        LayoutInflater inflate = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        pView = inflate.inflate(R.layout.launcher_layout, null);
       
        manager.addView(pView, params);
       

//        setupViews();
        G.selectedKid.joKid=FetchDb.getSelectedKid(App.getContext());
        setupViewsViewPager();
//        homeWatcher();
        
        try {
            G.selectedKid.joKid = FetchDb.getSelectedKid(this);
            JSONObject kidSettings = new JSONObject(G.selectedKid.joKid.getString("settings"));
            Permisions.setPermissionLauncher(this, kidSettings);
            if (kidSettings.getString("screen").equals("true")) {
                setScreen();
            }
            //if is access to answerCalls
            watchIncommingCalls(kidSettings.getBoolean("call"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        
        if (G.Watchdog.launcher != null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    G.Watchdog.launcher.finish();
                }
            }, 1000);

        }
        //--------------

        App application = (App) getApplication();
        mTracker = application.getDefaultTracker();
        mTracker.enableAdvertisingIdCollection(true);
        
        mTracker.setScreenName("Image~دنیای درسا");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        G.isServiceLaucerRun=true;
    }

    @Override
    public void onDestroy() {
       
           
            stopForeground(true);
            if (pView != null) {
                manager.removeView(pView);
                Intent intent = new Intent(getApplicationContext().getApplicationContext(), broadcastTimer.class);
                PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext().getApplicationContext(), 124, intent, 0);
//                getApplicationContext().unregisterReceiver(timerBroadcast);
                pi.cancel();
            }
            //---- Stop screen on ---------------
            if (mWakeLock != null) mWakeLock.release();
            if (receiveCallBroadcast != null)
                getApplicationContext().unregisterReceiver(receiveCallBroadcast);

        G.isServiceLaucerRun=false;
        
        super.onDestroy();
        
    }

    private void noti() {
        // Use NotificationCompat.Builder to set up our notification.
        NotificationCompat.Builder builder = new NotificationCompat.Builder(App.getContext());

        //icon appears in device notification bar and right hand corner of notification
        builder.setSmallIcon(R.mipmap.ic_launcher_kid);

        // This intent is fired when notification is clicked
        Intent intent = new Intent(App.getContext(), ir.dorsa.dorsaworld.launcher.class);

//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pIntent = PendingIntent.getBroadcast(App.getContext(), 234, intent, 0);

        // Set the intent that will fire when the user taps the notification.
        builder.setContentIntent(pIntent);

        // Large icon appears on the left of the notification
        builder.setLargeIcon(BitmapFactory.decodeResource(App.getContext().getResources(), R.mipmap.ic_launcher_kid));

        // Content title, which appears in large type at the top of the notification
        builder.setContentTitle(this.getResources().getString(R.string.noti_titleـkid));

        // Content text, which appears in smaller text below the title
        builder.setContentText(this.getResources().getString(R.string.noti_descـkid));

        // The subtext, which appears under the text on newer devices.
        // This will show-up in the devices with Android 4.2 and above only
        Notification note = builder.build();
        note.flags |= Notification.FLAG_NO_CLEAR;


        startForeground(1370, note);
    }

    private void setScreen() {
        final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        this.mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
        this.mWakeLock.acquire();
    }

    private void closeLauncher(String switchParent){
        if(closePressed){//close launcher
            try {
                G.selectedKid.joKid = FetchDb.getSelectedKid(App.getContext());
                Intent mIntent = new Intent();
                mIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                mIntent.setAction(Intent.ACTION_MAIN);
                mIntent.setPackage(App.getContext().getPackageName());
                ResolveInfo rInfo = App.getContext().getPackageManager().resolveActivity(mIntent, 0);

                String className = rInfo.activityInfo.name;
                Permisions.setPermissionApps(getApplicationContext(), new JSONObject(G.selectedKid.joKid.getString("settings")), App.getContext().getPackageName(), className, "true");
//                    getApplicationContext().stopService(new Intent(getApplicationContext(), Service_Launcher.class));

                Service_Launcher.this.stopForeground(true);
                Service_Launcher.this.stopSelf();

                if (FetchDb.getSetting(getApplicationContext(), "passmode").equals("0")) {//have to start activity password
                    Intent intent = new Intent(getApplicationContext(), Activity_Get_Password.class);
                    intent.putExtra("UlockApp", "true");
                    intent.putExtra("switchParent", switchParent);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getApplicationContext().startActivity(intent);
                } else {
                    Intent intent = new Intent(getApplicationContext(), Activity_GetPattern.class);
                    intent.putExtra("UlockApp", "true");
                    intent.putExtra("switchParent", switchParent);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getApplicationContext().startActivity(intent);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            closePressed=true;
            Animation fadeIn=AnimationUtils.loadAnimation(App.getContext(),android.R.anim.fade_in);
            fadeIn.setDuration(300);
            fadeIn.setFillAfter(true);
            fadeIn.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    closeText.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animation animation) {

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            closeText.setText("برای خروج یکبار دیگر دکمه را فشار دهید");
            closeText.startAnimation(fadeIn);
            new Timer().schedule(new timerClose(),1000);
        }
    }
    
    class timerClose extends TimerTask{
        
        @Override
        public void run() {
            closePressed=false;
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Animation fadeOut=AnimationUtils.loadAnimation(App.getContext(),android.R.anim.fade_out);
                    fadeOut.setDuration(300);
                    fadeOut.setFillBefore(false);
                    fadeOut.setFillAfter(true);
                    fadeOut.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            closeText.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    closeText.startAnimation(fadeOut);
                }
            });
            
        }
    }
    
    private void watchIncommingCalls(boolean answer) {
        final IntentFilter intentFilter = new IntentFilter("android.intent.action.PHONE_STATE");
        receiveCallBroadcast = new Broadcast_Recieve_Call("launcher-" + answer);
        getApplicationContext().registerReceiver(receiveCallBroadcast, intentFilter);
    }

    void setupViews() {
        G.selectedKid.joKid = FetchDb.getSelectedKid(this);


        //--------- setup launcher design -------------------
        RelativeLayout REL_parent = (RelativeLayout) pView.findViewById(R.id.act_launcher_parentRel);
        ImageView IMG_kidAvatar = (ImageView) pView.findViewById(R.id.act_launcher_kid_name);
        TextView IMG_kidName = (TextView) pView.findViewById(R.id.act_launcher_kid_avatar);

        ImageView close = (ImageView) pView.findViewById(R.id.act_launcher_close);
        

        TextView TXT_time = (TextView) pView.findViewById(R.id.act_launcher_clock_clock);
        TextView TXT_date = (TextView) pView.findViewById(R.id.act_launcher_clock_day);


        CalTool cal = new CalTool();
        TXT_date.setText((cal.getDayOfWeekIranianName() + "٬ " + cal.getIranianDay() + " " + cal.getIranianMonthName())
                        .replace("0", "۰")
                        .replace("1", "۱")
                        .replace("2", "۲")
                        .replace("3", "۳")
                        .replace("4", "۴")
                        .replace("5", "۵")
                        .replace("6", "۶")
                        .replace("7", "۷")
                        .replace("8", "۸")
                        .replace("9", "۹")

        );

        //-------- set clock broadcast ---------------
//        timerBroadcast = new broadcastTimer();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.SECOND, (calendar.get(Calendar.SECOND) + 1));
//        AlarmManager am = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
//        Intent intent = new Intent(getApplicationContext().getApplicationContext(), broadcastTimer.class);
//        PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), 124, intent, 0);
//        getApplicationContext().registerReceiver(timerBroadcast, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
//        
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() + 1000, pi);
//        }else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
//            am.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1000, pi);
//        }else{
//            am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1000, pi);
//        }
        
        String time = ("" + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE))
                .replace("0", "۰")
                .replace("1", "۱")
                .replace("2", "۲")
                .replace("3", "۳")
                .replace("4", "۴")
                .replace("5", "۵")
                .replace("6", "۶")
                .replace("7", "۷")
                .replace("8", "۸")
                .replace("9", "۹");
        TXT_time.setText(time);

//-------------------------------------------------------------------------------

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    G.selectedKid.joKid = FetchDb.getSelectedKid(App.getContext());
                    Intent mIntent = new Intent();
                    mIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                    mIntent.setAction(Intent.ACTION_MAIN);
                    mIntent.setPackage(App.getContext().getPackageName());
                    ResolveInfo rInfo = App.getContext().getPackageManager().resolveActivity(mIntent, 0);

                    String className = rInfo.activityInfo.name;
                    Permisions.setPermissionApps(getApplicationContext(), new JSONObject(G.selectedKid.joKid.getString("settings")), App.getContext().getPackageName(), className, "true");
//                    getApplicationContext().stopService(new Intent(getApplicationContext(), Service_Launcher.class));

                    Service_Launcher.this.stopForeground(true);
                    Service_Launcher.this.stopSelf();

                    if (FetchDb.getSetting(getApplicationContext(), "passmode").equals("0")) {//have to start activity password
                        Intent intent = new Intent(getApplicationContext(), Activity_Get_Password.class);
                        intent.putExtra("UlockApp", "true");
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getApplicationContext().startActivity(intent);
                    } else {
                        Intent intent = new Intent(getApplicationContext(), Activity_GetPattern.class);
                        intent.putExtra("UlockApp", "true");
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getApplicationContext().startActivity(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });

        try {
            Bitmap bgrImage = Func.getImage("bgr_kid_" + G.selectedKid.joKid.getString("ID"));
            Bitmap kidImage = Func.getImage("Avatar_" + G.selectedKid.joKid.getString("ID"));
            if (bgrImage != null)
                REL_parent.setBackgroundDrawable(new BitmapDrawable(getResources(), bgrImage));
            if (kidImage != null) IMG_kidAvatar.setImageBitmap(kidImage);
            IMG_kidName.setText(G.selectedKid.joKid.getString("name"));

        } catch (JSONException e) {
            e.printStackTrace();
        }


        //--------- setup kids apps -------------------------
        try {
            if (G.kids_app.AllSystemApps.size() == 0) {//if it was not loadded before
                G.kids_app.AllSystemApps = installedApps();
            }
            for (ResolveInfo ri : G.kids_app.AllSystemApps) {
                G.kids_app.AllAppResolver.add(ri);
            }
            JSONArray JArrKidApps = FetchDb.getKidApps(this, G.selectedKid.joKid.getString("ID"));
            if (G.kids_app.KidAppResolver.size() == 0) {// if it was not loaded before
                for (int i = 0; i < JArrKidApps.length(); i++) {
                    G.kids_app.KidAppResolver.add(JArrKidApps.getJSONObject(i));


                }
            }
            for (JSONObject jo : G.kids_app.KidAppResolver) {//if it was added before ,remove
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


           /* RecyclerView mainList = (RecyclerView) pView.findViewById(R.id.act_launcher_mainRel);
            mainList.setAdapter(new Adapter_Launcher_Apps(getBaseContext()));
            StaggeredGridLayoutManager mStaggeredLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
            mainList.setLayoutManager(mStaggeredLayoutManager);
*/

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void setupViewsViewPager() {

        //--------- setup launcher design -------------------
        RelativeLayout REL_bug = (RelativeLayout) pView.findViewById(R.id.act_launcher_bug_rel);
        ImageView bgr = (ImageView) pView.findViewById(R.id.act_launcher_bgr);

        ImageView IMG_kidAvatar = (ImageView) pView.findViewById(R.id.act_launcher_kid_name);
        TextView IMG_kidName = (TextView) pView.findViewById(R.id.act_launcher_kid_avatar);

        ImageView close = (ImageView) pView.findViewById(R.id.act_launcher_close);
        ImageView switch_parent = (ImageView) pView.findViewById(R.id.act_launcher_switch_parent);

        TextView TXT_time = (TextView) pView.findViewById(R.id.act_launcher_clock_clock);
        TextView TXT_date = (TextView) pView.findViewById(R.id.act_launcher_clock_day);

        closeText=(TextView)pView.findViewById(R.id.act_launcher_close_hint);

        CalTool cal = new CalTool();
        TXT_date.setText((cal.getDayOfWeekIranianName() + "٬ " + cal.getIranianDay() + " " + cal.getIranianMonthName())
                        .replace("0", "۰")
                        .replace("1", "۱")
                        .replace("2", "۲")
                        .replace("3", "۳")
                        .replace("4", "۴")
                        .replace("5", "۵")
                        .replace("6", "۶")
                        .replace("7", "۷")
                        .replace("8", "۸")
                        .replace("9", "۹")

        );

        //-------- set clock broadcast ---------------
//        timerBroadcast = new broadcastTimer();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.SECOND, (calendar.get(Calendar.SECOND) + 1));
//        AlarmManager am = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
//        Intent intent = new Intent(getApplicationContext().getApplicationContext(), broadcastTimer.class);
//        PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), 124, intent, 0);
//        getApplicationContext().registerReceiver(timerBroadcast, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            Log.d(G.LOG_TAG,"1.start timer");
//            am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() + 1000, pi);
//        }else if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
//            Log.d(G.LOG_TAG,"2.start timer");
//            am.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1000, pi);
//        }else{
//            Log.d(G.LOG_TAG,"3.start timer");
//            am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1000, pi);
//        }
        try {
            new Timer().schedule(new timerClock(), 1000);
        }catch (Exception ex){
            
        }
        
//        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1000, pi);
        String time = ("" + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE))
                .replace("0", "۰")
                .replace("1", "۱")
                .replace("2", "۲")
                .replace("3", "۳")
                .replace("4", "۴")
                .replace("5", "۵")
                .replace("6", "۶")
                .replace("7", "۷")
                .replace("8", "۸")
                .replace("9", "۹");
        TXT_time.setText(time);

//-------------------------------------------------------------------------------

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeLauncher("false");
            }
        });
        switch_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeLauncher("true");
            }
        });

        try {
            G.selectedKid.joKid=FetchDb.getSelectedKid(App.getContext());
            JSONObject kidSettings = new JSONObject(G.selectedKid.joKid.getString("settings"));
            
            String bgrName = kidSettings.getString("bgr");
            if (bgrName.contains("bgr_kid_drawable_")) {
                int bgrResourceId = Integer.parseInt(bgrName.substring("bgr_kid_drawable_".length()));
                Picasso.with(App.getContext()).load(bgrResourceId).into(bgr);
//                bgr.setImageResource(bgrResourceId);
                if (bgrResourceId == R.drawable.bgr_default) {//add monsters
                    REL_bug.setVisibility(View.VISIBLE);
                    animateMonsters();
                } else {
                    REL_bug.setVisibility(View.GONE);
                }

            } else {
//                Bitmap bgrImage = Func.getImage("bgr_kid_" + G.selectedKid.joKid.getString("ID"));
             
                File bgrImage=new File(G.dir + "/bgr_kid_" + G.selectedKid.joKid.getString("ID") + ".PNG");
                if(bgrImage.exists()){
                    Picasso.with(App.getContext()).load(bgrImage).skipMemoryCache().into(bgr);
                    REL_bug.setVisibility(View.GONE);
                }else{
                    REL_bug.setVisibility(View.VISIBLE);
                    animateMonsters();
                }
             
            }
            Bitmap kidImage = Func.getImage("Avatar_" + G.selectedKid.joKid.getString("ID"));
//                    bgr.setImageDrawable(new BitmapDrawable(getResources(), bgrImage));
            if (kidImage != null) IMG_kidAvatar.setImageBitmap(kidImage);
            IMG_kidName.setText(G.selectedKid.joKid.getString("name"));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        //--------- setup kids apps -------------------------
        try {
            if (G.kids_app.AllSystemApps.size() == 0) {//if it was not loadded before
                G.kids_app.AllSystemApps = installedApps();
            }
            for (ResolveInfo ri : G.kids_app.AllSystemApps) {
                G.kids_app.AllAppResolver.add(ri);
            }
            JSONArray JArrKidApps = FetchDb.getKidApps(this, G.selectedKid.joKid.getString("ID"));
            if (G.kids_app.KidAppResolver.size() == 0) {// if it was not loaded before
                for (int i = 0; i < JArrKidApps.length(); i++) {
                    G.kids_app.KidAppResolver.add(JArrKidApps.getJSONObject(i));


                }
            }
            for (JSONObject jo : G.kids_app.KidAppResolver) {//if it was added before ,remove
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
            final ViewPager mainViewPager = (ViewPager) pView.findViewById(R.id.act_launcher_mainPager);
            final int startIndex=4;
                final LinearLayout mainLinear = (LinearLayout) pView.findViewById(R.id.act_launcher_linear_shortcuts);
                mainLinear.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        int width = mainLinear.getMeasuredWidth();
                        int height = mainViewPager.getMeasuredHeight();
                        if(width>height){//landscape mode
                            int a=height;
                            height=width;
                            width=a;
                        }
                        for (int i = 0; i < G.kids_app.KidAppResolver.size(); i++) {//add bottom icons
                            LayoutInflater inflater = LayoutInflater.from(App.getContext());
                            View child = inflater.inflate(R.layout.row_launcher_shortcut, null);
                            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width / (G.kids_app.KidAppResolver.size()+1), RelativeLayout.LayoutParams.MATCH_PARENT);
                            if(G.kids_app.KidAppResolver.size()>startIndex){
                                params = new RelativeLayout.LayoutParams(width / (startIndex+1), RelativeLayout.LayoutParams.MATCH_PARENT);
                            }
                            ImageView icon = (ImageView) child.findViewById(R.id.row_launcher_shortcut_app_icon);
                            final JSONObject jo = G.kids_app.KidAppResolver.get(i);
                            try {
                                final String packageName = jo.getString("packageName");
                                final String className = jo.getString("className");

                                final ComponentName componentName=new ComponentName(packageName,className);
                                PackageManager pkgManager=App.getContext().getPackageManager();

                                ActivityInfo app =App.getContext().getPackageManager().getActivityInfo(componentName, 0);
                                Drawable iconDrawable = app.loadIcon(pkgManager);
                                String name = app.loadLabel(pkgManager).toString();

                                icon.setImageDrawable(iconDrawable);


                                icon.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        try {
                                            boolean isAllowToUseApp = Func.isDailyForAppEnable(App.getContext(), G.selectedKid.joKid.getString("ID"), packageName,className);
                                            int isAllowToUseWeeklyApp = FetchDb.isSchedualWeekEnable(App.getContext(), G.selectedKid.joKid.getString("ID"), packageName,className);
                                            if (isAllowToUseApp & isAllowToUseWeeklyApp != -1) {
                                                Permisions.setPermissionApps(App.getContext(), new JSONObject(G.selectedKid.joKid.getString("settings")), packageName, className, jo.getString("AccessInternet"));
                                                Intent intent=new Intent(Intent.ACTION_MAIN, null);
                                                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                intent.setComponent(componentName);
                                                App.getContext().startActivity(intent);
                                            }

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (PackageManager.NameNotFoundException e) {
                                e.printStackTrace();
                            }

                            mainLinear.addView(child, params);
                            if(i>=startIndex-1){
                                break;
                            }
                        }
                        LayoutInflater inflater = LayoutInflater.from(App.getContext());
                        View child = inflater.inflate(R.layout.row_launcher_shortcut, null);
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width / (G.kids_app.KidAppResolver.size()+1), RelativeLayout.LayoutParams.MATCH_PARENT);
                        if(G.kids_app.KidAppResolver.size()>startIndex){
                            params = new RelativeLayout.LayoutParams(width / (startIndex+1), RelativeLayout.LayoutParams.MATCH_PARENT); 
                        }
                        final ImageView icon = (ImageView) child.findViewById(R.id.row_launcher_shortcut_app_icon);
//                        icon.setPadding(10,10,10,10);
                        icon.setImageResource(R.drawable.icon_app_close);
                        icon.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (mainViewPager.getVisibility() == View.VISIBLE) {
                                    Func.ShowHideMenu(mainViewPager, false);
                                    icon.setImageResource(R.drawable.icon_app_close);
                                } else {
                                    Func.ShowHideMenu(mainViewPager, true);
                                    icon.setImageResource(R.drawable.icon_app_open);

                                }
                            }
                        });
                        mainLinear.addView(child, params);

                        mainViewPager.setVisibility(View.GONE);
                        mainViewPager.setAdapter(new CustomPagerAdapter(getApplicationContext(),0, 16, height));
//                        mainViewPager.setAdapter(new CustomPagerAdapter(getApplicationContext(),0, 16, height));
                        mainViewPager.getViewTreeObserver().removeOnPreDrawListener(this);
                        
                        
                        mainLinear.getViewTreeObserver().removeOnPreDrawListener(this);
                        return true;
                    }
                });

            


        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void animateMonsters() {
        //------------ anite baloon ----------
        final ImageView baloon = (ImageView) pView.findViewById(R.id.baloon);
        final Animation anim_baloon = AnimationUtils.loadAnimation(this, R.anim.anim_balloon);
        anim_baloon.setDuration(50000);
        baloon.startAnimation(anim_baloon);

        //------------- animate bugs ---------
        final Animation anim_jump= AnimationUtils.loadAnimation(this,R.anim.anim_jump);
        final Animation anim_shadow= AnimationUtils.loadAnimation(this,R.anim.anim_scale_shadow);

        final RelativeLayout bug_rel=(RelativeLayout)pView.findViewById(R.id.frg_intro_1_bug_rel);
        final ImageView bug_eye=(ImageView)pView.findViewById(R.id.frg_intro_1_bug_eye);
        final ImageView monster_2_eye=(ImageView)pView.findViewById(R.id.monster_2_eye);
        final ImageView bug_shadow=(ImageView)pView.findViewById(R.id.frg_intro_1_bug_shadow);


        anim_jump.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                ((AnimationDrawable) bug_eye.getDrawable()).stop();
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                if(bug_eye!=null)((AnimationDrawable) bug_eye.getDrawable()).start();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        bug_rel.startAnimation(anim_jump);
                        bug_shadow.startAnimation(anim_shadow);
                    }
                }, 4000);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        bug_rel.startAnimation(anim_jump);
        bug_shadow.startAnimation(anim_shadow);
        ((AnimationDrawable) bug_eye.getDrawable()).start();
        ((AnimationDrawable) monster_2_eye.getDrawable()).start();

    }

    public class CustomPagerAdapter extends PagerAdapter {

        private Context mContext;
        private int mIconCount;
        private int mHeight;
        private int pagesCount = -1;
        private int mStartIndex;

        public CustomPagerAdapter(Context context,int startIndex, int iconCount, int height) {
            mContext = context;
            mIconCount = iconCount;
            mHeight = height;
            mStartIndex=startIndex;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int pageIndex) {

            int from = (pageIndex * mIconCount) + mStartIndex;
            int to = from;

            if (((pageIndex + 1) * mIconCount) >= G.kids_app.KidAppResolver.size() ) {//its last page
                to = (G.kids_app.KidAppResolver.size()) - 1;
            } else {//its not last page
                to = from + (mIconCount - 1);//15==a pages total icon count
            }

            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.fragment_select_kid_apps, container, false);
            RecyclerView mainList = (RecyclerView) view.findViewById(R.id.frg_kid_apps_recycleview);
            mainList.setAdapter(new Adapter_Launcher_Fragments_Apps(mContext, mHeight, from, to));
            StaggeredGridLayoutManager mStaggeredLayoutManager = new StaggeredGridLayoutManager((int) Math.sqrt(mIconCount), StaggeredGridLayoutManager.VERTICAL);
            mainList.setLayoutManager(mStaggeredLayoutManager);
            container.addView(view);
            Log.d(G.LOG_TAG, "FRG load view ");
            return view;
        }

        @Override
        public void destroyItem(ViewGroup collection, int position, Object view) {
            collection.removeView((View) view);
        }

        @Override
        public int getCount() {
            if (pagesCount == -1) {
                int maghsumElayh = (G.kids_app.KidAppResolver.size() - mStartIndex) / mIconCount;
                int baghimande = (G.kids_app.KidAppResolver.size() - mStartIndex) % mIconCount;


                if (baghimande >= maghsumElayh) {
                    pagesCount = maghsumElayh + 1;
                } else if (baghimande < maghsumElayh) {
                    pagesCount = maghsumElayh;
                }
                Log.d(G.LOG_TAG, "FRG count " + pagesCount);
            }
            return pagesCount;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            return "";
        }

    }

    class broadcastTimer extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent mIntent) {
            TextView TXT_time = (TextView) pView.findViewById(R.id.act_launcher_clock_clock);
            Calendar cal = Calendar.getInstance();
            int hour = cal.get(Calendar.HOUR_OF_DAY);
            int minute = cal.get(Calendar.MINUTE);
            String time = ("" + hour + ":" + minute)
                    .replace("0", "۰")
                    .replace("1", "۱")
                    .replace("2", "۲")
                    .replace("3", "۳")
                    .replace("4", "۴")
                    .replace("5", "۵")
                    .replace("6", "۶")
                    .replace("7", "۷")
                    .replace("8", "۸")
                    .replace("9", "۹");
            TXT_time.setText(time);
            Log.d(G.LOG_TAG, "set timme:" + hour + ":" + minute);


//            timerBroadcast = new broadcastTimer();
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.SECOND, (calendar.get(Calendar.SECOND) + 1));
//            AlarmManager am = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
//            Intent intent = new Intent(getApplicationContext().getApplicationContext(), broadcastTimer.class);
//            PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), 124, intent, 0);
////            getApplicationContext().registerReceiver(timerBroadcast, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
//
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() + 1000, pi);
//            }else if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
////                am.setInExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis()+1000, pi);
//            }
            

        }
    }
    
    class timerClock extends TimerTask{

        @Override
        public void run() {
            try {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        TextView TXT_time = (TextView) pView.findViewById(R.id.act_launcher_clock_clock);
                        Calendar cal = Calendar.getInstance();
                        int hour = cal.get(Calendar.HOUR_OF_DAY);
                        int minute = cal.get(Calendar.MINUTE);
                        String time = ("" + hour + ":" + minute)
                                .replace("0", "۰")
                                .replace("1", "۱")
                                .replace("2", "۲")
                                .replace("3", "۳")
                                .replace("4", "۴")
                                .replace("5", "۵")
                                .replace("6", "۶")
                                .replace("7", "۷")
                                .replace("8", "۸")
                                .replace("9", "۹");
                        TXT_time.setText(time);
                        Log.d(G.LOG_TAG, "set timme:" + hour + ":" + minute);


                        if(G.isServiceLaucerRun)new Timer().schedule(new timerClock(), 1000);
                    }
                });
            }catch (Exception ex){
                
            }
        }
    }

    private List<ResolveInfo> installedApps() {
        final Intent main_intent = new Intent(Intent.ACTION_MAIN, null);
        main_intent.addCategory(Intent.CATEGORY_LAUNCHER);
        return getPackageManager().queryIntentActivities(main_intent, 0);
    }


}
