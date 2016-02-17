package dorsa.psb.com.dorsa;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import dorsa.psb.com.dorsa.other.FetchDb;
import dorsa.psb.com.dorsa.other.PatternView;
import dorsa.psb.com.dorsa.other.ViewAccessibilityCompat;

/**
 * Created by mehdi on 1/26/16 AD.
 */
public class Activity_GetPattern extends AppCompatActivity {
     PatternView mPatternView;
     Button mLeftButton;
     Button mRightButton;
     TextView mMessageText;

    protected int numFailedAttempts;
    public static final int RESULT_FORGOT_PASSWORD = RESULT_FIRST_USER;
    private static final String KEY_NUM_FAILED_ATTEMPTS = "num_failed_attempts";

    private static final int CLEAR_PATTERN_DELAY_MILLI = 500;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_pattern);
        
        getSupportActionBar().hide();
        
        mPatternView = (PatternView) findViewById(R.id.patternView);
        
        mLeftButton = (Button) findViewById(R.id.pl_left_button);
        mRightButton = (Button) findViewById(R.id.pl_right_button);
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
                    mMessageText.setText(R.string.pl_wrong_pattern);
                    mPatternView.setDisplayMode(PatternView.DisplayMode.Wrong);
                    postClearPatternRunnable();
                    ViewAccessibilityCompat.announceForAccessibility(mMessageText, mMessageText.getText());
                    onWrongPattern();
                }
            }
        });
        mLeftButton.setText(R.string.pl_cancel);
        mLeftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCancel();
            }
        });
        mRightButton.setText(R.string.pl_forgot_pattern);
        mRightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onForgotPassword();
            }
        });
        ViewAccessibilityCompat.announceForAccessibility(mMessageText, mMessageText.getText());

        if (savedInstanceState == null) {
            numFailedAttempts = 0;
        } else {
            numFailedAttempts = savedInstanceState.getInt(KEY_NUM_FAILED_ATTEMPTS);
        }

    }

    protected void onForgotPassword() {
        setResult(RESULT_FORGOT_PASSWORD);
        finish();
    }
    protected void onCancel() {
        setResult(RESULT_CANCELED);
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
        finish();
    }
    private final Runnable clearPatternRunnable = new Runnable() {
        public void run() {
            // clearPattern() resets display mode to DisplayMode.Correct.
            mPatternView.clearPattern();
        }
    };
    protected void removeClearPatternRunnable() {
        mPatternView.removeCallbacks(clearPatternRunnable);
    }
    protected void postClearPatternRunnable() {
        removeClearPatternRunnable();
        mPatternView.postDelayed(clearPatternRunnable, CLEAR_PATTERN_DELAY_MILLI);
    }
}
