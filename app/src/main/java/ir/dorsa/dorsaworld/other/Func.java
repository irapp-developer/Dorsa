package ir.dorsa.dorsaworld.other;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import ir.dorsa.dorsaworld.App;
import ir.dorsa.dorsaworld.BuildConfig;
import ir.dorsa.dorsaworld.R;
import ir.dorsa.dorsaworld.adapter.Adapter_All_Apps;
import ir.dorsa.dorsaworld.adapter.Adapter_Dorsa_Apps;
import ir.dorsa.dorsaworld.adapter.Adapter_manage_kid;
import ir.dorsa.dorsaworld.service.Service_version_notification;

/**
 * Created by mehdi on 1/19/16 AD.
 */
public class Func {

    public static void dialogContact(final Context mContext) {
        final Dialog mDialog = new Dialog(mContext, android.R.style.Theme_Holo_Light_Dialog_NoActionBar);
        mDialog.setContentView(R.layout.dialog_contact);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        RelativeLayout telegram = (RelativeLayout) mDialog.findViewById(R.id.dialog_about_telegram_lay);
        RelativeLayout email = (RelativeLayout) mDialog.findViewById(R.id.dialog_about_email_lay);
        RelativeLayout phone = (RelativeLayout) mDialog.findViewById(R.id.dialog_about_phone_lay);

        Button ok = (Button) mDialog.findViewById(R.id.dialog_about_gotit);


        telegram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent smsIntent = new Intent(android.content.Intent.ACTION_VIEW);
                smsIntent.setType("vnd.android-dir/mms-sms");
                smsIntent.putExtra("address", "+98210000222");
                mContext.startActivity(smsIntent);
                mDialog.cancel();
            }
        });
        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"info@dorsaworld.ir"});
                intent.putExtra(Intent.EXTRA_SUBJECT, "پشتیبانی دنیای درسا");
                if (intent.resolveActivity(mContext.getPackageManager()) != null) {
                    mContext.startActivity(intent);
                    mDialog.cancel();
                }
            }
        });
        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + Uri.encode("+982166061506".trim())));
                callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(callIntent);
                mDialog.cancel();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.cancel();
            }
        });

        mDialog.show();

    }

    public static String getDefaultLauncher(Context mContext) {
        PackageManager localPackageManager = mContext.getPackageManager();
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        String str = localPackageManager.resolveActivity(intent,
                PackageManager.MATCH_DEFAULT_ONLY).activityInfo.packageName;
        return str;
    }

    public static Intent newAppDetailsIntent(String packageName) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse("package:" + packageName));
            return intent;
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.FROYO) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setClassName("com.android.settings",
                    "com.android.settings.InstalledAppDetails");
            intent.putExtra("pkg", packageName);
            return intent;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClassName("com.android.settings",
                "com.android.settings.InstalledAppDetails");
        intent.putExtra("com.android.settings.ApplicationPkgName", packageName);
        return intent;
    }

    public static void dialogBirthday(final Context mContext, final TextView TXT_date, final TextView hint, final ImageView tick_birthday) {
        final Dialog mDialog = new Dialog(mContext, android.R.style.Theme_Holo_Dialog_NoActionBar);
        mDialog.setContentView(R.layout.dialog_date_picker);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        
        TextView close = (TextView) mDialog.findViewById(R.id.dialog_pick_date_cross);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.cancel();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            final NumberPicker day = (NumberPicker) mDialog.findViewById(R.id.dialog_pick_date_day_picker);
            final NumberPicker month = (NumberPicker) mDialog.findViewById(R.id.dialog_pick_date_month_picker);
            final NumberPicker years = (NumberPicker) mDialog.findViewById(R.id.dialog_pick_date_year_picker);
            final Button submit = (Button) mDialog.findViewById(R.id.dialog_pick_date_month_submit);

            final String day_name[] = buildYears(1, 31);
            day.setMinValue(0);
            day.setMaxValue(30);
            day.setDisplayedValues(day_name);
            if (G.kid_details.selectedValueDay != -1)
                day.setValue(G.kid_details.selectedValueDay);

            final String month_name[] = new String[]{"فروردین", "اردیبهشت", "خرداد", "تیر", "مرداد", "شهریور", "مهر", "آبان", "آذر", "دی", "بهمن", "اسفند"};
            month.setMinValue(0);
            month.setMaxValue(11);
            month.setDisplayedValues(month_name);
            if (G.kid_details.selectedValueMonth != -1)
                month.setValue(G.kid_details.selectedValueMonth);

            final String[] Year_value = buildYears(1360, 1400);
            years.setMinValue(0);
            years.setMaxValue(Year_value.length - 1);
            years.setDisplayedValues(Year_value);
            years.setValue(Year_value.length - 2);
            if (G.kid_details.selectedValueYear != -1) {
                years.setValue(G.kid_details.selectedValueYear);
            } else {
                years.setValue(new CalTool().getIranianYear() + 1);
//                new CalTool().getIranianYear()
            }

            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    G.kid_details.selectedValueDay = day.getValue();
                    G.kid_details.selectedValueMonth = month.getValue();
                    G.kid_details.selectedValueYear = years.getValue();
                    G.kid_details.birthDay = "" + (month_name[month.getValue()] + " " + Year_value[years.getValue()]);
                    hint.setVisibility(View.INVISIBLE);
                    tick_birthday.setVisibility(View.VISIBLE);
                    TXT_date.setText("" + (day_name[day.getValue()] + " " + month_name[month.getValue()] + " " + Year_value[years.getValue()]));
                    G.addChild.birthday = "" + (day_name[day.getValue()] + " " + month_name[month.getValue()] + " " + Year_value[years.getValue()]);
                    mDialog.cancel();

                }
            });

            mDialog.show();
        } else {
//            final Spinner SPN_year = (Spinner) mDialog.findViewById(R.id.dialog_pick_date_spinner_year);
//            final Spinner SPN_month = (Spinner) mDialog.findViewById(R.id.dialog_pick_date_spinner_month);
//            final Spinner SPN_day = (Spinner) mDialog.findViewById(R.id.dialog_pick_date_spinner_day);
//            final Button submit = (Button) mDialog.findViewById(R.id.dialog_pick_date_month_submit);
//
//            final String month_name[] = new String[]{"فروردین", "اردیبهشت", "خرداد", "تیر", "مرداد", "شهریور", "مهر", "آبان", "آذر", "دی", "بهمن", "اسفند"};
//            final String[] Year_value = buildYears(1360, 1400);
//            final String[] Day_value = buildYears(1, 31);
//
//
//            ArrayAdapter<String> adp_month = new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, month_name);
//            adp_month.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//            SPN_month.setAdapter(adp_month);
//
//            ArrayAdapter<String> adp_year = new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, Year_value);
//            adp_year.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//            SPN_year.setAdapter(adp_year);
//            SPN_year.setSelection(new CalTool().getIranianYear());
//
//            ArrayAdapter<String> adp_day = new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, Day_value);
//            adp_day.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//            SPN_day.setAdapter(adp_day);
//            SPN_day.setSelection(new CalTool().getIranianDay());
//
//
//            submit.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    G.kid_details.selectedValueDay = SPN_day.getSelectedItemPosition() + 1;
//                    G.kid_details.selectedValueMonth = SPN_month.getSelectedItemPosition();
//                    G.kid_details.selectedValueYear = SPN_year.getSelectedItemPosition();
//                    G.kid_details.birthDay = "" + (Day_value[SPN_day.getSelectedItemPosition()] + " " + month_name[SPN_month.getSelectedItemPosition()] + " " + Year_value[SPN_year.getSelectedItemPosition()]);
//                    hint.setVisibility(View.INVISIBLE);
//                    tick_birthday.setVisibility(View.VISIBLE);
//                    TXT_date.setText("" + (Day_value[SPN_day.getSelectedItemPosition()] + " " + month_name[SPN_month.getSelectedItemPosition()] + " " + Year_value[SPN_year.getSelectedItemPosition()]));
//                    G.addChild.birthday = "" + (Day_value[SPN_day.getSelectedItemPosition()] + " " + month_name[SPN_month.getSelectedItemPosition()] + " " + Year_value[SPN_year.getSelectedItemPosition()]);
//                    mDialog.cancel();
//
//                }
//            });
//

        mDialog.show();
        }

    }

    public static void setDorsaApps(Context mContext, Dialog mDialog, RecyclerView mainList, ArrayList<JSONObject> KidAppResolver) {
        try {

            final Intent main_intent = new Intent(Intent.ACTION_MAIN, null);
            main_intent.addCategory(Intent.CATEGORY_LAUNCHER);
            List<ResolveInfo> AllSystemApps = mContext.getPackageManager().queryIntentActivities(main_intent, 0);

            G.DorsaApps.listData.clear();
            G.DorsaApps.listDataBackup.clear();

///---------------------------------------------
            JSONObject json = new JSONObject();
            json.put("AppName", "درسابازار");//
            json.put("packageName", "com.noveiry.dm");
            json.put("url", "http://2rsa.ir/dorsabazar.apk");
            json.put("exist", "false");
            json.put("icon", R.drawable.icon_dorsa);

            G.DorsaApps.listData.add(json);
            G.DorsaApps.listDataBackup.add(json);
///---------------------------------------------            
//            json = new JSONObject();
//            json.put("AppName", "درساگرد");
//            json.put("packageName", "ir.dorsa.dorsagard");
//            json.put("url", "http://getalma.ir/dorsagard.apk");
//            json.put("exist", "false");
//            json.put("icon", R.drawable.icon_dorsagard);
//
//            G.DorsaApps.listData.add(json);
//            G.DorsaApps.listDataBackup.add(json);
///---------------------------------------------            
            json = new JSONObject();
            json.put("AppName", "آفرینک");
            json.put("packageName", "com.ilasoftware.ipmedia.dorsa");
            json.put("url", "http://2rsa.ir/afarinak.apk");
            json.put("exist", "false");
            json.put("icon", R.drawable.icon_afarinak);

            G.DorsaApps.listData.add(json);
            G.DorsaApps.listDataBackup.add(json);
///---------------------------------------------
            json = new JSONObject();
            json.put("AppName", "مهد قرآن ");
            json.put("packageName", "com.tanzil");
            json.put("url", "http://2rsa.ir/mahd.apk");
            json.put("exist", "false");
            json.put("icon", R.drawable.icon_mahd_ghoran);

            G.DorsaApps.listData.add(json);
            G.DorsaApps.listDataBackup.add(json);
///---------------------------------------------


//            json = new JSONObject();
//            json.put("AppName", "کودکانه2");//
//            json.put("packageName", "com.dzmn.koodakane2");
//            json.put("url", "http://bit.ly/koodak2");
//            json.put("exist", "false");
//            json.put("icon", R.drawable.icon_koodakane_2);
//
//            G.DorsaApps.listData.add(json);
//            G.DorsaApps.listDataBackup.add(json);

//            json = new JSONObject();
//            json.put("AppName", "شیر علی و دوستان");//
//            json.put("packageName", "com.gamestudioseven.iranhero");
//            json.put("url", "http://bit.ly/shirali");
//            json.put("exist", "false");
//            json.put("icon", R.drawable.icon_shirali);
//
//            G.DorsaApps.listData.add(json);
//            G.DorsaApps.listDataBackup.add(json);

            for (int i = 0; i < G.DorsaApps.listData.size(); i++) {
                JSONObject jo = G.DorsaApps.listData.get(i);
                String pkgName = null;
                pkgName = jo.getString("packageName");
                //check was this app downloaded before
                for (int j = 0; j < AllSystemApps.size(); j++) {
                    if (AllSystemApps.get(j).activityInfo.applicationInfo.packageName
                            .equals(pkgName)
                            ) {//it was downloaded before
                        jo.put("exist", "true");
                        G.DorsaApps.listData.set(i, jo);
                        break;
                    }
                }
            }
            //-----check was it in kidsApp
            for (JSONObject jo : KidAppResolver) {//if it was added before ,remove
                String pkgNameKidApp = null;
                pkgNameKidApp = jo.getString("packageName");//kid app package name
                for (int i = 0; i < G.DorsaApps.listData.size(); i++) {
                    String pkgNameDorsaApp = G.DorsaApps.listData.get(i).getString("packageName");
                    if (pkgNameDorsaApp.equals(pkgNameKidApp)
                            ) {
                        G.DorsaApps.listData.remove(i);
                        break;
                    }
                }
            }

            mainList.setAdapter(new Adapter_Dorsa_Apps(mContext, mDialog));
            StaggeredGridLayoutManager mStaggeredLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
            mainList.setLayoutManager(mStaggeredLayoutManager);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void DialogSelectApp(final Activity mContext,String sexMode) {

        final Dialog mDialog = new Dialog(mContext, android.R.style.Theme_Translucent_NoTitleBar);
        mDialog.setContentView(R.layout.dialog_select_app);
        final RelativeLayout mainRel = (RelativeLayout) mDialog.findViewById(R.id.dialog_ask_select_app_main_rel);
        Animation tv_in = AnimationUtils.loadAnimation(mContext, R.anim.tv_in);
        final Animation tv_out = (AnimationUtils.loadAnimation(mContext, R.anim.tv_out));
        tv_out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mDialog.cancel();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        View toolbar= mDialog.findViewById(R.id.dialog_ask_select_app_topbgr);
        TextView title= (TextView)mDialog.findViewById(R.id.dialog_select_app_device_text_title);

        RecyclerView mainList = (RecyclerView) mDialog.findViewById(R.id.dialog_select_app_recView);
        RecyclerView DorsaList = (RecyclerView) mDialog.findViewById(R.id.dialog_select_app_dorsa_recView);
        RelativeLayout DorsaList_linear = (RelativeLayout) mDialog.findViewById(R.id.dialog_select_app_dorsa_recView_lay);

        int width = 0;
        int height = 0;
        // getsize() is available from API 13
        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB_MR2) {
            Display display = mContext.getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            width = size.x;
            height = size.y;
        } else {
            Display display = mContext.getWindowManager().getDefaultDisplay();
            // getWidth() & getHeight() are depricated
            width = display.getWidth();
            height = display.getHeight();
        }
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, width / 3);
        params.addRule(RelativeLayout.BELOW, R.id.dialog_select_app_dorsa_text_title);
        DorsaList_linear.setLayoutParams(params);

        setDorsaApps(mContext, mDialog, DorsaList, G.kids_app.KidAppResolver);
        getApps(mContext);
        G.kids_app.Adapter_all_app = new Adapter_All_Apps(mContext, mDialog);

        mainList.setAdapter(G.kids_app.Adapter_all_app);

        StaggeredGridLayoutManager mStaggeredLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        mainList.setLayoutManager(mStaggeredLayoutManager);
        ImageView BTN_cancel = (ImageView) mDialog.findViewById(R.id.dialog_ask_btn_submit);
        
        if("0".equals(sexMode)){
//            BTN_cancel.setBackgroundColor(ContextCompat.getColor(mContext,R.color.Color_blue));
            mainRel.setBackgroundResource(R.drawable.bgr_blue);
        }else{
//            BTN_cancel.setBackgroundColor(ContextCompat.getColor(mContext,R.color.Color_red_girl));
            mainRel.setBackgroundResource(R.drawable.bgr_red);
        }
        
        BTN_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainRel.startAnimation(tv_out);
            }
        });

        mDialog.show();
//                        mainRel.startAnimation(tv_in);


    }

    public static void dialogPermissions(final Context mContext, final View row, final String appName, final String kidId, final String packageName, final int position) {

        final Dialog mDialog = new Dialog(mContext, android.R.style.Theme_Holo_Light_Dialog_NoActionBar);
        mDialog.setContentView(R.layout.dialog_permissions);

        TextView title = (TextView) mDialog.findViewById(R.id.dialog_permissions_txt_title);
        final CheckBox chBox_internet = (CheckBox) mDialog.findViewById(R.id.dialog_permissions_chbox_internet);
        final CheckBox chBox_addressBar = (CheckBox) mDialog.findViewById(R.id.dialog_permissions_chbox_addressbar);
        RelativeLayout rel_internet = (RelativeLayout) mDialog.findViewById(R.id.dialog_permissions_rel_internet);
        RelativeLayout rel_addressBar = (RelativeLayout) mDialog.findViewById(R.id.dialog_permissions_rel_addressbar);
        Button btn_save = (Button) mDialog.findViewById(R.id.dialog_ask_btn_yes);
        Button btn_cancel = (Button) mDialog.findViewById(R.id.dialog_ask_btn_no);

        final ImageView wifiIcon = (ImageView) row.findViewById(R.id.grid_kidd_app_wifi);

        title.setText("تغییر تنظیمات برنامه " + appName);
        boolean isChecked = true;
        JSONObject joShPref = null;
        SharedPreferences AppPrf = null;
        try {
            Context dorsaAppContext = mContext.getApplicationContext().createPackageContext("ir.dorsa.dorsagard", Context.CONTEXT_IGNORE_SECURITY);
            AppPrf = dorsaAppContext.getSharedPreferences("com_adak_dorsagard_public", Context.MODE_MULTI_PROCESS);
            String pref = AppPrf.getString("public_sp", "-1");
            JSONArray ja = new JSONArray(pref);
            joShPref = ja.getJSONObject(0);
            isChecked = Boolean.parseBoolean(joShPref.getString("Default"));
            Log.d(G.LOG_TAG, "sh pref is :" + pref);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        chBox_addressBar.setChecked(isChecked);
        //------------ get Access internet ------------------------
        String internetMode = "noPer";
        try {
            boolean needAccessIntenet = false;
            PackageInfo packageInfo = null;
            packageInfo = mContext.getPackageManager().getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);
            String[] requestedPermissions = packageInfo.requestedPermissions;
            if (requestedPermissions != null) {
                for (String permissionName : requestedPermissions) {
                    if (permissionName.toLowerCase().contains("internet")) {
                        needAccessIntenet = true;
                        break;
                    }
                }
            }

            if (needAccessIntenet) {
                JSONObject jo = G.kids_app.KidAppResolver.get(position);
                final String AccessInternet = jo.getString("AccessInternet");
                if (AccessInternet.equals("true")) {//user alow to access intent
//                holder.wifiIcon.setImageResource(R.drawable.icon_wifi_on);
                    internetMode = "on";
                    chBox_internet.setChecked(true);
                } else {//user doesnt alow
//                holder.wifiIcon.setImageResource(R.drawable.icon_wifi_off);
                    internetMode = "off";
                    chBox_internet.setChecked(false);
                }
            } else {
//            holder.wifiIcon.setVisibility(View.INVISIBLE);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        rel_internet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chBox_internet.setChecked(!chBox_internet.isChecked());
            }
        });
        rel_addressBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chBox_addressBar.setChecked(!chBox_addressBar.isChecked());
            }
        });


        final JSONObject finalJo = joShPref;
        final SharedPreferences finalAppPrf = AppPrf;
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                save internet mode
                FetchDb.UpdateInternetAccess(mContext, kidId, packageName, "" + chBox_internet.isChecked());
                if (chBox_internet.isChecked()) {
                    wifiIcon.setImageResource(R.drawable.icon_wifi_on);
                } else {
                    wifiIcon.setImageResource(R.drawable.icon_wifi_off);
                }


                try {
                    finalJo.put("Default", "" + chBox_addressBar.isChecked());
                    JSONArray ja = new JSONArray();
                    ja.put(finalJo);
                    SharedPreferences.Editor finalSharedPrefereceEditor = finalAppPrf.edit();
                    finalSharedPrefereceEditor.putString("public_sp", ja.toString());
                    finalSharedPrefereceEditor.commit();
//                    finalSharedPrefereceEditor.apply();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                try {
                    JSONObject jo = G.kids_app.KidAppResolver.get(position);
                    jo.put("AccessInternet", "" + chBox_internet.isChecked());
                    G.kids_app.KidAppResolver.set(position, jo);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mDialog.cancel();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mDialog.cancel();
            }
        });

        mDialog.show();

    }

    public static void getApps(Context mContext) {
        if (G.kids_app.AllAppResolver.size() == 0) {
            try {
                if (G.kids_app.AllSystemApps.size() == 0) {//if it was not loadded before
                    G.kids_app.AllSystemApps = installedApps(mContext);
                }

                for (ResolveInfo ri : G.kids_app.AllSystemApps) {
                    String PkgName = ri.activityInfo.applicationInfo.packageName;
                    if (!PkgName.equals(App.getContext().getPackageName())
                            & !PkgName.equals("com.android.dialer")
                            ) {
                        G.kids_app.AllAppResolver.add(ri);
                    }
                    if (PkgName.equals("com.android.dialer")) {
//                    isDialerAdded=true;
                        G.kids_app.AllAppResolver.add(ri);
                    }

                }
                JSONArray JArrKidApps = FetchDb.getKidApps(mContext, G.selectedKid.joKid.getString("ID"));
                if (G.kids_app.KidAppResolver.size() == 0) {// if it was not loaded before
                    for (int i = 0; i < JArrKidApps.length(); i++) {
                        G.kids_app.KidAppResolver.add(JArrKidApps.getJSONObject(i));
                    }
                }

                Log.d(G.LOG_TAG, "PKG APP :" + App.getContext().getPackageName());
                for (JSONObject jo : G.kids_app.KidAppResolver) {//if it was added before ,remove
                    String pkgName = jo.getString("packageName");
                    for (int i = 0; i < G.kids_app.AllAppResolver.size(); i++) {
                        String systemPackageName = G.kids_app.AllAppResolver.get(i).activityInfo.applicationInfo.packageName;
                        Log.d(G.LOG_TAG, "PKG system :" + systemPackageName);
                        if (systemPackageName.equals(pkgName)) {
                            G.kids_app.AllAppResolver.remove(i);
                            break;
                        }

                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static List<ResolveInfo> installedApps(Context mContext) {
        final Intent main_intent = new Intent(Intent.ACTION_MAIN, null);
        main_intent.addCategory(Intent.CATEGORY_LAUNCHER);
        return mContext.getPackageManager().queryIntentActivities(main_intent, 0);
    }

    private static void showProgress(Activity mActivity, final Dialog mDialog, final boolean show) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (show) {
                    mDialog.show();
                } else {
                    mDialog.cancel();
                }
            }
        });
    }

    public static void dialogNewpassword(final Context mContext, final boolean SaveToDb) {
        final Dialog mDialog = new Dialog(mContext, android.R.style.Theme_Holo_Dialog_NoActionBar);
        mDialog.setContentView(R.layout.dialog_ask_password);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        final ImageView cross = (ImageView) mDialog.findViewById(R.id.dialog_pass_cross);
        final EditText pass_1 = (EditText) mDialog.findViewById(R.id.dialog_pass_pass_1);
        final EditText pass_2 = (EditText) mDialog.findViewById(R.id.dialog_pass_pass_2);
        final TextView hintPass_1 = (TextView) mDialog.findViewById(R.id.dialog_pass_pass_hint_1);
        final TextView hintPass_2 = (TextView) mDialog.findViewById(R.id.dialog_pass_pass_hint_2);
        final Button BTN_OK = (Button) mDialog.findViewById(R.id.dialog_pass_btn_ok);
        final Button BTN_Cancel = (Button) mDialog.findViewById(R.id.dialog_pass_btn_cancel);

        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.cancel();
            }
        });

        //---------- set hints visibility ----------------
        pass_1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 0) {
                    hintPass_1.setVisibility(View.GONE);
                } else {
                    hintPass_1.setVisibility(View.VISIBLE);
                }
            }
        });

        pass_2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 0) {
                    hintPass_2.setVisibility(View.GONE);
                } else {
                    hintPass_2.setVisibility(View.VISIBLE);
                }
            }
        });
        //--------- Set buttons --------
        BTN_OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pass_1.getText().toString().length() == 0) {
                    pass_1.setError("لطفا رمز را وارد نمایید");
                } else if (pass_2.getText().toString().length() == 0) {
                    pass_2.setError("لطفا رمز را دوباره وارد کنید");
                } else if (!pass_1.getText().toString().equals(pass_2.getText().toString())) {
                    Toast.makeText(mContext, "رمزها یکسان نمی باشد ٬دوباره تلاش کنید", Toast.LENGTH_LONG).show();
                } else {
                    G.Intro.password = pass_1.getText().toString();
                    G.Intro.passmode = "0";
                    if (SaveToDb) {
                        FetchDb.setSetting(mContext, "password", G.Intro.password);
                        FetchDb.setSetting(mContext, "passmode", G.Intro.passmode);
                    }
                    mDialog.cancel();
                    if (G.mViewPager != null) G.mViewPager.setCurrentItem(2);
                    //go to next step
                }
            }
        });

        BTN_Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.cancel();
            }
        });


        mDialog.show();
    }

    public static void dialogResetPassword(final Activity mActivity) {
        final Dialog mDialog = new Dialog(mActivity, android.R.style.Theme_Holo_Dialog_NoActionBar);
        mDialog.setContentView(R.layout.dialog_ask_password);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        final ImageView cross = (ImageView) mDialog.findViewById(R.id.dialog_pass_cross);
        final EditText pass_1 = (EditText) mDialog.findViewById(R.id.dialog_pass_pass_1);
        final EditText pass_2 = (EditText) mDialog.findViewById(R.id.dialog_pass_pass_2);
        final TextView hintPass_1 = (TextView) mDialog.findViewById(R.id.dialog_pass_pass_hint_1);
        final TextView hintPass_2 = (TextView) mDialog.findViewById(R.id.dialog_pass_pass_hint_2);
        final Button BTN_OK = (Button) mDialog.findViewById(R.id.dialog_pass_btn_ok);
        final Button BTN_Cancel = (Button) mDialog.findViewById(R.id.dialog_pass_btn_cancel);

        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.cancel();
            }
        });

        //---------- set hints visibility ----------------
        pass_1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 0) {
                    hintPass_1.setVisibility(View.GONE);
                } else {
                    hintPass_1.setVisibility(View.VISIBLE);
                }
            }
        });

        pass_2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 0) {
                    hintPass_2.setVisibility(View.GONE);
                } else {
                    hintPass_2.setVisibility(View.VISIBLE);
                }
            }
        });
        //--------- Set buttons --------
        BTN_OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pass_1.getText().toString().length() == 0) {
                    pass_1.setError("لطفا رمز را وارد نمایید");
                } else if (pass_2.getText().toString().length() == 0) {
                    pass_2.setError("لطفا رمز را دوباره وارد کنید");
                } else if (!pass_1.getText().toString().equals(pass_2.getText().toString())) {
                    Toast.makeText(mActivity, "رمزها یکسان نمی باشد ٬دوباره تلاش کنید", Toast.LENGTH_LONG).show();
                } else {

                    FetchDb.setSetting(mActivity, "password", pass_1.getText().toString());
                    FetchDb.setSetting(mActivity, "passmode", "0");
                    mDialog.cancel();
                    mActivity.setResult(Activity.RESULT_OK);
                    mActivity.finish();
                    if (G.mViewPager != null) G.mViewPager.setCurrentItem(2);
                    //go to next step
                }
            }
        });

        BTN_Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.cancel();
            }
        });


        mDialog.show();
    }

    public static void dialogChooseKid(Activity mContext,String sexMode) {
        final Dialog mDialog = new Dialog(mContext, android.R.style.Theme_Holo_Dialog_NoActionBar);
        mDialog.setContentView(R.layout.dialog_choose_kid);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        
        RelativeLayout relParent=(RelativeLayout)mDialog.findViewById(R.id.dialog_manage_chid_main_rel); 
        Button close = (Button) mDialog.findViewById(R.id.dialog_manage_chid_btn_back);

        
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.cancel();
            }
        });

        G.chooseKid.kidsDetails.clear();
        RecyclerView mainList = (RecyclerView) mDialog.findViewById(R.id.dialog_manage_chid_list);
        JSONArray JArrKids = FetchDb.getAllKids(mContext);

        for (int i = 0; i < JArrKids.length(); i++) {
            try {
                G.chooseKid.kidsDetails.add(JArrKids.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        mainList.setAdapter(new Adapter_manage_kid(mContext, mDialog));

        StaggeredGridLayoutManager mStaggeredLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        mainList.setLayoutManager(mStaggeredLayoutManager);


        mDialog.show();
    }

    public static void dialogAbout(Context mContext) {
        final Dialog mdiDialog = new Dialog(mContext, android.R.style.Theme_Holo_Light_Dialog_NoActionBar);
        mdiDialog.setContentView(R.layout.dialog_about);
        mdiDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView back = (TextView) mdiDialog.findViewById(R.id.dialog_about_gotit);
        TextView version = (TextView) mdiDialog.findViewById(R.id.dialog_about_header_version);
        version.setText("نسخه "+BuildConfig.VERSION_NAME);
        back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                mdiDialog.cancel();
            }
        });
        mdiDialog.show();
    }

    private static String[] buildYears(int from, int to) {
        String result[] = new String[(to - from) + 1];

        for (int i = 0; i <= (to - from); i++) {
            result[i] = "" + (from + i);
            result[i] = result[i]
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
            ;
        }

        return result;
    }

    public static Bitmap getRoundedCroppedBitmap(Bitmap bmp, int radius) {
        Bitmap sbmp;
        if (bmp.getWidth() != radius || bmp.getHeight() != radius)
            sbmp = Bitmap.createScaledBitmap(bmp, radius, radius, false);
        else
            sbmp = bmp;
        Bitmap output = Bitmap.createBitmap(sbmp.getWidth(),
                sbmp.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xffa19774;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, sbmp.getWidth(), sbmp.getHeight());

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.parseColor("#BAB399"));
        canvas.drawCircle(sbmp.getWidth() / 2 + 0.7f, sbmp.getHeight() / 2 + 0.7f,
                sbmp.getWidth() / 2 + 0.1f, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(sbmp, rect, rect, paint);


        return output;
    }

    public static class CompoSwitchChangeListner implements CompoundButton.OnCheckedChangeListener {
        private String name;
        private Context mContext;

        public CompoSwitchChangeListner(Context context, String mName) {
            this.mContext = context;
            this.name = mName;

        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            changeSettings(mContext, name, "" + isChecked);
        }
    }

    public static int dpToPx(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    public static Bitmap getThumbnail(Bitmap bmp) {
        int dimension = Math.min(bmp.getWidth(), bmp.getHeight());
        return ThumbnailUtils.extractThumbnail(bmp, dimension, dimension);
    }

    public static void changeSettings(Context mContext, String name, String dim) {
        try {
            JSONObject KidSetting = new JSONObject(G.selectedKid.joKid.getString("settings"));
            KidSetting.put(name, dim);
            G.selectedKid.joKid.put("settings", KidSetting.toString());

            Log.d(G.LOG_TAG, "new Set values change settings (" + name + "):" + KidSetting.toString());

            FetchDb.setKidSettings(mContext, G.selectedKid.joKid.getString("ID"), KidSetting.toString());


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void saveImage(Bitmap img, String fileName) {
        File dir = new File(G.dir);
        if (!dir.exists()) {
            dir.mkdirs();
            File output = new File(dir, ".nomedia");
            try {
                boolean fileCreated = output.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        OutputStream outStream = null;
        File file = new File(G.dir, fileName + ".PNG");
        try {
            outStream = new FileOutputStream(file);
            img.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();
        } catch (Exception e) {

        }
    }

    public static Bitmap getImage(String fileName) {
        Bitmap bitmap = BitmapFactory.decodeFile(G.dir + "/" + fileName + ".PNG");
        return bitmap;
    }

    public static void removeImage(String fileName) {
        File file = new File(G.dir + "/" + fileName + ".PNG");
        file.delete();
    }

    public static boolean deleteNon_EmptyDir(File dir) {
        try {
            if (dir.isDirectory()) {
                String[] children = dir.list();
                for (int i = 0; i < children.length; i++) {
                    boolean success = deleteNon_EmptyDir(new File(dir, children[i]));
                    if (!success) {
                        return false;
                    }
                }
            }
            return dir.delete();
        } catch (Exception ex) {
            return true;
        }
    }

    public static void setInternetEnabled(Context context, boolean enabled) {
        try {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            wifiManager.setWifiEnabled(enabled);

            ConnectivityManager dataManager;
            dataManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            Method dataMtd = ConnectivityManager.class.getMethod("setMobileDataEnabled", new Class[]{Boolean.TYPE});
            dataMtd.setAccessible(enabled);
            dataMtd.invoke(dataManager, enabled);

        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void setWifiEnabled(Context context, boolean enabled) {

        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(enabled);

    }

    public static void setDataEnabled(Context context, boolean enabled) {
        try {
            ConnectivityManager dataManager;
            dataManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            Method dataMtd = ConnectivityManager.class.getMethod("setMobileDataEnabled", new Class[]{Boolean.TYPE});
            dataMtd.setAccessible(enabled);
            dataMtd.invoke(dataManager, enabled);

        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static String getDayName(String index) {
        String result = "شنبه";
        if (index.equals("0")) {
            result = "شنبه";
        } else if (index.equals("1")) {
            result = "یک شنبه";
        } else if (index.equals("2")) {
            result = "دوشنبه";
        } else if (index.equals("3")) {
            result = "سه شنبه";
        } else if (index.equals("4")) {
            result = "چهارشنبه";
        } else if (index.equals("5")) {
            result = "پنج شنبه";
        } else if (index.equals("6")) {
            result = "جمعه";
        }
        return result;
    }

    public static boolean isUsageStateEnable(Context mContext) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            final UsageStatsManager usageStatsManager = (UsageStatsManager) mContext.getSystemService(Context.USAGE_STATS_SERVICE);
            final List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, 0,  System.currentTimeMillis());
            boolean granted= !queryUsageStats.isEmpty();
            return granted;
        } else {
            return false;
        }
    }

    public static void ShowHideMenu(final ViewPager view, boolean show) {
        if (show) {
            Animation AnimMenuIn = AnimationUtils.loadAnimation(App.getContext(), R.anim.menu_in);
            AnimMenuIn.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    view.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animation animation) {

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            view.startAnimation(AnimMenuIn);
        } else {
            Animation AnimMenuOut = AnimationUtils.loadAnimation(App.getContext(), R.anim.menu_out);
            AnimMenuOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    view.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    view.setVisibility(View.GONE);
                    view.setCurrentItem(0, false);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            view.startAnimation(AnimMenuOut);
        }
    }

    public static class ProgressAsyncTaskSchedual<Params> extends AsyncTask<Params, Integer, Boolean> {

        protected Activity mActivity;
        protected ProgressDialog progressDialog;
        protected String mMode;


        public ProgressAsyncTaskSchedual(Activity activity, String mode, ProgressDialog progressDialog) {
            this.progressDialog = progressDialog;
            this.mActivity = activity;
            this.mMode = mode;
        }

        @Override
        protected Boolean doInBackground(Params... paramses) {
            if (mMode.equals("get")) {
                G.schedual.isNothigAdded = FetchDb.getSchedual(mActivity, G.schedual.KidId, G.schedual.packageName, G.schedual.className);
            } else if (mMode.equals("remove")) {
                FetchDb.removeKidAppSchedual(mActivity, G.schedual.KidId, G.schedual.packageName, G.schedual.className);
                G.schedual.timing = new boolean[48][7];
            }
            this.mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (G.schedual.mAdapter != null) G.schedual.mAdapter.notifyDataSetChanged();
                }
            });

            return G.schedual.isNothigAdded;
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

    public static boolean isDailyForAppEnable(Context mContext, String kidId, String packageName, String className) {
        boolean isAllowToUseApp = false;
        try {
            //--------------------- check daily limit ------------------------------------------
            JSONObject dailyJo = FetchDb.getKidDailyLimit(mContext, kidId, packageName, className);
            if (dailyJo.getBoolean("enable")) {//daily limit is enable
                Calendar cal = Calendar.getInstance();
                if (dailyJo.getString("lastDayUsed").equals("" + cal.get(Calendar.DAY_OF_WEEK))) {//today used before
                    if (dailyJo.getInt("usedSecound") >= (dailyJo.getInt("limitPerDay") * 60 * 60)) {//user not allow to use this app
                        isAllowToUseApp = false;
                    } else {//user have time to use this app
                        isAllowToUseApp = true;
                    }
                } else {//today not used so set todays usage
                    FetchDb.setKidDailyLimitLastUsed(mContext, kidId, packageName, className, "" + cal.get(Calendar.DAY_OF_WEEK), "0");
                    isAllowToUseApp = true;
                }
            } else {//daily limit is disable
                isAllowToUseApp = true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return isAllowToUseApp;
    }

    public static int timeIndex(int hour, int minute) {
        int index = 2 * hour;
        if (minute >= 30 & minute < 60) {
            index += 1;
        }
        return index;
    }

    public static int getDayIndex(int day) {
        int index = 0;
        switch (day) {
            case Calendar.SATURDAY:
                index = 0;
                break;
            case Calendar.SUNDAY:
                index = 1;
                break;
            case Calendar.MONDAY:
                index = 2;
                break;
            case Calendar.TUESDAY:
                index = 3;
                break;
            case Calendar.WEDNESDAY:
                index = 4;
                break;
            case Calendar.THURSDAY:
                index = 5;
                break;
            case Calendar.FRIDAY:
                index = 6;
                break;
        }
        return index;

    }

    public static boolean isDataConnected(Context ctx) {
        ConnectivityManager connectivityMgr = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobile = connectivityMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        // Check if wifi or mobile network is available or not. If any of them is
        // available or connected then it will return true, otherwise false;

        if (mobile != null) {
            if (mobile.isConnected()) {
                return true;
            }
        }
        return false;
    }

    public static boolean isWifiConnected(Context ctx) {
        ConnectivityManager connectivityMgr = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connectivityMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        // Check if wifi or mobile network is available or not. If any of them is
        // available or connected then it will return true, otherwise false;
        if (wifi != null) {
            if (wifi.isConnected()) {
                return true;
            }
        }

        return false;
    }

    public static void checkVersion(final Context mContext) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<String> postName = new ArrayList<String>();
                ArrayList<String> postVal = new ArrayList<String>();

                String result = FetchInternet.getStringFromInternet("http://dorsanegar.dorsalife.ir:8001/apkAddress/address", postName, postVal);
                Log.d(G.LOG_TAG, "check version is :" + result);
                if (result != null) {
                    try {
                        JSONObject jo = new JSONObject(result);
                        int version = jo.getInt("version");
                        String extra = jo.getString("extra");
                        String message = jo.getString("message");
                        if (version > G.version) {//Show notification

                            // Use NotificationCompat.Builder to set up our notification.
                            NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);

                            //icon appears in device notification bar and right hand corner of notification
                            builder.setSmallIcon(R.drawable.icon_dorsa_parent);

                            // This intent is fired when notification is clicked
                            Intent intent = new Intent(mContext, Service_version_notification.class);
                            intent.putExtra("extra_1", extra);

//                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            PendingIntent pIntent = PendingIntent.getService(mContext, new Random().nextInt(475), intent, 0);


                            // Set the intent that will fire when the user taps the notification.
                            builder.setContentIntent(pIntent);

                            // Large icon appears on the left of the notification
                            builder.setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.icon_dorsa_parent));

                            // Content title, which appears in large type at the top of the notification
                            builder.setContentTitle("دنیای درسا");

                            // Content text, which appears in smaller text below the title
                            builder.setContentText(message);
                            builder.setAutoCancel(true);
                            // The subtext, which appears under the text on newer devices.
                            // This will show-up in the devices with Android 4.2 and above only
                            Notification note = builder.build();
//                          note.flags |= Notification.FLAG_NO_CLEAR;

                            note.flags |= Notification.FLAG_AUTO_CANCEL;
                            AudioManager am = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
                            if (am.getRingerMode() != AudioManager.RINGER_MODE_SILENT)
                                note.defaults |= Notification.DEFAULT_SOUND;

                            NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
                            notificationManager.notify(876, note);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }
        }).start();
    }

    public static void registerImsi(final Activity mContext) {
        boolean isPermissionEnable = true;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                if (mContext.checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
                        != PackageManager.PERMISSION_GRANTED) {
                    isPermissionEnable = false;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        if (isPermissionEnable) {

            TelephonyInfo telephonyInfo = TelephonyInfo.getInstance(mContext);

            String imsiSIM1 = telephonyInfo.getImsiSIM1();
            String imsiSIM2 = telephonyInfo.getImsiSIM2();

            boolean isSIM1Ready = telephonyInfo.isSIM1Ready();
            boolean isSIM2Ready = telephonyInfo.isSIM2Ready();

            TelephonyManager telemamanger = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
            String imsi = telemamanger.getSimSerialNumber();


            String IMEI = null;
            if (isSIM1Ready) {
                IMEI = imsiSIM1;
            } else if (isSIM2Ready) {
                IMEI = imsiSIM2;
            }

            final String ImeiCopy = IMEI;
            final String ImsiCopy = imsi;
            final String phoneNumber = FetchDb.getSetting(mContext, "phonenumber");
            final String service_code = "130";
            final String model_number = telephonyInfo.getDeviceName();
            final String brand = Build.BRAND;
            final String AndroidVersion = android.os.Build.VERSION.RELEASE;

            Thread sendServerThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    ArrayList<String> postName = new ArrayList<String>();
                    ArrayList<String> postVal = new ArrayList<String>();

                    postName.add("subscriber_id");
                    postVal.add(phoneNumber);
                    postName.add("service_code");
                    postVal.add(service_code);
                    postName.add("imsi");
                    postVal.add(ImsiCopy);
                    postName.add("imei");
                    postVal.add(ImeiCopy);
                    postName.add("device_name");
                    postVal.add(brand);
                    postName.add("model_number");
                    postVal.add(model_number);
                    postName.add("android_version");
                    postVal.add(AndroidVersion);


                    String result = FetchInternet.getStringFromInternet("http://79.175.155.135:9090/dorsa/services/clientRegistration/login_with_imsi", postName, postVal);
                    if (result != null) {
                        try {
                            JSONObject jo = new JSONObject(result);
                            if (jo.getString("status").equals("0")) {
                                //save access_token in db jo.getString("access_token");
                                FetchDb.setSetting(mContext, "register_imsi", jo.getString("access_token"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    Log.d(G.LOG_TAG, "Register imsi is :" + result);
                }
            });

            if (IMEI != null) {
                sendServerThread.start();
            }
        } else if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> permissions = new ArrayList<String>();
            permissions.add(Manifest.permission.READ_PHONE_STATE);
            if (mContext.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            if (mContext.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            mContext.requestPermissions(permissions.toArray(new String[permissions.size()]), 999);
        }
    }
    
    public static void authentication(Context mContext){
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<String> postName = new ArrayList<String>();
                ArrayList<String> postVal = new ArrayList<String>();

                postName.add("subscriber_id");
                postVal.add("09126421759");
                postName.add("service_code");
                postVal.add("130");
                String result = FetchInternet.getStringFromInternet("http://79.175.155.135:9090/dorsa/dorsa/services/publisher/api/v1/client_authentication/authentication_request", postName, postVal);
                Log.d(G.LOG_TAG,"authentication result---->"+result);
            }
        }).start();
       
    }
}
