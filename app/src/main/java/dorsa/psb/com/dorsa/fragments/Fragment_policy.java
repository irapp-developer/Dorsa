package dorsa.psb.com.dorsa.fragments;

import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import dorsa.psb.com.dorsa.R;
import dorsa.psb.com.dorsa.other.G;

public class Fragment_policy extends Fragment {
    PackageManager p;
    ComponentName cN;
    private View pView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        pView = inflater.inflate(R.layout.fragment_policy, container, false);

        //------- start animations --------

        final Animation anim_jump= AnimationUtils.loadAnimation(getActivity(),R.anim.anim_jump);
        final Animation anim_shadow= AnimationUtils.loadAnimation(getActivity(),R.anim.anim_scale_shadow);

        final RelativeLayout bug_rel=(RelativeLayout)pView.findViewById(R.id.frg_intro_1_bug_rel);
        final ImageView bug_eye=(ImageView)pView.findViewById(R.id.frg_intro_1_bug_eye);
        final ImageView monster_2_eye=(ImageView)pView.findViewById(R.id.monster_2_eye);
        final ImageView bug_shadow=(ImageView)pView.findViewById(R.id.frg_intro_1_bug_shadow);

        Button btn_accept=(Button)pView.findViewById(R.id.frg_intro_1_accept);

        btn_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(G.mViewPager!=null)G.mViewPager.setCurrentItem(1);
            }
        });

        anim_jump.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                ((AnimationDrawable) bug_eye.getDrawable()).stop();
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                
                    if(bug_eye!=null)((AnimationDrawable) bug_eye.getDrawable()).start();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(getActivity()!=null) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        bug_rel.startAnimation(anim_jump);
                                        bug_shadow.startAnimation(anim_shadow);
                                    }
                                });
                            }
                        }
                    }, 4000);
                
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        bug_rel.startAnimation(anim_jump);
        bug_shadow.startAnimation(anim_shadow);
        ((AnimationDrawable) bug_eye.getDrawable()).start();
        ((AnimationDrawable) monster_2_eye.getDrawable()).start();
        
        return pView;
    }
    
    
   
        
       /* Button btn = (Button) findViewById(R.id.button);
         p = getApplicationContext().getPackageManager();
        cN= new ComponentName(getApplicationContext(), launcher.class);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               *//* getPackageManager().clearPackagePreferredActivities(getPackageName());
                MainActivity.this.finish();*//*

            *//*    Intent startMain = new Intent(Intent.ACTION_MAIN);
//                startMain.addCategory("AAA");
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startMain);*//*


                p.setComponentEnabledSetting(cN, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

                Intent selector = new Intent(Intent.ACTION_MAIN);
                selector.addCategory(Intent.CATEGORY_HOME);
                selector.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(selector);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        p.setComponentEnabledSetting(cN, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
                    }
                }, 100);

            }
        });*/
   
   
}
