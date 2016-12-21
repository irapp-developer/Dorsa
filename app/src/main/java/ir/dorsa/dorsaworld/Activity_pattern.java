package ir.dorsa.dorsaworld;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ir.dorsa.dorsaworld.other.G;
import ir.dorsa.dorsaworld.other.PatternUtils;
import ir.dorsa.dorsaworld.other.PatternView;
import ir.dorsa.dorsaworld.other.ViewAccessibilityCompat;

/**
 * Created by mehdi on 1/18/16 AD.
 */
public class Activity_pattern extends AppCompatActivity {

    private enum LeftButtonState {

        Cancel(R.drawable.btn_cancel_item, true),
        CancelDisabled(R.drawable.btn_cancel_item, false),
        Redraw(R.drawable.btn_retry, true),
        RedrawDisabled(R.drawable.btn_retry, false);

        public final int textId;
        public final boolean enabled;

        private LeftButtonState(int textId, boolean enabled) {
            this.textId = textId;
            this.enabled = enabled;
        }
    }

    private enum RightButtonState {

        Continue(R.drawable.btn_continue, true),
        ContinueDisabled(R.drawable.btn_continue, false),
        Confirm(R.drawable.btn_ok, true),
        ConfirmDisabled(R.drawable.btn_ok, false);

        public final int imageResource;
        public final boolean enabled;

        private RightButtonState(int textId, boolean enabled) {
            this.imageResource = textId;
            this.enabled = enabled;
        }
    }

    private enum Stage {

        Draw(R.string.pl_draw_pattern, LeftButtonState.Cancel, RightButtonState.ContinueDisabled,
                true),
        DrawTooShort(R.string.pl_pattern_too_short, LeftButtonState.Redraw,
                RightButtonState.ContinueDisabled, true),
        DrawValid(R.string.pl_pattern_recorded, LeftButtonState.Redraw, RightButtonState.Continue,
                false),
        Confirm(R.string.pl_confirm_pattern, LeftButtonState.Cancel,
                RightButtonState.ConfirmDisabled, true),
        ConfirmWrong(R.string.pl_wrong_pattern, LeftButtonState.Cancel,
                RightButtonState.ConfirmDisabled, true),
        ConfirmCorrect(R.string.pl_pattern_confirmed, LeftButtonState.Cancel,
                RightButtonState.Confirm, false);

        public final int messageId;
        public final LeftButtonState leftButtonState;
        public final RightButtonState rightButtonState;
        public final boolean patternEnabled;

        private Stage(int messageId, LeftButtonState leftButtonState,
                      RightButtonState rightButtonState, boolean patternEnabled) {
            this.messageId = messageId;
            this.leftButtonState = leftButtonState;
            this.rightButtonState = rightButtonState;
            this.patternEnabled = patternEnabled;
        }
    }

    private static final String KEY_STAGE = "stage";
    private static final String KEY_PATTERN = "pattern";

    private int minPatternSize;
    private List<PatternView.Cell> pattern;
    private Stage stage;

    private PatternView mPatternView;
    private ImageView mLeftButton;
    private ImageView mRightButton;
    private TextView mMessageText;

    private final Runnable clearPatternRunnable = new Runnable() {
        public void run() {
            // clearPattern() resets display mode to DisplayMode.Correct.
            mPatternView.clearPattern();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_pattern);
        getSupportActionBar().hide();
        minPatternSize = 4;
        mPatternView = (PatternView) findViewById(R.id.patternView);

        mLeftButton = (ImageView) findViewById(R.id.pl_left_button);
        mRightButton = (ImageView) findViewById(R.id.pl_right_button);
        mMessageText = (TextView) findViewById(R.id.text_pattern);

        mLeftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (stage.leftButtonState == LeftButtonState.Redraw) {
                    pattern = null;
                    updateStage(Stage.Draw);
                } else if (stage.leftButtonState == LeftButtonState.Cancel) {
                    setResult(RESULT_CANCELED);
                    finish();
                } else {
                    throw new IllegalStateException("left footer button pressed, but stage of " + stage
                            + " doesn't make sense");
                }
            }
        });

        mRightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (stage.rightButtonState == RightButtonState.Continue) {
                    if (stage != Stage.DrawValid) {
                        throw new IllegalStateException("expected ui stage " + Stage.DrawValid
                                + " when button is " + RightButtonState.Continue);
                    }
                    updateStage(Stage.Confirm);
                } else if (stage.rightButtonState == RightButtonState.Confirm) {
                    if (stage != Stage.ConfirmCorrect) {
                        throw new IllegalStateException("expected ui stage " + Stage.ConfirmCorrect
                                + " when button is " + RightButtonState.Confirm);
                    }
                    onSetPattern(pattern);
                    Intent intent=new Intent();
                    intent.putExtra("pass",getPassText(pattern));
                    intent.putExtra("passMode","1");
                    setResult(RESULT_OK,intent);
                    finish();
                }
            }
        });


        mPatternView.setOnPatternListener(new PatternView.OnPatternListener() {
            @Override
            public void onPatternStart() {
                removeClearPatternRunnable();

                mMessageText.setText(R.string.pl_recording_pattern);
                mPatternView.setDisplayMode(PatternView.DisplayMode.Correct);
                mLeftButton.setEnabled(false);
                mRightButton.setEnabled(false);
            }

            @Override
            public void onPatternCleared() {
                removeClearPatternRunnable();
            }

            @Override
            public void onPatternCellAdded(List<PatternView.Cell> pattern) {

            }

            @Override
            public void onPatternDetected(List<PatternView.Cell> newPattern) {
                switch (stage) {
                    case Draw:
                    case DrawTooShort:
                        if (newPattern.size() < minPatternSize) {
                            updateStage(Stage.DrawTooShort);
                        } else {
                            pattern = new ArrayList<>(newPattern);
                            updateStage(Stage.DrawValid);
                        }
                        break;
                    case Confirm:
                    case ConfirmWrong:
                        if (newPattern.equals(pattern)) {
                            updateStage(Stage.ConfirmCorrect);
                        } else {
                            updateStage(Stage.ConfirmWrong);
                        }
                        break;
                    default:
                        throw new IllegalStateException("Unexpected stage " + stage + " when "
                                + "entering the pattern.");
                }
            }
        });


        if (savedInstanceState == null) {
            updateStage(Stage.Draw);
        } else {
            String patternString = savedInstanceState.getString(KEY_PATTERN);
            if (patternString != null) {
                pattern = PatternUtils.stringToPattern(patternString);
            }
            updateStage(Stage.values()[savedInstanceState.getInt(KEY_STAGE)]);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(KEY_STAGE, stage.ordinal());
        if (pattern != null) {
            outState.putString(KEY_PATTERN, PatternUtils.patternToString(pattern));
        }
    }


    private void updateStage(Stage newStage) {

        Stage previousStage = stage;
        stage = newStage;

        if (stage == Stage.DrawTooShort) {
            mMessageText.setText(getString(stage.messageId, minPatternSize));
        } else {
            mMessageText.setText(stage.messageId);
        }

        mLeftButton.setImageResource(stage.leftButtonState.textId);
        mLeftButton.setEnabled(stage.leftButtonState.enabled);

        mRightButton.setImageResource(stage.rightButtonState.imageResource);
        mRightButton.setEnabled(stage.rightButtonState.enabled);

        mPatternView.setInputEnabled(stage.patternEnabled);

        switch (stage) {
            case Draw:
                // clearPattern() resets display mode to DisplayMode.Correct.
                mPatternView.clearPattern();
                break;
            case DrawTooShort:
                mPatternView.setDisplayMode(PatternView.DisplayMode.Wrong);
                postClearPatternRunnable();
                break;
            case DrawValid:
                break;
            case Confirm:
                mPatternView.clearPattern();
                break;
            case ConfirmWrong:
                mPatternView.setDisplayMode(PatternView.DisplayMode.Wrong);
                postClearPatternRunnable();
                break;
            case ConfirmCorrect:
                break;
        }

        // If the stage changed, announce the header for accessibility. This
        // is a no-op when accessibility is disabled.
        if (previousStage != stage) {
            ViewAccessibilityCompat.announceForAccessibility(mMessageText, mMessageText.getText());
        }
    }

    protected void removeClearPatternRunnable() {
        mPatternView.removeCallbacks(clearPatternRunnable);
    }

    protected void postClearPatternRunnable() {
        removeClearPatternRunnable();
        mPatternView.postDelayed(clearPatternRunnable, 500);
    }

    protected void onSetPattern(List<PatternView.Cell> pattern) {
        String pass="";
        for(PatternView.Cell cell:pattern) {
            pass+=cell.getRow()+""+cell.getColumn();
        }
        G.Intro.password=pass;
        G.Intro.passmode="1";
    }
    
    private String getPassText(List<PatternView.Cell> pattern){
        String pass="";
        for(PatternView.Cell cell:pattern) {
            pass+=cell.getRow()+""+cell.getColumn();
        }
        return pass;
    }

}
