package ir.dorsa.dorsaworld;

import android.app.Activity;
import android.content.Intent;
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

import ir.dorsa.dorsaworld.other.FetchDb;
import ir.dorsa.dorsaworld.other.G;
import ir.dorsa.dorsaworld.other.Permisions;
import ir.dorsa.dorsaworld.service.Service_Launcher;

/**
 * Created by mehdi on 1/26/16 AD.
 */
public class Activity_Get_Password extends AppCompatActivity {
    
    public static Activity THIS;

    private static final int REQUEST_RESET_PASSWORD = 384;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_password);
        
        THIS=this;
        
        getSupportActionBar().hide();
        final TextView TXT_title = (TextView) findViewById(R.id.act_pass_title);
        final TextView TXT_hint = (TextView) findViewById(R.id.act_pass_pass_hint_1);
        final EditText ET_password = (EditText) findViewById(R.id.act_pass_pass_1);


      
        
        final RelativeLayout pass_Rel = (RelativeLayout) findViewById(R.id.act_pass_pass_rel);

        Button BTN_submit = (Button) findViewById(R.id.act_pass_btn_submit);
        Button BTN_forget_pass = (Button) findViewById(R.id.act_pass_btn_forget);

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
                Animation shake = AnimationUtils.loadAnimation(Activity_Get_Password.this, R.anim.anim_shake);
                if (ET_password.getText().toString().length() == 0) {
                    pass_Rel.startAnimation(shake);
                } else if (!FetchDb.getSetting(Activity_Get_Password.this, "password")
                        .equals(ET_password.getText().toString())
                        ) {
                    pass_Rel.startAnimation(shake);
                    ET_password.setText("");
                    TXT_title.setText("رمز اشتباه است");
                } else {//password is correct
                    onConfirmed();
                }
            }
        });

        BTN_forget_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(Activity_Get_Password.this, Activity_Reset_Password.class), REQUEST_RESET_PASSWORD);
            }
        });


    }

    @Override
    protected void onDestroy() {
        THIS=null;
                
        super.onDestroy();
    }

    private void onConfirmed(){
        setResult(Activity.RESULT_OK);
        Intent intent = getIntent();
        String isFromLauncher = intent.getStringExtra("UlockApp");
        String isSwitchParent=intent.getStringExtra("switchParent");
        if(isSwitchParent==null)isSwitchParent="false";
        
        if (isFromLauncher != null) {
            if (isFromLauncher.equals("true")) {
                G.Watchdog.isAppRuned = 2;

                Permisions.setDisablePermisions(getApplicationContext());
                getApplicationContext().stopService(new Intent(getApplicationContext(), Service_Launcher.class));

                if("true".equals(isSwitchParent)){
                    Intent intent_parent=new Intent(this,Activity_Parent.class);
                    intent_parent.putExtra("switchParent","true");
                    startActivity(intent_parent);
                }
                
                finish();
                Permisions.setDisablePermisions(getApplicationContext());
//                            finish();
                           /* new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    finish();
                                }
                            }, 1000);*/
            }
        } else {
            finish();
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==REQUEST_RESET_PASSWORD & resultCode== Activity.RESULT_OK){
            onConfirmed();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
