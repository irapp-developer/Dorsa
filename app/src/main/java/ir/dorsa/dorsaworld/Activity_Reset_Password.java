package ir.dorsa.dorsaworld;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import ir.dorsa.dorsaworld.fragments.Fragment_Ask_Hint;
import ir.dorsa.dorsaworld.fragments.Fragment_Select_Reset_Pass;
import ir.dorsa.dorsaworld.other.G;

/**
 * Created by mehdi on 2/23/16 AD.
 */
public class Activity_Reset_Password  extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_reset_password);
        getSupportActionBar().hide();
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        G.ResetPass.mViewPager = (ViewPager) findViewById(R.id.ViewPager_reset_password);
        G.ResetPass.mViewPager.setAdapter(mSectionsPagerAdapter);
        
    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {

            switch (i) {
                case 0:
                    return new Fragment_Ask_Hint();
                case 1:
                    return new Fragment_Select_Reset_Pass();
                default:
                    return new Fragment_Ask_Hint();
            }

        }

        @Override
        public int getCount() {
            int pages = 2;
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
}
