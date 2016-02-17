package dorsa.psb.com.dorsa;

import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import dorsa.psb.com.dorsa.other.Func;
import dorsa.psb.com.dorsa.other.G;

public class MainActivity extends AppCompatActivity {
    PackageManager p;
    ComponentName cN;

    @Override
    protected void onStart() {
        super.onStart();
        p =getApplicationContext().getPackageManager();
        cN= new ComponentName(getApplicationContext(), dorsa.psb.com.dorsa.launcher.class);
        p.setComponentEnabledSetting(cN, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);


    }

    @Override
    protected void onDestroy() {
        Log.d(G.LOG_TAG,"on destroy");
        if(!Func.getDefaultLauncher(this).equals(this.getPackageName())){
            Log.d(G.LOG_TAG,"Clear launcher");
            p.setComponentEnabledSetting(cN, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        }
        super.onDestroy();
    }
    
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_policy);
        setupActionBar();
        
        final Animation anim_jump= AnimationUtils.loadAnimation(this,R.anim.anim_jump);
        final Animation anim_shadow= AnimationUtils.loadAnimation(this,R.anim.anim_scale_shadow);
//        final Animation anim_hand= AnimationUtils.loadAnimation(this,R.anim.anim_hand);
        
        final RelativeLayout bug_rel=(RelativeLayout)findViewById(R.id.frg_intro_1_bug_rel);
        final ImageView bug_eye=(ImageView)findViewById(R.id.frg_intro_1_bug_eye);
        final ImageView monster_2_eye=(ImageView)findViewById(R.id.monster_2_eye);
//        final ImageView bug_hand=(ImageView)findViewById(R.id.frg_intro_1_bug_hand);
        final ImageView bug_shadow=(ImageView)findViewById(R.id.frg_intro_1_bug_shadow);

        
        anim_jump.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                ((AnimationDrawable) bug_eye.getDrawable()).stop();
//                bug_hand.startAnimation(anim_hand);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ((AnimationDrawable) bug_eye.getDrawable()).start();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                bug_rel.startAnimation(anim_jump);
                                bug_shadow.startAnimation(anim_shadow);
                            }
                        });
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

  
    
    private void setupActionBar(){
        getSupportActionBar().hide();
    }
   
}
