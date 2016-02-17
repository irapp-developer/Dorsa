package dorsa.psb.com.dorsa;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import dorsa.psb.com.dorsa.fragments.Fragment_Kid_Applications;
import dorsa.psb.com.dorsa.fragments.Fragment_Kid_Setting;
import dorsa.psb.com.dorsa.other.FetchDb;
import dorsa.psb.com.dorsa.other.Func;
import dorsa.psb.com.dorsa.other.G;
import dorsa.psb.com.dorsa.other.Permisions;

/**
 * Created by mehdi on 1/24/16 AD.
 */
public class Activity_Parent extends AppCompatActivity {

    PackageManager p;
    ComponentName cN;

    private static TabLayout tabLayout;
    private static ViewPager viewPager;
    private static ViewPagerAdapter adapter;

    static AppCompatActivity mActivity;
    @Override
    protected void onStart() {
        super.onStart();
        p = getApplicationContext().getPackageManager();
        cN = new ComponentName(getApplicationContext(), dorsa.psb.com.dorsa.launcher.class);
        p.setComponentEnabledSetting(cN, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }

    @Override
    public void onBackPressed() {
        Log.d(G.LOG_TAG, "on back press");
        if (!Func.getDefaultLauncher(this).equals(this.getPackageName())) {
            Log.d(G.LOG_TAG, "Clear launcher");
            p.setComponentEnabledSetting(cN, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        }
        if(FetchDb.getKidsCount(this)>0) {
            Intent intent = new Intent(this, launcher.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplicationContext().startActivity(intent);
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        Log.d(G.LOG_TAG,"Destroy happend");
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        Permisions.setParrentPermisions(this);
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent);
        mActivity=this;
        viewPager = (ViewPager) mActivity.findViewById(R.id.viewPager);
        tabLayout = (TabLayout) mActivity.findViewById(R.id.tabs);
        adapter= new ViewPagerAdapter(mActivity.getSupportFragmentManager());
        if (FetchDb.getSetting(this, "password").equals("-1") || !Func.getDefaultLauncher(this).equals(this.getPackageName())) {
            startActivityForResult(new Intent(this, Activity_Intro.class), G.REQUEST_INTRO);
        } else if (FetchDb.getSetting(this, "passmode").equals("0")) {//have to start activity password
            startActivityForResult(new Intent(this, Activity_Get_Password.class), G.REQUEST_PASSWORD);
        } else {
            startActivityForResult(new Intent(this, Activity_GetPattern.class), G.REQUEST_PASSWORD);
        }

        TextView newKid = (TextView) findViewById(R.id.act_parent_child_change);
        newKid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(Activity_Parent.this, Activity_Add_Child.class), G.REQUEST_CHILD);
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
        if(item.getItemId()==R.id.action_settings){
            startActivity(new Intent(this,Activity_Parent_Settings.class));     
        }
        return true;
    }
       
    private static void setupToolbar() {
        SwitchCompat isDeviceEnable = (SwitchCompat) mActivity.findViewById(R.id.act_parent_child_switch_enable);
        TextView childName = (TextView) mActivity.findViewById(R.id.act_parent_child_name);
        isDeviceEnable.setOnCheckedChangeListener(null);
        ImageView kidAvatar = (ImageView) mActivity.findViewById(R.id.act_parent_child_icon);
        TextView switchKid = (TextView) mActivity.findViewById(R.id.act_parent_child_change);

        switchKid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Func.dialogChooseKid(mActivity);
            }
        });

        try {
            String name = G.selectedKid.joKid.getString("name");
            JSONObject joSettings = new JSONObject(G.selectedKid.joKid.getString("settings"));
            Boolean isEnable = Boolean.parseBoolean(joSettings.getString("isEnable"));
            isDeviceEnable.setChecked(isEnable);
            isDeviceEnable.setOnCheckedChangeListener(new Func.CompoSwitchChangeListner(mActivity, "isEnable"));
            childName.setText(name);
            kidAvatar.setImageBitmap(Func.getImage("Avatar_" + G.selectedKid.joKid.getString("ID")));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static void setupViewPager() {

        G.kids_app.AllAppResolver.clear();
        G.kids_app.KidAppResolver.clear();
        
        if(adapter.getCount()==0) {
            adapter.addFragment(new Fragment_Kid_Applications(), "برنامه های کودک");
            adapter.addFragment(new Fragment_Kid_Setting(), "تنظیمات دسترسی");
            viewPager.setAdapter(adapter);

            tabLayout.setupWithViewPager(viewPager);
            viewPager.setCurrentItem(1, false);

        }else {
            ((Fragment_Kid_Applications) adapter.getItem(0)).setupViews();
            ((Fragment_Kid_Setting) adapter.getItem(1)).setValues();
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
        Typeface FONT_NAME = Typeface.createFromAsset(mActivity.getAssets(), "iran_sans_lightt.ttf");
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
        if (FetchDb.getKidsCount(mActivity) == 0) {
            mActivity.startActivityForResult(new Intent(mActivity, Activity_Add_Child.class), G.REQUEST_CHILD);
        } else {
            G.selectedKid.joKid = FetchDb.getSelectedKid(mActivity);
         


            setupToolbar();
            setupViewPager();
            changeTabsFont();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        
        if (requestCode == G.REQUEST_INTRO) {
            if (resultCode == Activity.RESULT_OK) {
                notifyParentPage();

            } else {
                this.onBackPressed();
            }
            Log.d(G.LOG_TAG, "Result Parrent start is :" + requestCode);
        } else if (requestCode == G.REQUEST_PASSWORD) {
            if (resultCode == Activity.RESULT_OK) {
                notifyParentPage();
            } else {
                this.onBackPressed();
            }
        } else if (requestCode == G.REQUEST_CHILD) {
            if (resultCode == Activity.RESULT_OK) {
                notifyParentPage();
            } else {
                if (FetchDb.getKidsCount(this) == 0) this.onBackPressed();
            }
        }
    }


}
