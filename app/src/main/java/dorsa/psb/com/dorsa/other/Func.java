package dorsa.psb.com.dorsa.other;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import dorsa.psb.com.dorsa.R;
import dorsa.psb.com.dorsa.adapter.Adapter_All_Apps;
import dorsa.psb.com.dorsa.adapter.Adapter_manage_kid;

/**
 * Created by mehdi on 1/19/16 AD.
 */
public class Func {

   

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            final Dialog mDialog = new Dialog(mContext, android.R.style.Theme_Holo_Dialog_NoActionBar);
            mDialog.setContentView(R.layout.dialog_date_picker);

            final NumberPicker month = (NumberPicker) mDialog.findViewById(R.id.dialog_pick_date_month_picker);
            final NumberPicker years = (NumberPicker) mDialog.findViewById(R.id.dialog_pick_date_year_picker);
            final Button submit = (Button) mDialog.findViewById(R.id.dialog_pick_date_month_submit);


            final String month_name[] = new String[]{"فروردین", "اردیبهشت", "خرداد", "تیر", "مرداد", "شهریور", "مهر", "آبان", "آذر", "دی", "بهمن", "اسفند"};
            month.setMinValue(0);
            month.setMaxValue(11);
            month.setDisplayedValues(month_name);
            if (G.kid_details.selectedValueMonth != -1)
                month.setValue(G.kid_details.selectedValueMonth);

            final String[] Year_value = buildYears(1360, 1395);
            years.setMinValue(0);
            years.setMaxValue(Year_value.length - 1);
            years.setDisplayedValues(Year_value);
            years.setValue(Year_value.length - 2);
            if (G.kid_details.selectedValueYear != -1)
                years.setValue(G.kid_details.selectedValueYear);


            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    G.kid_details.selectedValueMonth = month.getValue();
                    G.kid_details.selectedValueYear = years.getValue();
                    G.kid_details.birthDay = "" + (month_name[month.getValue()] + " " + Year_value[years.getValue()]);
                    hint.setVisibility(View.INVISIBLE);
                    tick_birthday.setVisibility(View.VISIBLE);
                    TXT_date.setText("" + (month_name[month.getValue()] + " " + Year_value[years.getValue()]));
                    G.addChild.birthday="" + (month_name[month.getValue()] + " " + Year_value[years.getValue()]);
                    mDialog.cancel();

                }
            });


            mDialog.show();
        }


    }

    public static void DialogSelectApp(Activity mActivity) {
        final Dialog mDialog = new Dialog(mActivity, android.R.style.Theme_Translucent_NoTitleBar);
        mDialog.setContentView(R.layout.dialog_select_app);

        final RelativeLayout mainRel = (RelativeLayout) mDialog.findViewById(R.id.dialog_ask_select_app_main_rel);
        Animation tv_in = AnimationUtils.loadAnimation(mActivity, R.anim.tv_in);
       final  Animation tv_out = (AnimationUtils.loadAnimation(mActivity, R.anim.tv_out));
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


        RecyclerView mainList = (RecyclerView) mDialog.findViewById(R.id.dialog_select_app_recView);
        if (G.kids_app.Adapter_all_app == null)
            G.kids_app.Adapter_all_app = new Adapter_All_Apps(mActivity);
        mainList.setAdapter(G.kids_app.Adapter_all_app);
        StaggeredGridLayoutManager mStaggeredLayoutManager = new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL);
        mainList.setLayoutManager(mStaggeredLayoutManager);

        Button BTN_cancel = (Button) mDialog.findViewById(R.id.dialog_ask_btn_submit);
        BTN_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainRel.startAnimation(tv_out);
//                mDialog.cancel();
            }
        });


        mDialog.show();
        mainRel.startAnimation(tv_in);
    }

    public static void dialogNewpassword(final Context mContext, final boolean SaveToDb) {
        final Dialog mDialog = new Dialog(mContext, android.R.style.Theme_Holo_Dialog_NoActionBar);
        mDialog.setContentView(R.layout.dialog_ask_password);

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
                    G.Intro.password=pass_1.getText().toString();
                    G.Intro.passmode="0";
                    if(SaveToDb){
                        FetchDb.setSetting(mContext,"password", G.Intro.password);
                        FetchDb.setSetting(mContext,"passmode", G.Intro.passmode);
                    }
                    mDialog.cancel();
                    if(G.mViewPager!=null)G.mViewPager.setCurrentItem(2);
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
    
    public static void dialogChooseKid(Activity mContext){
        final Dialog mDialog = new Dialog(mContext, android.R.style.Theme_Holo_Dialog_NoActionBar);
        mDialog.setContentView(R.layout.dialog_choose_kid);
        G.chooseKid.kidsDetails.clear();
        RecyclerView mainList=(RecyclerView)mDialog.findViewById(R.id.dialog_manage_chid_list);
        JSONArray JArrKids=FetchDb.getAllKids(mContext);
        
        for(int i=0;i<JArrKids.length();i++){
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

    public static class CompoSwitchChangeListner implements CompoundButton.OnCheckedChangeListener{
        private String name;
        private Context mContext;
        
        public CompoSwitchChangeListner(Context context,String mName){
            this.mContext=context;
            this.name=mName;
                    
        }
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            changeSettings(mContext,name,""+isChecked);
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
    
    public static void changeSettings(Context mContext,String name,String dim){
        try {
            JSONObject KidSetting=new JSONObject(G.selectedKid.joKid.getString("settings"));
            KidSetting.put(name,dim);
            G.selectedKid.joKid.put("settings", KidSetting.toString());

            Log.d(G.LOG_TAG, "new Set values change settings ("+name+"):" + KidSetting.toString());
            
            FetchDb.setKidSettings(mContext, G.selectedKid.joKid.getString("ID"), KidSetting.toString());
            
            
            
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    
    public static void saveImage(Bitmap img , String fileName){
        File dir=new File(G.dir);
        if(!dir.exists()){
            dir.mkdirs();
            File output = new File(dir, ".nomedia");
            try {
                boolean fileCreated = output.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        OutputStream outStream = null;
        File file = new File(G.dir, fileName+".PNG");
        try {
            outStream = new FileOutputStream(file);
            img.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();
        } catch(Exception e) {

        }
    }
    
    public static Bitmap getImage(String fileName){
        Bitmap bitmap = BitmapFactory.decodeFile(G.dir+"/"+fileName+".PNG");
        return bitmap;
    }
    
    public static void removeImage(String fileName){
        File file=new File(G.dir+"/"+fileName+".PNG");
        file.delete();
    }

    public static boolean deleteNon_EmptyDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                boolean success = deleteNon_EmptyDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }
}
