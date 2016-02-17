package dorsa.psb.com.dorsa.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Calendar;
import java.util.List;

import dorsa.psb.com.dorsa.R;
import dorsa.psb.com.dorsa.adapter.Adapter_Launcher_Apps;
import dorsa.psb.com.dorsa.other.CalTool;
import dorsa.psb.com.dorsa.other.FetchDb;
import dorsa.psb.com.dorsa.other.Func;
import dorsa.psb.com.dorsa.other.G;
import dorsa.psb.com.dorsa.other.Permisions;

public class Service_Launcher extends Service {
    private View pView;
    private WindowManager manager;
    private Intent mInent;
    private broadcastTimer timerBroadcast;


    public Service_Launcher() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        mInent = intent;
        return null;
    }

    @Override
    public void onCreate() {

        manager = ((WindowManager) getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE));


        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |

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

        setupViews();
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

        timerBroadcast = new broadcastTimer();

        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.SECOND, (calendar.get(Calendar.SECOND) + 1));
        AlarmManager am = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext().getApplicationContext(), broadcastTimer.class);
        PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), 124, intent, 0);
        getApplicationContext().registerReceiver(timerBroadcast, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1000, pi);
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

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Permisions.setParrentPermisions(getApplicationContext());
                getApplicationContext().stopService(new Intent(getApplicationContext(), Service_Launcher.class));
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
                    G.kids_app.KidAppResolver.add(JArrKidApps.getJSONObject(i).getString("packageName"));


                }
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


            RecyclerView mainList = (RecyclerView) pView.findViewById(R.id.act_launcher_mainRel);
            mainList.setAdapter(new Adapter_Launcher_Apps(getBaseContext()));
            StaggeredGridLayoutManager mStaggeredLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
            mainList.setLayoutManager(mStaggeredLayoutManager);


        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    class broadcastTimer extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
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

        }
    }


    private List<ResolveInfo> installedApps() {
        final Intent main_intent = new Intent(Intent.ACTION_MAIN, null);
        main_intent.addCategory(Intent.CATEGORY_LAUNCHER);
        return getPackageManager().queryIntentActivities(main_intent, 0);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (pView != null) {
            manager.removeView(pView);
            Intent intent = new Intent(getApplicationContext().getApplicationContext(), broadcastTimer.class);
            PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext().getApplicationContext(), 124, intent, 0);
            getApplicationContext().unregisterReceiver(timerBroadcast);
            pi.cancel();
        }
    }
}
