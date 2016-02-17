package dorsa.psb.com.dorsa;

import android.content.DialogInterface;
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

import dorsa.psb.com.dorsa.fragments.Fragment_Hint;
import dorsa.psb.com.dorsa.fragments.Fragment_Select_Pass;
import dorsa.psb.com.dorsa.fragments.Fragment_policy;
import dorsa.psb.com.dorsa.fragments.Fragment_select_launcher;
import dorsa.psb.com.dorsa.other.FetchDb;
import dorsa.psb.com.dorsa.other.G;

/**
 * Created by mehdi on 1/19/16 AD.
 */
public class Activity_Intro extends AppCompatActivity {


    @Override
    public void onBackPressed() {
        final  AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("خروج از برنامه");
        dialog.setMessage("مایل به حروج از برنامه می باشید ؟");
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
        setupActionBar();
        setClouds();
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        G.mViewPager = (ViewPager) findViewById(R.id.ViewPager_firstRun);
        G.mViewPager.setAdapter(mSectionsPagerAdapter);
        if(!FetchDb.getSetting(this,"password").equals("-1")){
            G.mViewPager.setCurrentItem(3,false);
        }
        //check witch section is completed
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
                    return new Fragment_select_launcher();

                default:
                    return new Fragment_policy();
            }

        }

        @Override
        public int getCount() {
            int pages = 4;
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
    private void setClouds(){
        final ImageView baloon=(ImageView)findViewById(R.id.baloon);
        final Animation anim_baloon= AnimationUtils.loadAnimation(this, R.anim.anim_balloon);
        anim_baloon.setDuration(50000);
        baloon.startAnimation(anim_baloon);

    }
    private void setupActionBar(){
        getSupportActionBar().hide();
    }
}
