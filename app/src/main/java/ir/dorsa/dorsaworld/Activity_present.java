package ir.dorsa.dorsaworld;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class Activity_present extends AppCompatActivity {

    private Tracker mTracker;


    @Override
    protected void onResume() {
        mTracker.setScreenName("Image~تنظیمات دنیای درسا");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        super.onResume();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_present);

        // make anallytics //
        App application = (App) getApplication();
        mTracker = application.getDefaultTracker();
        mTracker.enableAdvertisingIdCollection(true);
        
        final ViewPager mViewPager= (ViewPager) findViewById(R.id.act_present_viewpager);
        mViewPager.setAdapter(new SectionsPagerAdapter(getSupportFragmentManager()));
        
        getSupportActionBar().hide();

        final ImageView next=(ImageView)findViewById(R.id.act_present_next);
        final TextView start=(TextView)findViewById(R.id.act_present_start);
//        final TextView start_2=(TextView)findViewById(R.id.act_present_start_2);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(Activity.RESULT_OK);
                finish();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mViewPager.getCurrentItem()<7){
                    mViewPager.setCurrentItem(mViewPager.getCurrentItem()+1,true);
                }
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                
            }

            @Override
            public void onPageSelected(int position) {
                next.setVisibility(View.VISIBLE);
                start.setVisibility(View.GONE);
                
                
            switch (position){
                case(4):
                    next.setVisibility(View.GONE);
                    start.setVisibility(View.VISIBLE);
                    break;
            }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#467ca0"));
        }
        
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            Fragment fragment = new DummySectionFragment();
            Bundle args = new Bundle();
            args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, i );
            fragment.setArguments(args);

            return fragment;
        }

        @Override
        public int getCount() {
            int pages=5;
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
    
    public static class DummySectionFragment extends Fragment {
        public DummySectionFragment() {
        }

        public static final String ARG_SECTION_NUMBER = "section_number";

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            Bundle args = getArguments();
            int index=(args.getInt(ARG_SECTION_NUMBER));
            View mainRel=inflater.inflate(R.layout.fragment_present_0,null);
            
            if(index==0){
                mainRel=inflater.inflate(R.layout.fragment_present_0,null);
            }else if(index==1){
                mainRel=inflater.inflate(R.layout.fragment_present_1,null);
            }else if(index==2){
                mainRel=inflater.inflate(R.layout.fragment_present_2,null);
            }  else if(index==3){
                mainRel=inflater.inflate(R.layout.fragment_present_3,null);
            }else if(index==4){
                mainRel=inflater.inflate(R.layout.fragment_present_4,null);
            } 
            
            
            return mainRel;
        }
    }
}
