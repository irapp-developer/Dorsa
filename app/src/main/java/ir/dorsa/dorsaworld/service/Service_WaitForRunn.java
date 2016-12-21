package ir.dorsa.dorsaworld.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import ir.dorsa.dorsaworld.App;
import ir.dorsa.dorsaworld.R;
import ir.dorsa.dorsaworld.other.G;

/**
 * Created by mehdi on 2/21/16 AD.
 */
public class Service_WaitForRunn extends Service {
    private WindowManager manager;
    private View pView;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        manager  = ((WindowManager) getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE));


        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |

                        // this is to enable the notification to recieve touch events
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |

                        // Draws over status bar
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.RGBA_8888);

        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.TOP;
        LayoutInflater inflate = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
         pView = inflate.inflate(R.layout.service_wait_for_run, null);

        manager.addView(pView, params);
        
        Button back=(Button)pView.findViewById(R.id.service_wait_for_run_button_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                G.Watchdog.isAppRuned = 1;
                if (G.Watchdog.waitForBack != null) {
                    G.Watchdog.waitForBack.finish();
                    G.Watchdog.waitForBack = null;
                }

                startService(new Intent(App.getContext(), Service_Launcher.class));
                stopService(new Intent(App.getContext(),Service_Whatchdog.class));
                Service_WaitForRunn.this.stopForeground(true);
                Service_WaitForRunn.this.stopSelf();
            }
        });
        
        setupAnimations();
    }

    @Override
    public void onDestroy() {
        manager.removeView(pView);
        super.onDestroy();
    }
    
    private void setupAnimations(){
        ImageView monster_3_eye=(ImageView)pView.findViewById(R.id.monster_3_eye);
        final RelativeLayout monster_4=(RelativeLayout)pView.findViewById(R.id.monster_4_rel);
        final Animation anim_scale= AnimationUtils.loadAnimation(App.getContext(), R.anim.anim_scale_body);


        anim_scale.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        monster_4.startAnimation(anim_scale);
                    }
                }, 4000);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        monster_4.startAnimation(anim_scale);
        ((AnimationDrawable) monster_3_eye.getDrawable()).start();
    }
    
}
