package ir.dorsa.dorsaworld;

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

import ir.dorsa.dorsaworld.fragments.Fragment_Add_Child_Details;
import ir.dorsa.dorsaworld.fragments.Fragment_kid_apps;
import ir.dorsa.dorsaworld.fragments.Fragment_kid_img;
import ir.dorsa.dorsaworld.other.FetchDb;
import ir.dorsa.dorsaworld.other.G;

/**
 * Created by mehdi on 1/21/16 AD.
 */
public class Activity_Add_Child extends AppCompatActivity {
    @Override
    public void onBackPressed() {
        if(G.mViewPager.getCurrentItem()==1){
            G.mViewPager.setCurrentItem(0);
        }else if(G.mViewPager.getCurrentItem()==2){
            G.mViewPager.setCurrentItem(1);
        }else if(FetchDb.getKidsCount(this)==0){
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
        }else{
            G.addChild.isEditedMode=false;
            finish();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_run);
        setupActionBar();
        setClouds();
        G.addChild.kidApps.clear();

        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        G.mViewPager = (ViewPager) findViewById(R.id.ViewPager_firstRun);
        G.mViewPager.setAdapter(mSectionsPagerAdapter);
    }
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {

            switch (i) {
                case 0:
                    return new Fragment_Add_Child_Details();
                case 1:
                    return new Fragment_kid_img();
                case 2:
                    return new Fragment_kid_apps();
                default:
                    return new Fragment_Add_Child_Details();
            }

        }

        @Override
        public int getCount() {
            int pages = 3;
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
