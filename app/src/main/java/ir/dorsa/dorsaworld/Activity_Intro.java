package ir.dorsa.dorsaworld;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import ir.dorsa.dorsaworld.fragments.Fragment_Hint;
import ir.dorsa.dorsaworld.fragments.Fragment_Select_Pass;
import ir.dorsa.dorsaworld.fragments.Fragment_policy;
import ir.dorsa.dorsaworld.fragments.Fragment_set_task;
import ir.dorsa.dorsaworld.other.Func;
import ir.dorsa.dorsaworld.other.G;

/**
 * Created by mehdi on 1/19/16 AD.
 */
public class Activity_Intro extends AppCompatActivity {

private Tracker mTracker;
    @Override
    public void onBackPressed() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("خروج از برنامه");
        dialog.setMessage("مایل به خروج از برنامه می باشید ؟");
        dialog.setPositiveButton("بله", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                setResult(RESULT_CANCELED);
                finish();

            }
        });
        dialog.setNegativeButton("خیر", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {

            }
        });
        dialog.show();


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_run);

        // make anallytics //
        App application = (App) getApplication();
        mTracker = application.getDefaultTracker();
        mTracker.enableAdvertisingIdCollection(true);
        
        setupActionBar();
        setClouds();
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        G.mViewPager = (ViewPager) findViewById(R.id.ViewPager_firstRun);
        G.mViewPager.setAdapter(mSectionsPagerAdapter);
      
        Intent intent=getIntent();
        String TaskMode=intent.getStringExtra("TaskMode");
        if(TaskMode!=null) {
            if (TaskMode.equals("true")) {
                G.mViewPager.setCurrentItem(3, false);
            }
        }

        
        
       /* if (!FetchDb.getSetting(this, "password").equals("-1")) {
            G.mViewPager.setCurrentItem(3, false);
        }else if(){
            
        }*/
        //check witch section is completed
    }

    @Override
    protected void onResume() {
        mTracker.setScreenName("Image~راهنمایی اولیه");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        super.onResume();
    }
    
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {

            switch (i) {
                case 0:
                    return new Fragment_policy();
                case 1:
                    return new Fragment_Select_Pass();
                case 2:
                    return new Fragment_Hint();
                case 3:
                    return new Fragment_set_task();
             /*   case 3:
                    return new Fragment_select_launcher();*/

                default:
                    return new Fragment_policy();
            }

        }

        @Override
        public int getCount() {
            int pages;
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//Check system version and 
                if (!Func.isUsageStateEnable(Activity_Intro.this)) {//Check is access to read task ok
                     pages = 4;
                }else{
                     pages = 3;
                }
            }else{
                 pages = 3;
            }

            
            return pages;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            switch (position) {
//	            case 0: return (getResources().getText(R.string.tones));
//	            case 1: return(getResources().getText(R.string.browse));
            }

            return null;
        }
    }

    private void setClouds() {
        final ImageView baloon = (ImageView) findViewById(R.id.baloon);
        final Animation anim_baloon = AnimationUtils.loadAnimation(this, R.anim.anim_balloon);
        anim_baloon.setDuration(50000);
        baloon.startAnimation(anim_baloon);

    }

    private void setupActionBar() {
        getSupportActionBar().hide();
    }
}
