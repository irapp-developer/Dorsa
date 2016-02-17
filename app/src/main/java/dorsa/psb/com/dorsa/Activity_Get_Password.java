package dorsa.psb.com.dorsa;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import dorsa.psb.com.dorsa.other.FetchDb;

/**
 * Created by mehdi on 1/26/16 AD.
 */
public class Activity_Get_Password extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_password);
        getSupportActionBar().hide();
        final TextView TXT_title = (TextView) findViewById(R.id.act_pass_title);
        final TextView TXT_hint = (TextView) findViewById(R.id.act_pass_pass_hint_1);
        final EditText ET_password = (EditText) findViewById(R.id.act_pass_pass_1);

        final RelativeLayout pass_Rel = (RelativeLayout) findViewById(R.id.act_pass_pass_rel);

        Button BTN_submit = (Button) findViewById(R.id.act_pass_btn_submit);

        ET_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 0) {
                    TXT_hint.setVisibility(View.VISIBLE);
                } else {
                    TXT_hint.setVisibility(View.GONE);
                }
            }
        });

        BTN_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation shake= AnimationUtils.loadAnimation(Activity_Get_Password.this,R.anim.anim_shake);
                if (ET_password.getText().toString().length() == 0) {
                    pass_Rel.startAnimation(shake);
                }else if(!FetchDb.getSetting(Activity_Get_Password.this,"password")
                        .equals(ET_password.getText().toString())
                        ){
                    pass_Rel.startAnimation(shake);
                    ET_password.setText("");
                    TXT_title.setText("رمز اشتباه است");
                }else{//password is correct
                    
                    setResult(Activity.RESULT_OK);
                    finish();
                }
            }
        });


    }
}
