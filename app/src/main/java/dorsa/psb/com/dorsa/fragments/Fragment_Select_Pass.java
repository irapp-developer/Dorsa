package dorsa.psb.com.dorsa.fragments;

import android.app.Activity;
import android.content.Intent;
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

import dorsa.psb.com.dorsa.Activity_pattern;
import dorsa.psb.com.dorsa.R;
import dorsa.psb.com.dorsa.other.Func;
import dorsa.psb.com.dorsa.other.G;

/**
 * Created by mehdi on 1/19/16 AD.
 */
public class Fragment_Select_Pass extends Fragment {
    private View pView;
    private int REQUEST_CODE_PATTERN=123;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        pView = inflater.inflate(R.layout.fragment_select_password, container, false);
        ImageView monster_3_eye=(ImageView)pView.findViewById(R.id.monster_3_eye);

        Button BTN_pattern=(Button)pView.findViewById(R.id.frg_select_pass_btn_pattern);
        Button BTN_password=(Button)pView.findViewById(R.id.frg_select_pass_btn_text);
        
        
        final RelativeLayout monster_4=(RelativeLayout)pView.findViewById(R.id.monster_4_rel);
        final Animation anim_scale= AnimationUtils.loadAnimation(getActivity(), R.anim.anim_scale_body);
       
            
            anim_scale.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                  
                      new Handler().postDelayed(new Runnable() {
                          @Override
                          public void run() {
                             if(getActivity()!=null) {
                                 getActivity().runOnUiThread(new Runnable() {
                                     @Override
                                     public void run() {
                                         monster_4.startAnimation(anim_scale);
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
       
        monster_4.startAnimation(anim_scale);
        ((AnimationDrawable) monster_3_eye.getDrawable()).start();
        
        
        BTN_pattern.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment_Select_Pass.this.startActivityForResult(new Intent(getActivity(), Activity_pattern.class), REQUEST_CODE_PATTERN);
            }
        });

        BTN_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              Func.dialogNewpassword(getActivity(),false);
            }
        });
        return pView;
    }

    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CODE_PATTERN) {
            if (resultCode == Activity.RESULT_OK) {
                G.mViewPager.setCurrentItem(2);
            }
        }
    }
}
