import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import ir.dorsa.dorsaworld.R;

/**
 * Created by mehdi on 3/14/16 AD.
 */
public class Activity_Intro_Schedual extends AppCompatActivity {
private static ViewPager mViewPager;
    private static Activity mActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivity=this;
        mViewPager=(ViewPager)findViewById(R.id.act_intro_schedual_view_pager);
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            Fragment fragment = new DummySectionFragment();
            Bundle args = new Bundle();
            args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, i + 1);
            fragment.setArguments(args);

            return fragment;
        }

        @Override
        public int getCount() {
            int pages=3;
            return pages;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            switch (position) {
//            case 0: return (getResources().getText(R.string.tones));
//            case 1: return(getResources().getText(R.string.browse));
            }

            return null;
        }
    }
    
    public  static class DummySectionFragment extends Fragment {
        public DummySectionFragment() {
        }

        public static final String ARG_SECTION_NUMBER = "section_number";

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            Bundle args = getArguments();
            int index=(args.getInt(ARG_SECTION_NUMBER));
            View mainRel=inflater.inflate(R.layout.fragmrnt_intro_schedual_1,null);
             if(index==1){
                 mainRel=inflater.inflate(R.layout.fragmrnt_intro_schedual_1,null);
                 Button btn=(Button)mainRel.findViewById(R.id.frg_intro_schedual_daily_btn_next);
                 btn.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View view) {
                         mViewPager.setCurrentItem(1,false);
                     }
                 });
                 
            }else if(index==2){
                 mainRel=inflater.inflate(R.layout.fragmrnt_intro_schedual_2,null);
                 Button btn=(Button)mainRel.findViewById(R.id.frg_intro_schedual_daily_btn_next);
                 btn.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View view) {
                         mViewPager.setCurrentItem(1,false);
                     }
                 });
            }else {
                 mainRel=inflater.inflate(R.layout.fragmrnt_intro_schedual_3,null);
                 Button btn=(Button)mainRel.findViewById(R.id.frg_intro_schedual_daily_btn_next);
                 btn.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View view) {
                         mActivity.setResult(Activity.RESULT_OK);
                         mActivity.finish();
                     }
                 });
             }
            return mainRel;
        }
    }
}
