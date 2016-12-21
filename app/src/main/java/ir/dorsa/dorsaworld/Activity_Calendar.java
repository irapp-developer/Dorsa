package ir.dorsa.dorsaworld;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import ir.dorsa.dorsaworld.adapter.Adapter_Calendar;
import ir.dorsa.dorsaworld.other.FetchDb;
import ir.dorsa.dorsaworld.other.Func;
import ir.dorsa.dorsaworld.other.G;

/**
 * Created by mehdi on 3/7/16 AD.
 */
public class Activity_Calendar extends AppCompatActivity {
    private ArrayList<View> appsIcons = new ArrayList();
    private ArrayList<String> appPackageName = new ArrayList<>();
    private ArrayList<String> appClassName = new ArrayList<>();


    private CheckBox dailyLimit_CheckBox;
    private EditText dailyLimit_EditText;
    
    private ImageView dailyLimit_plus;
    private ImageView dailyLimit_minios;

    private TextView dayLimit_title_1;
    private TextView dayLimit_title_2;
    
    private Tracker mTracker;
    
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.schedual, menu);
        return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public void onBackPressed() {
        setDailyLimit();
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        
        // make anallytics //
        App application = (App) getApplication();
        mTracker = application.getDefaultTracker();
        mTracker.enableAdvertisingIdCollection(true);
        
        setupActionBar();
        ListView mainList = (ListView) findViewById(R.id.act_calendar_mainList);
        dailyLimit_CheckBox = (CheckBox) findViewById(R.id.act_calendar_daily_checkBox);
        dailyLimit_EditText = (EditText) findViewById(R.id.act_calendar_daily_time);
        
        dailyLimit_plus = (ImageView) findViewById(R.id.act_calendar_daily_plus);
        dailyLimit_minios = (ImageView) findViewById(R.id.act_calendar_daily_minios);

        dayLimit_title_1 = (TextView) findViewById(R.id.act_calendar_daily_txt1);
        dayLimit_title_2 = (TextView) findViewById(R.id.act_calendar_daily_txt2);


        dailyLimit_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String number=dailyLimit_EditText.getText().toString();
                if(number.length()>0){
                    int num=Integer.parseInt(number);
                    if(num<24){
                        num+=1;
                        dailyLimit_EditText.setText(""+num);
                    }
                }
            }
        });
        dailyLimit_minios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String number=dailyLimit_EditText.getText().toString();
                if(number.length()>0){
                    int num=Integer.parseInt(number);
                    if(num>1){
                        num-=1;
                        dailyLimit_EditText.setText(""+num);
                    }
                }
            }
        });
        
        View removeAll = findViewById(R.id.act_calendar_weeek_reset_all);
        removeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder dialog = new AlertDialog.Builder(Activity_Calendar.this);
                dialog.setTitle("پاک کردن برنامه ریزی");
                dialog.setMessage("آیا مطمئن هستید که تمام برنامه ریزی های زمانی برای این برنامه حذف گردد؟");
                dialog.setPositiveButton("بله", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        ProgressDialog mDialog = new ProgressDialog(Activity_Calendar.this);
                        mDialog.setMessage("لطفا صبور باشید");
                        new Func.ProgressAsyncTaskSchedual(Activity_Calendar.this, "remove", mDialog).execute();

                    }
                });
                dialog.setNegativeButton("خیر", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });
                dialog.show();
            }
        });


        try {
            //----- setup kids app list-- and select first app as selected
            G.schedual.KidId = G.selectedKid.joKid.getString("ID");
            G.schedual.mAdapter = new Adapter_Calendar(this);
            mainList.setAdapter(G.schedual.mAdapter);

            ProgressDialog mDialog = new ProgressDialog(Activity_Calendar.this);
            mDialog.setMessage("لطفا صبور باشید");
            new ProgressAsyncTaskSchedual(this, mDialog).execute();
//            setupKidApps();
            //--------- setup kids limitaion -----------------

        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (FetchDb.getSetting(this, "intro_schedual").equals("false")) {
            startActivity(new Intent(this, Activity_Intro_Schedual.class));
            FetchDb.setSetting(this, "intro_schedual", "true");
        }

    }

    @Override
    protected void onResume() {
        mTracker.setScreenName("Activity~جدول زمانبندی");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_help) {
            
            
           startActivity(new Intent(this,Activity_Intro_Schedual.class));
        }
        return false;
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        String sexMode="0";
        try {
            sexMode=G.selectedKid.joKid.getString("sex");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if("0".equals(sexMode)){
            actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this,R.color.Color_blue)));
        }else{
            actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this,R.color.Color_red_girl)));
        }
        
        View pView = getLayoutInflater().inflate(R.layout.actionbar_user_image, null);

        ImageView userIcon = (ImageView) pView.findViewById(R.id.actionbar_icon_user);
        ImageView backIcon = (ImageView) pView.findViewById(R.id.actionbar_icon_back);

        TextView userName = (TextView) pView.findViewById(R.id.actionbar_user_name);
        TextView pageTitle = (TextView) pView.findViewById(R.id.actionbar_title);

        pageTitle.setText("جدول زمانبندی");

        try {//-------- 
            String name = G.selectedKid.joKid.getString("name");
            userName.setText(name);
            userIcon.setImageBitmap(Func.getImage("Avatar_" + G.selectedKid.joKid.getString("ID")));

        } catch (JSONException e) {
            e.printStackTrace();
        }


        actionBar.setCustomView(pView);
        Toolbar parent = (Toolbar) pView.getParent();
        parent.setContentInsetsAbsolute(0, 10);


        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private class ProgressAsyncTaskSchedual<Params> extends AsyncTask<Params, Integer, Boolean> {

        protected Activity mActivity;
        protected ProgressDialog progressDialog;


        public ProgressAsyncTaskSchedual(Activity activity, ProgressDialog progressDialog) {
            this.progressDialog = progressDialog;
            this.mActivity = activity;
        }

        @Override
        protected Boolean doInBackground(Params... paramses) {
            setupKidApps();
           /* mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setupKidApps();
                }
            });*/

            return true;
        }

        @Override
        protected void onProgressUpdate(Integer... changed) {
            if (progressDialog != null)
                progressDialog.setProgress(changed[0]);

        }

        @Override
        protected void onPreExecute() {
            if (progressDialog != null)
                try {
                    progressDialog.show();
                } catch (Exception ex) {

                }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (progressDialog.isShowing())
                progressDialog.dismiss();

        }
    }


    private void setupKidApps() {
        final LinearLayout app_lay = (LinearLayout) findViewById(R.id.act_calendar_top_apps_lay);
        for (int i = 0; i < G.kids_app.KidAppResolver.size(); i++) {
            Log.d(G.LOG_TAG, "Kid app " + i + " Added");

            final View child = getLayoutInflater().inflate(R.layout.grid_kids_app, null);
            final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(Func.dpToPx(this, 120), Func.dpToPx(this, 120));

            CardView bgr_card = (CardView) child.findViewById(R.id.grid_kidd_app_cardView);
            ImageView AppImage = (ImageView) child.findViewById(R.id.grid_kidd_app_img);
            TextView AppName = (TextView) child.findViewById(R.id.grid_kidd_app_name);
            ImageView wifiIcon = (ImageView) child.findViewById(R.id.grid_kidd_app_wifi);
            ImageView moreIcon = (ImageView) child.findViewById(R.id.grid_kidd_app_more);

            wifiIcon.setVisibility(View.INVISIBLE);
            moreIcon.setVisibility(View.INVISIBLE);


            JSONObject jo = G.kids_app.KidAppResolver.get(i);
            String packageName = null;
            try {
                packageName = jo.getString("packageName");
                final String className = jo.getString("className");
                final String AccessInternet = jo.getString("AccessInternet");


                final ComponentName componentName = new ComponentName(packageName, className);
                PackageManager pkgManager = App.getContext().getPackageManager();

                ActivityInfo app = App.getContext().getPackageManager().getActivityInfo(componentName, 0);
                Drawable iconDrawable = app.loadIcon(pkgManager);
                String name = app.loadLabel(pkgManager).toString();

                AppName.setText(name);
                AppImage.setImageDrawable(iconDrawable);

                Calendar cal = Calendar.getInstance();
                Log.d(G.LOG_TAG, "is packege for time Index " + Func.timeIndex(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE)) + " :" + FetchDb.isSchedualWeekEnable(this, G.schedual.KidId, packageName, className));

                child.setOnClickListener(new AppIconClick(i));
                appsIcons.add(child);
                appPackageName.add(packageName);
                appClassName.add(className);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        app_lay.addView(child, params);
                    }
                });


                if (i == 0) {
                    String sexMode = G.selectedKid.joKid.getString("sex");
                    if("0".equals(sexMode)){
                        bgr_card.setCardBackgroundColor(ContextCompat.getColor(this,R.color.Color_blue));    
                    }else{
                        bgr_card.setCardBackgroundColor(ContextCompat.getColor(this,R.color.Color_red_girl));
                    }
                    
                    G.schedual.packageName = packageName;
                    G.schedual.className = className;
//                    G.schedual.isNothigAdded= FetchDb.getSchedual(Activity_Calendar.this,G.schedual.KidId,packageName);
//                    G.schedual.mAdapter=new Adapter_Calendar(Activity_Calendar.this);
//                    if(mAdapter!=null)mAdapter.notifyDataSetChanged();m
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ProgressDialog mDialog = new ProgressDialog(Activity_Calendar.this);
                            mDialog.setMessage("لطفا صبور باشید");
                            new Func.ProgressAsyncTaskSchedual(Activity_Calendar.this, "get", mDialog).execute();
                            getDailyLimit();
                        }
                    });


                }


            } catch (JSONException e) {
                e.printStackTrace();
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }


        }
    }

    class AppIconClick implements View.OnClickListener {
        int mPosition;

        public AppIconClick(int position) {
            mPosition = position;
        }

        @Override
        public void onClick(View view) {

            for (View child : appsIcons) {
                CardView bgr_card = (CardView) child.findViewById(R.id.grid_kidd_app_cardView);
                bgr_card.setCardBackgroundColor(Color.parseColor("#ffffff"));
            }

            View child = appsIcons.get(mPosition);
            CardView bgr_card = (CardView) child.findViewById(R.id.grid_kidd_app_cardView);
            ImageView AppImage = (ImageView) child.findViewById(R.id.grid_kidd_app_img);
            TextView AppName = (TextView) child.findViewById(R.id.grid_kidd_app_name);
            ImageView wifiIcon = (ImageView) child.findViewById(R.id.grid_kidd_app_wifi);
            ImageView moreIcon = (ImageView) child.findViewById(R.id.grid_kidd_app_more);

            wifiIcon.setVisibility(View.INVISIBLE);
            moreIcon.setVisibility(View.INVISIBLE);

            setDailyLimit();//save last app daily limit
            String sexMode = "0";
            try {
                sexMode = G.selectedKid.joKid.getString("sex");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if("0".equals(sexMode)){
                bgr_card.setCardBackgroundColor(ContextCompat.getColor(Activity_Calendar.this,R.color.Color_blue));
            }else{
                bgr_card.setCardBackgroundColor(ContextCompat.getColor(Activity_Calendar.this,R.color.Color_red_girl));
            }
            G.schedual.packageName = appPackageName.get(mPosition);
            G.schedual.className = appClassName.get(mPosition);
            ProgressDialog mDialog = new ProgressDialog(Activity_Calendar.this);
            mDialog.setMessage("لطفا صبور باشید");
            new Func.ProgressAsyncTaskSchedual(Activity_Calendar.this, "get", mDialog).execute();
            getDailyLimit();
//            G.schedual.isNothigAdded= FetchDb.getSchedual(Activity_Calendar.this,G.schedual.KidId,G.schedual.packageName);
//            if(G.schedual.mAdapter!=null)G.schedual.mAdapter.notifyDataSetChanged();
        }
    }

    private void getDailyLimit() {
        try {
            JSONObject dailyJo = FetchDb.getKidDailyLimit(this, G.schedual.KidId, G.schedual.packageName, G.schedual.className);
            dailyLimit_EditText.setText(dailyJo.getString("limitPerDay"));
            if (dailyJo.getBoolean("enable")) {
                dailyLimit_CheckBox.setChecked(true);
            } else {
                dailyLimit_CheckBox.setChecked(false);
                dailyLimit_EditText.setEnabled(false);
                dailyLimit_plus.setEnabled(false);
                dailyLimit_minios.setEnabled(false);
                dayLimit_title_1.setEnabled(false);
                dayLimit_title_2.setEnabled(false);
                
            }
            dailyLimit_CheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                    dailyLimit_EditText.setEnabled(b);
                    dailyLimit_plus.setEnabled(b);
                    dailyLimit_minios.setEnabled(b);
                    dayLimit_title_1.setEnabled(b);
                    dayLimit_title_2.setEnabled(b);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setDailyLimit() {
        if (dailyLimit_EditText.getText().length() > 0) {
            FetchDb.setKidDailyLimit(this, G.schedual.KidId, G.schedual.packageName, G.schedual.className, "" + dailyLimit_CheckBox.isChecked(), dailyLimit_EditText.getText().toString());
        }
    }

}
