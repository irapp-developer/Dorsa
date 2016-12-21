package ir.dorsa.dorsaworld;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import ir.dorsa.dorsaworld.fragments.Fragment_Kid_Applications;
import ir.dorsa.dorsaworld.fragments.Fragment_Kid_Setting;
import ir.dorsa.dorsaworld.other.CalTool;
import ir.dorsa.dorsaworld.other.CustomTypefaceSpan;
import ir.dorsa.dorsaworld.other.FetchDb;
import ir.dorsa.dorsaworld.other.Func;
import ir.dorsa.dorsaworld.other.G;
import ir.dorsa.dorsaworld.other.Permisions;

/**
 * Created by mehdi on 1/24/16 AD.
 */
public class Activity_Parent extends AppCompatActivity {

   /* PackageManager p;
    ComponentName cN;*/

    private static TabLayout tabLayout;
    private static ViewPager viewPager;
    private static ViewPagerAdapter adapter;

    private static int REQUEST_PRESENT = 946;


    private static final int REQUEST_ENABLE_USAGE_STATE = 8573;

    private Tracker mTracker;

    private static Activity THIS;

    @Override
    public void onBackPressed() {

       /* if (FetchDb.getKidsCount(this) > 0) {
            try {
                G.selectedKid.joKid = FetchDb.getSelectedKid(this);
                JSONObject jo = new JSONObject(G.selectedKid.joKid.getString("settings"));
                if (jo.getString("isEnable").equals("false")) {// start kid app afret exit
                    Intent intent = new Intent(this, launcher.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getApplicationContext().startActivity(intent);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }*/
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
//        Permisions.setDisablePermisions(this);
        mTracker.setScreenName("Image~تنظیمات دنیای درسا");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
    /*Save your data to be restored here
    Example : outState.putLong("time_state", time); , time is a long variable*/
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent);

        THIS = this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Func.authentication(this);
        // make anallytics //
        App application = (App) getApplication();
        mTracker = application.getDefaultTracker();
        mTracker.enableAdvertisingIdCollection(true);

        Func.checkVersion(App.getContext());
        if (!FetchDb.getSetting(this, "phonenumber").equals("-1") & FetchDb.getSetting(this, "register_imsi").equals("-1")) {
            Func.registerImsi(this);
        }

//        FetchDb.EditDB(this);
        setupActionBar();

//        Thread.setDefaultUncaughtExceptionHandler(new UnCaughtException(Activity_Parent.this));


        G.mActivity = this;
        viewPager = (ViewPager) G.mActivity.findViewById(R.id.viewPager);
        tabLayout = (TabLayout) G.mActivity.findViewById(R.id.tabs);
        adapter = new ViewPagerAdapter(G.mActivity.getSupportFragmentManager());

        String switchParent = getIntent().getStringExtra("switchParent");
        if (switchParent == null) switchParent = "false";

        if ("true".equals(switchParent)) {
            launchlauncher();
        } else if (FetchDb.getSetting(this, "present").equals("false")) {
            startActivityForResult(new Intent(this, Activity_present.class), REQUEST_PRESENT);
        } else if (FetchDb.getSetting(this, "password").equals("-1")) {
            startActivityForResult(new Intent(this, Activity_Intro.class), G.REQUEST_INTRO);
        } else if (FetchDb.getSetting(this, "passmode").equals("0")) {//have to start activity password
            startActivityForResult(new Intent(this, Activity_Get_Password.class), G.REQUEST_PASSWORD);
        } else {
            startActivityForResult(new Intent(this, Activity_GetPattern.class), G.REQUEST_PASSWORD);
        }


        ImageView calendar = (ImageView) findViewById(R.id.act_parent_child_btn_btn_schedual);
        calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (G.kids_app.KidAppResolver.size() > 0) {
                    startActivity(new Intent(G.mActivity, Activity_Calendar.class));
                } else {
                    Toast.makeText(Activity_Parent.this, "ابتدا برای کودک برنامه تعیین نمایید", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.parent, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            startActivity(new Intent(this, Activity_Parent_Settings.class));
        } else if (item.getItemId() == R.id.action_about) {
            Func.dialogAbout(this);
        } else if (item.getItemId() == R.id.action_contact) {
            Func.dialogContact(this);
        } else if (item.getItemId() == R.id.action_present) {
//            startActivity(new Intent(this, Activity_present.class));
            startActivity(new Intent(this, Activity_Parent_Hint.class));
        }
        return true;
    }

    private static void setupToolbar() {
        TextView childName = (TextView) G.mActivity.findViewById(R.id.act_parent_child_name);
        TextView childAge = (TextView) G.mActivity.findViewById(R.id.act_parent_child_age);
        ImageView kidAvatar = (ImageView) G.mActivity.findViewById(R.id.act_parent_child_icon);
        ImageView switchKidMode = (ImageView) G.mActivity.findViewById(R.id.act_parent_switch_kid);

        View switchKid = G.mActivity.findViewById(R.id.act_parent_child_switch_button);


        switchKidMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                G.mActivity.startActivity(new Intent(G.mActivity, Activity_Launcher_Kid.class));
                G.mActivity.finish();
            }
        });

        switchKid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Func.dialogChooseKid(G.mActivity, G.selectedKid.joKid.getString("sex"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        try {//-------- 
            changeTabColors();
            String name = G.selectedKid.joKid.getString("name");
            childName.setText(name);
            kidAvatar.setImageBitmap(Func.getImage("Avatar_" + G.selectedKid.joKid.getString("ID")));
            String birthday = G.selectedKid.joKid.getString("birthday");
            birthday = birthday.substring(birthday.length() - 4)
                    .replace("۰", "0")
                    .replace("۱", "1")
                    .replace("۲", "2")
                    .replace("۳", "3")
                    .replace("۴", "4")
                    .replace("۵", "5")
                    .replace("۶", "6")
                    .replace("۷", "7")
                    .replace("۸", "8")
                    .replace("۹", "9")
            ;

            CalTool cal=new CalTool();
            int birth=cal.getIranianYear()-Integer.parseInt(birthday);
            if(birth>0){
                childAge.setText(birth+" ساله");
                childAge.setVisibility(View.VISIBLE);
            }else{
                childAge.setVisibility(View.GONE);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static void setupViewPager() {

        G.kids_app.AllAppResolver.clear();
        G.kids_app.KidAppResolver.clear();

        if (adapter.getCount() == 0) {
            adapter.addFragment(new Fragment_Kid_Applications(), "برنامه های کودک");
            adapter.addFragment(new Fragment_Kid_Setting(), "تنظیمات دسترسی");
            viewPager.setAdapter(adapter);

            tabLayout.setupWithViewPager(viewPager);
            changeTabColors();
            viewPager.setCurrentItem(1, false);
        } else {
            ((Fragment_Kid_Applications) adapter.getItem(0)).refreShApps();
            ((Fragment_Kid_Applications) adapter.getItem(0)).setupViews();
            ((Fragment_Kid_Setting) adapter.getItem(1)).setValues();
        }
    }

    private static void changeTabColors() {
        String sexMode = "1";
        try {
            sexMode = G.selectedKid.joKid.getString("sex");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RelativeLayout bgr_main = (RelativeLayout) THIS.findViewById(R.id.act_parent_child_profile);
        if ("0".equals(sexMode)) {//boy
            bgr_main.setBackgroundResource(R.drawable.boy_bgr);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = THIS.getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(ContextCompat.getColor(THIS, R.color.Color_blue_accent));
            }
        } else {//girl
            bgr_main.setBackgroundResource(R.drawable.girly_bgr);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = THIS.getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(ContextCompat.getColor(THIS, R.color.Color_red_accent));
            }
        }

        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            //android:id="@+id/tab_main_rel_left"
            //android:id="@+id/tab_main_rel_right"


            if (tabLayout.getTabAt(i).getCustomView() == null) {
                TabLayout.Tab tab = tabLayout.getTabAt(i);
                RelativeLayout relativeLayout = (RelativeLayout) LayoutInflater.from(G.mActivity).inflate(R.layout.tab_left_design_girl, tabLayout, false);
                if ("1".equals(sexMode) && i == 0) {
                    relativeLayout = (RelativeLayout) LayoutInflater.from(G.mActivity).inflate(R.layout.tab_left_design_girl, tabLayout, false);
                } else if ("1".equals(sexMode) && i == 1) {
                    relativeLayout = (RelativeLayout) LayoutInflater.from(G.mActivity).inflate(R.layout.tab_right_design_girl, tabLayout, false);
                } else if ("0".equals(sexMode) && i == 0) {
                    relativeLayout = (RelativeLayout) LayoutInflater.from(G.mActivity).inflate(R.layout.tab_left_design_boy, tabLayout, false);
                } else if ("0".equals(sexMode) && i == 1) {
                    relativeLayout = (RelativeLayout) LayoutInflater.from(G.mActivity).inflate(R.layout.tab_right_design_boy, tabLayout, false);
                }
                TextView tabTextView = (TextView) relativeLayout.findViewById(R.id.tab_title);
                tabTextView.setText(tab.getText());
                tab.setCustomView(relativeLayout);
            } else {
                RelativeLayout rightRel = (RelativeLayout) tabLayout.getTabAt(i).getCustomView().findViewById(R.id.tab_main_rel_right);
                RelativeLayout leftRel = (RelativeLayout) tabLayout.getTabAt(i).getCustomView().findViewById(R.id.tab_main_rel_left);
                if ("1".equals(sexMode) && i == 0) {
                    leftRel.setBackgroundResource(R.drawable.bgr_tab_left_girl);
                } else if ("1".equals(sexMode) && i == 1) {
                    rightRel.setBackgroundResource(R.drawable.bgr_tab_right_girl);
                } else if ("0".equals(sexMode) && i == 0) {
                    leftRel.setBackgroundResource(R.drawable.bgr_tab_left_boy);
                } else if ("0".equals(sexMode) && i == 1) {
                    rightRel.setBackgroundResource(R.drawable.bgr_tab_right_boy);
                }
            }
        }

    }

    static class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
            mFragmentList.clear();
            mFragmentTitleList.clear();
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);//text and icon
            // return null;//only icon
        }

    }

    private static void changeTabsFont() {
        Typeface FONT_NAME = Typeface.createFromAsset(G.mActivity.getAssets(), "iran_sans_lightt.ttf");
        ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);
        int tabsCount = vg.getChildCount();
        for (int j = 0; j < tabsCount; j++) {
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
            int tabChildsCount = vgTab.getChildCount();
            for (int i = 0; i < tabChildsCount; i++) {
                View tabViewChild = vgTab.getChildAt(i);
                if (tabViewChild instanceof TextView) {
                    ((TextView) tabViewChild).setTypeface(FONT_NAME);
                }
            }
        }
    }

    public static void notifyParentPage() {

        //if kids count==0 start add kid
        //else get slected kid id and notify page
        if (FetchDb.getKidsCount(G.mActivity) == 0) {
            G.mActivity.startActivityForResult(new Intent(G.mActivity, Activity_Add_Child.class), G.REQUEST_CHILD);
        } else {
            G.selectedKid.joKid = FetchDb.getSelectedKid(G.mActivity);
            setupToolbar();
            setupViewPager();
            changeTabsFont();
            if (!"true".equals(FetchDb.getSetting(THIS, "parent_hint"))) {
                FetchDb.setSetting(THIS, "parent_hint", "true");
                THIS.startActivity(new Intent(THIS, Activity_Parent_Hint.class));
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == G.REQUEST_INTRO) {
            if (resultCode == Activity.RESULT_OK) {
                checkPermmisions();
                if (!FetchDb.getSetting(this, "phonenumber").equals("-1") & FetchDb.getSetting(this, "register_imsi").equals("-1")) {
                    Func.registerImsi(this);
                }

            } else {
                this.onBackPressed();
            }
            Log.d(G.LOG_TAG, "Result Parrent start is :" + requestCode);
        } else if (requestCode == G.REQUEST_PASSWORD) {
            if (resultCode == Activity.RESULT_OK) {
                launchlauncher();
            } else {
                this.onBackPressed();
            }
        } else if (requestCode == G.REQUEST_CHILD) {
            if (resultCode == Activity.RESULT_OK) {
                checkPermmisions();
            } else {
                if (FetchDb.getKidsCount(this) == 0) this.onBackPressed();
            }
        } else if (requestCode == REQUEST_ENABLE_USAGE_STATE) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (!Func.isUsageStateEnable(this)) {
                    final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                    dialog.setTitle("کنترل برنامه ها");
                    dialog.setMessage("برای اجرای صحیح برنامه بایستی دسترسی لازم به برنامه داده شود.\nددر صفحه ای هدایت می شوید گزینه درسا را فعال نمایید");
                    dialog.setPositiveButton("تایید", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivityForResult(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS), REQUEST_ENABLE_USAGE_STATE);
                        }
                    });
                    dialog.setNegativeButton("نیازی نیست", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    dialog.show();
                }
            }
        } else if (requestCode == G.REQUEST_ACCESS_TASK) {
            if (resultCode == Activity.RESULT_OK) {
                checkPermmisions();
            } else {
                this.onBackPressed();
            }
        } else if (requestCode == REQUEST_PRESENT) {
            if (resultCode == Activity.RESULT_OK) {
                //set db present is ok
                FetchDb.setSetting(this, "present", "true");
                if (FetchDb.getSetting(this, "password").equals("-1")) {
                    startActivityForResult(new Intent(this, Activity_Intro.class), G.REQUEST_INTRO);
                } else if (FetchDb.getSetting(this, "passmode").equals("0")) {//have to start activity password
                    startActivityForResult(new Intent(this, Activity_Get_Password.class), G.REQUEST_PASSWORD);
                } else {
                    startActivityForResult(new Intent(this, Activity_GetPattern.class), G.REQUEST_PASSWORD);
                }
            } else {
                this.onBackPressed();
            }
        }
    }

    private void checkPermmisions() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> permissions = new ArrayList<String>();
            boolean phoneStatePer = true;
            boolean messagePer = true;
            boolean StoragePer = true;


            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.READ_PHONE_STATE);
                phoneStatePer = false;
            }
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                StoragePer = false;
            }
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
                StoragePer = false;
            }

            if (checkSelfPermission(Manifest.permission.RECEIVE_SMS)
                    != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.RECEIVE_SMS);
                messagePer = false;
            }

            if (checkSelfPermission(Manifest.permission.READ_SMS)
                    != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.READ_SMS);
                messagePer = false;
            }


            if (!messagePer || !StoragePer || !phoneStatePer) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), 154);
            } else {
                notifyParentPage();
            }

        } else {
            notifyParentPage();
        }
    }

    private void launchlauncher() {
        Permisions.setDisablePermisions(this);
        checkPermmisions();
        if (!FetchDb.getSetting(this, "phonenumber").equals("-1") & FetchDb.getSetting(this, "register_imsi").equals("-1")) {
            Func.registerImsi(this);
        }
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (!Func.isUsageStateEnable(this)) {///
                Intent intent = new Intent(this, Activity_Intro.class);
                intent.putExtra("TaskMode", "true");
                startActivityForResult(intent, G.REQUEST_ACCESS_TASK);
            }
        }
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        Typeface font = Typeface.createFromAsset(getAssets(), "iran_sans_lightt.ttf");
        SpannableStringBuilder SS_all = new SpannableStringBuilder("تنظیمات دنیای درسا");
        SS_all.setSpan(new CustomTypefaceSpan("", font, Color.parseColor("#ffffff"), Func.dpToPx(this, 14)), 0, SS_all.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        actionBar.setTitle(SS_all);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissionss, int[] grantResults) {
        if (requestCode == 154) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                final ArrayList<String> permissions = new ArrayList<String>();

                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        permissions.add(permissionss[i]);
                        Log.d(G.LOG_TAG, "lost permission is :" + permissionss[i]);

                    }
                }


                if (permissions.size() == 0) {
                    notifyParentPage();
                } else {
                    final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                    dialog.setMessage("برای اجرای برنامه بایستی به موارد خواسته شده اجازه دسترسی داده شود");
                    dialog.setTitle("اجازه دسترسی");
                    dialog.setPositiveButton("شروع", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                Activity_Parent.this.requestPermissions(permissions.toArray(new String[permissions.size()]), 154);
                            }
                        }
                    });
                    dialog.setNegativeButton("خروج", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            onBackPressed();
                        }
                    });
                    dialog.setCancelable(false);
                    dialog.show();
                }
            }
        }
//        super.onRequestPermissionsResult(requestCode, permissionss, grantResults);
    }
}
