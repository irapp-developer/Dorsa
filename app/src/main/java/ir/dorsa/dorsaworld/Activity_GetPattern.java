package ir.dorsa.dorsaworld;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import ir.dorsa.dorsaworld.other.FetchDb;
import ir.dorsa.dorsaworld.other.G;
import ir.dorsa.dorsaworld.other.PatternView;
import ir.dorsa.dorsaworld.other.Permisions;
import ir.dorsa.dorsaworld.other.ViewAccessibilityCompat;
import ir.dorsa.dorsaworld.service.Service_Launcher;
import ir.dorsa.dorsaworld.service.Service_Whatchdog;

/**
 * Created by mehdi on 1/26/16 AD.
 */
public class Activity_GetPattern extends AppCompatActivity {
     PatternView mPatternView;
     ImageView mLeftButton;
     ImageView mRightButton;
     TextView mMessageText;
    
    public static Activity THIS;

    protected int numFailedAttempts;
    public static final int RESULT_FORGOT_PASSWORD = RESULT_FIRST_USER;
    private static final String KEY_NUM_FAILED_ATTEMPTS = "num_failed_attempts";
    private static final int CLEAR_PATTERN_DELAY_MILLI = 500;
    private static final int REQUEST_RESET_PASSWORD = 384;
    
    
    private int wrongPassCounter =0;
    private long passTimeCounter=0;
    private int TOTAL_WRONG_PASS=5;
    private int TOTAL_DELAY_TIME=60;
    private Timer passTimer=new Timer();
    private View overPattern;


    @Override
    protected void onDestroy() {
        THIS=null;
        try{
            passTimer.cancel();
        }catch (Exception ex){
            
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        try{
            passTimer.cancel();
        }catch (Exception ex){

        }
        Intent intent=getIntent();
        String isFromLauncher=intent.getStringExtra("UlockApp");
        if(isFromLauncher!=null) {
            if (isFromLauncher.equals("true")) {
                startService(new Intent(App.getContext(), Service_Launcher.class));
                stopService(new Intent(App.getContext(), Service_Whatchdog.class));
            }
        }
        super.onBackPressed();
    } 
    public void FullScreencall() {
        if(Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if(Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_pattern);
        
        THIS=this;
        
        getSupportActionBar().hide();
        FullScreencall();
        mPatternView = (PatternView) findViewById(R.id.patternView);
        overPattern=findViewById(R.id.patternView_over);
        overPattern.setOnClickListener(null);
        
        
        mLeftButton = (ImageView) findViewById(R.id.pl_left_button);
        mRightButton = (ImageView) findViewById(R.id.pl_right_button);
        mMessageText = (TextView) findViewById(R.id.text_pattern);
        
        mMessageText.setText(R.string.pl_draw_pattern_to_unlock);
        mPatternView.setInStealthMode(false);
        mPatternView.setOnPatternListener(new PatternView.OnPatternListener() {
            @Override
            public void onPatternStart() {
                removeClearPatternRunnable();
                // Set display mode to correct to ensure that pattern can be in stealth mode.
                mPatternView.setDisplayMode(PatternView.DisplayMode.Correct);
            }

            @Override
            public void onPatternCleared() {
                removeClearPatternRunnable();
            }

            @Override
            public void onPatternCellAdded(List<PatternView.Cell> pattern) {

            }

            @Override
            public void onPatternDetected(List<PatternView.Cell> pattern) {
                if (isPatternCorrect(pattern)) {
                    onConfirmed();
                } else {
                    wrongPassCounter += 1;
                    if (wrongPassCounter >= TOTAL_WRONG_PASS) {
                        FetchDb.setSetting(Activity_GetPattern.this, "lastWrongPass", "" + Calendar.getInstance().getTimeInMillis());
                        passTimeCounter = TOTAL_DELAY_TIME;
                        passTimer.schedule(new passCounter(), 0);
                        overPattern.setVisibility(View.VISIBLE);
                        mPatternView.setDisplayMode(PatternView.DisplayMode.Wrong);
                        postClearPatternRunnable();

//                        ViewAccessibilityCompat.announceForAccessibility(mMessageText, mMessageText.getText());
                        announceForAccessibility(mMessageText, mMessageText.getText());
                        mMessageText.setText("بعد از " + passTimeCounter + " ثانیه اقدام نمایید");
                    } else {
                        mMessageText.setText(R.string.pl_wrong_pattern);
                        mPatternView.setDisplayMode(PatternView.DisplayMode.Wrong);
                        postClearPatternRunnable();
                        announceForAccessibility(mMessageText, mMessageText.getText());
//                        ViewAccessibilityCompat.announceForAccessibility(mMessageText, mMessageText.getText());
                        onWrongPattern();
                    }
                }
            }
        });
        mLeftButton.setImageResource(R.drawable.btn_cancel_item);
        mLeftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCancel();
            }
        });
        mRightButton.setImageResource(R.drawable.btn_recovery);
        mRightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onForgotPassword();
            }
        });
        announceForAccessibility(mMessageText, mMessageText.getText());
//        ViewAccessibilityCompat.announceForAccessibility(mMessageText, mMessageText.getText());

        if (savedInstanceState == null) {
            numFailedAttempts = 0;
        } else {
            numFailedAttempts = savedInstanceState.getInt(KEY_NUM_FAILED_ATTEMPTS);
        }
        
        //------check last WrongAttempt ------
        long lastAttempt=Long.parseLong(FetchDb.getSetting(this,"lastWrongPass"));
        long nowTime=Calendar.getInstance().getTimeInMillis();
        if(lastAttempt!=-1) {
            long delay = (nowTime - lastAttempt) / 1000;
            Log.d(G.LOG_TAG,"Pass first delay="+delay);
            if(delay>=TOTAL_DELAY_TIME){
                FetchDb.setSetting(this,"lastWrongPass","-1");
            }else{
                delay=TOTAL_DELAY_TIME-delay;
                wrongPassCounter =0;
                passTimeCounter=delay;
                overPattern.setVisibility(View.VISIBLE);
                passTimer.schedule(new passCounter(),0);
            }
        }

    }
    
    private void announceForAccessibility(TextView mTextView,CharSequence chrSeq){
        try {
            ViewAccessibilityCompat.announceForAccessibility(mTextView, chrSeq);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    protected void onForgotPassword() {
      /*  setResult(RESULT_FORGOT_PASSWORD);
        finish();*/
        
        startActivityForResult(new Intent(this, Activity_Reset_Password.class), REQUEST_RESET_PASSWORD);
    }
    protected void onCancel() {
        setResult(RESULT_CANCELED);
        passTimer.cancel();
        Intent intent=getIntent();
        String isFromLauncher=intent.getStringExtra("UlockApp");
        if(isFromLauncher!=null) {
            if (isFromLauncher.equals("true")) {
                startService(new Intent(App.getContext(), Service_Launcher.class));
                stopService(new Intent(App.getContext(), Service_Whatchdog.class));
            }
        }
        finish();
    }
    protected void onWrongPattern() {
        ++numFailedAttempts;
    }
    protected boolean isPatternCorrect(List<PatternView.Cell> pattern) {
        String pass="";
        for(PatternView.Cell cell:pattern) {
            pass+=cell.getRow()+""+cell.getColumn();
        }
        if(FetchDb.getSetting(this,"password").equals(pass)) {
            return true;
        }else{
            return false;
        }
    }
    protected void onConfirmed() {
        setResult(RESULT_OK);
        
        Intent intent=getIntent();
        String isFromLauncher=intent.getStringExtra("UlockApp");
        String isSwitchParent=intent.getStringExtra("switchParent");
        if(isSwitchParent==null)isSwitchParent="false";
        if(isFromLauncher!=null){
            if(isFromLauncher.equals("true")){
                
                G.Watchdog.isAppRuned=2;
                Permisions.setDisablePermisions(getApplicationContext());
                getApplicationContext().stopService(new Intent(getApplicationContext(), Service_Launcher.class));
                
                if("true".equals(isSwitchParent)){
                    Intent intent_parent=new Intent(this,Activity_Parent.class);
                    intent_parent.putExtra("switchParent","true");
                    startActivity(intent_parent);
                }
                
                
                finish();
                Permisions.setDisablePermisions(getApplicationContext());
         
            }
        }else{
            finish();
        }
        
    }
    private final Runnable clearPatternRunnable = new Runnable() {
        public void run() {
            // clearPattern() resets display mode to DisplayMode.Correct.
            mPatternView.clearPattern();
        }
    };
    
    class passCounter extends TimerTask{
        @Override
        public void run() {
          if(passTimeCounter<=0){
              setOverVisibility(false);
              FetchDb.setSetting(Activity_GetPattern.this, "lastWrongPass", "-1");
              wrongPassCounter =0;
              Activity_GetPattern.this.runOnUiThread(new Runnable() {
                  @Override
                  public void run() {
                      mMessageText.setText(R.string.pl_draw_pattern_to_unlock);
                  }
              });
          }else {
              passTimeCounter -= 1;
//              FetchDb.setSetting(Activity_GetPattern.this, "lastWrongPass", "" + Calendar.getInstance().getTimeInMillis());
              passTimer.schedule(new passCounter(), 1000);
              Activity_GetPattern.this.runOnUiThread(new Runnable() {
                  @Override
                  public void run() {
                      mMessageText.setText("بعد از " + (passTimeCounter+1) + " ثانیه اقدام نمایید");
                  }
              });
             
          }
        }
    }
    
    private void setOverVisibility(final boolean visible){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (visible){
                    overPattern.setVisibility(View.VISIBLE);   
                }else{
                    overPattern.setVisibility(View.GONE); 
                }
            }
        });
    }
    
    protected void removeClearPatternRunnable() {
        mPatternView.removeCallbacks(clearPatternRunnable);
    }
    protected void postClearPatternRunnable() {
        removeClearPatternRunnable();
        mPatternView.postDelayed(clearPatternRunnable, CLEAR_PATTERN_DELAY_MILLI);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==REQUEST_RESET_PASSWORD & resultCode== Activity.RESULT_OK){
            onConfirmed();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
