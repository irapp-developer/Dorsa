package ir.dorsa.dorsaworld.fragments;

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

import ir.dorsa.dorsaworld.Activity_pattern;
import ir.dorsa.dorsaworld.R;
import ir.dorsa.dorsaworld.other.FetchDb;
import ir.dorsa.dorsaworld.other.Func;

/**
 * Created by mehdi on 1/19/16 AD.
 */
public class Fragment_Select_Reset_Pass extends Fragment {
    private View pView;
    private int REQUEST_CODE_PATTERN=947;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        pView = inflater.inflate(R.layout.fragment_select_password, container, false);
        ImageView monster_3_eye=(ImageView)pView.findViewById(R.id.monster_3_eye);

        ImageView BTN_pattern=(ImageView)pView.findViewById(R.id.frg_select_pass_btn_pattern);
        ImageView BTN_password=(ImageView)pView.findViewById(R.id.frg_select_pass_btn_text);
        
        
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
                Fragment_Select_Reset_Pass.this.startActivityForResult(new Intent(getActivity(), Activity_pattern.class), REQUEST_CODE_PATTERN);
            }
        });

        BTN_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              Func.dialogResetPassword(getActivity());
            }
        });
        return pView;
    }

    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CODE_PATTERN) {
            if (resultCode == Activity.RESULT_OK & data!=null) {
                String pass=data.getStringExtra("pass");
                FetchDb.setSetting(getActivity(), "password",pass);
                FetchDb.setSetting(getActivity(),"passmode","1");
                getActivity().setResult(Activity.RESULT_OK);
                getActivity().finish();
                
            }
        }
    }
}
