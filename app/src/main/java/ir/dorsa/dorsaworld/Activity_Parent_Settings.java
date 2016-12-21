package ir.dorsa.dorsaworld;

import android.app.Activity;
import android.app.Dialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import ir.dorsa.dorsaworld.other.FetchDb;
import ir.dorsa.dorsaworld.other.Func;
import ir.dorsa.dorsaworld.other.G;
import ir.dorsa.dorsaworld.service.PolicyAdmin;

/**
 * Created by mehdi on 2/9/16 AD.
 */
public class Activity_Parent_Settings extends AppCompatActivity {

    private Spinner SPN_Question;

    private final int REQUEST_PASSWORD_PATTERN = 854;
    private final int REQUEST_PASSWORD_TEXT = 754;
    private final int REQUEST_NEW_PASSWORD_PATTERN = 654;
    private final int REQUEST_ACTIVE_ADMIN = 747;
    private static final int REQUEST_ENABLE_USAGE_STATE=8573;

    private String question = "";
    private String answer = "";
    private String phoneNumber = "";
    
    private String emergency_number = "";
    
    
    private  CheckBox AdminCheckBox;
    private  CheckBox AccessCheckBox;

    private EditText ET_emergency_numer;
    private  EditText ET_number;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            onBackPressed();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_parent_settings);
        setupActionBaer();
        //FetchDb.setSetting(getActivity(),"passmode",G.Intro.passmode);

        question = FetchDb.getSetting(this, "hint_question");
        answer = FetchDb.getSetting(this, "hint_answer");
        phoneNumber = FetchDb.getSetting(this, "phonenumber");
        
        emergency_number=FetchDb.getSetting(this, "emergency_number");

        final RelativeLayout RelChangePass = (RelativeLayout) findViewById(R.id.act_parent_setttins_pass);
        final RelativeLayout RelChangeAdmin = (RelativeLayout) findViewById(R.id.act_parent_setttins_admin);
        final RelativeLayout RelChangeAccess = (RelativeLayout) findViewById(R.id.act_parent_setttins_access);

        SPN_Question= (Spinner) findViewById(R.id.act_parent_setttins_hint_question);

        final EditText ET_answer = (EditText) findViewById(R.id.act_parent_setttins_hint_answer);
        final TextView answer_hitnt = (TextView) findViewById(R.id.act_parent_setttins_hint_answer_hint);

        ET_number = (EditText) findViewById(R.id.act_parent_setttins_number_desc);

        ET_emergency_numer= (EditText) findViewById(R.id.act_parent_setttins_emergency_exit_desc);


        AdminCheckBox=(CheckBox)findViewById(R.id.act_parent_setttins_admin_checkBox);
        AccessCheckBox=(CheckBox)findViewById(R.id.act_parent_setttins_access_checkBox);


        ET_answer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 0) {
                    answer_hitnt.setVisibility(View.INVISIBLE);
                } else {
                    answer_hitnt.setVisibility(View.VISIBLE);
                }
                answer=s.toString();
            }
        });
      
        
        ET_answer.setText(answer);
        ET_number.setText(phoneNumber);
        ET_emergency_numer.setText(emergency_number);
        
        if(isAdminActive())AdminCheckBox.setChecked(true);


        AdminCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked != isAdminActive()) {
                    AdminCheckBox.setChecked(!isChecked);
                    startActivityAdmin();
                }
            }
        });
        
        
        ///------------ set access mode ---------

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if(Func.isUsageStateEnable(this))AccessCheckBox.setChecked(true);
        }else{
            RelChangeAccess.setVisibility(View.GONE);
        }

        AccessCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked & Func.isUsageStateEnable(Activity_Parent_Settings.this)){
                    AccessCheckBox.setChecked(false);
                    Activity_Parent_Settings.this.startActivityForResult(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS),REQUEST_ENABLE_USAGE_STATE);
                }else if(isChecked  & !Func.isUsageStateEnable(Activity_Parent_Settings.this)){
                    AccessCheckBox.setChecked(true);
                    Activity_Parent_Settings.this.startActivityForResult(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS),REQUEST_ENABLE_USAGE_STATE);
                }
            }
        });

        RelChangeAccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity_Parent_Settings.this.startActivityForResult(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS),REQUEST_ENABLE_USAGE_STATE);
            }
        });
        //-------------------------------------
        String spinnerVals[]=getResources().getStringArray(R.array.hint_questions);
        
        for (int i = 0; i < spinnerVals.length; i++) {
            if(spinnerVals[i].equals(question)){
                SPN_Question.setSelection(i);
                break;
            }
        }


        RelChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FetchDb.getSetting(Activity_Parent_Settings.this, "passmode").equals("1")) {//pattern
                    startActivityForResult(new Intent(Activity_Parent_Settings.this, Activity_GetPattern.class), REQUEST_PASSWORD_PATTERN);
                } else {
                    startActivityForResult(new Intent(Activity_Parent_Settings.this, Activity_Get_Password.class), REQUEST_PASSWORD_TEXT);
                }
            }
        });

        RelChangeAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityAdmin();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PASSWORD_PATTERN && resultCode == Activity.RESULT_OK) {//insert pattern ok
            makePassChooser();
        } else if (requestCode == REQUEST_PASSWORD_TEXT && resultCode == Activity.RESULT_OK) {//insert pass ok
            makePassChooser();
        } else if (requestCode == REQUEST_NEW_PASSWORD_PATTERN & resultCode == Activity.RESULT_OK && data != null) {
            String password = data.getStringExtra("pass");
            String passwordMode = data.getStringExtra("passMode");

            FetchDb.setSetting(Activity_Parent_Settings.this, "password", password);
            FetchDb.setSetting(Activity_Parent_Settings.this, "passmode", passwordMode);
        }else if(requestCode == REQUEST_ACTIVE_ADMIN ){
            if(isAdminActive()){
                AdminCheckBox.setChecked(true);
            }else{
                AdminCheckBox.setChecked(false);
            }
        }else if (requestCode==REQUEST_ENABLE_USAGE_STATE){
            if(Func.isUsageStateEnable(this)){
                AccessCheckBox.setChecked(true);
            }else{
                AccessCheckBox.setChecked(false);
            }
        }
    }

    private void makePassChooser() {
        ArrayAdapter<CharSequence> itemsAdapter = new ArrayAdapter<CharSequence>(this, android.R.layout.select_dialog_item, new String[]{"با استفاده از الگو", "رمز نوشتاری"});
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setAdapter(itemsAdapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        startActivityForResult(new Intent(Activity_Parent_Settings.this, Activity_pattern.class), REQUEST_NEW_PASSWORD_PATTERN);
                        break;
                    case 1:
                        Func.dialogNewpassword(Activity_Parent_Settings.this, true);
                        break;
                }
            }
        });
        Dialog dialog = builder.create();
        dialog.show();
    }
    
    private void saveSettings(){
        if(answer.length()==0) {
            Toast.makeText(Activity_Parent_Settings.this, "پاسخ را وارد نمایید", Toast.LENGTH_SHORT).show();
            return;
        }else if(ET_number.getText().toString().length()==0){
            Toast.makeText(Activity_Parent_Settings.this, "سماره تماس را وارد نمایید", Toast.LENGTH_SHORT).show();
            return;
        }else if(!ET_number.getText().toString().startsWith("09")){
            Toast.makeText(Activity_Parent_Settings.this, "شماره بایستی با ۰۹ آغاز گردد", Toast.LENGTH_SHORT).show();
            return;
        }else if(ET_emergency_numer.getText().toString().length()==0){
            Toast.makeText(Activity_Parent_Settings.this, "شماره تماس اضطراری را وارد نمایید", Toast.LENGTH_SHORT).show();
            return;
        }else if(!ET_emergency_numer.getText().toString().startsWith("09")){
            Toast.makeText(Activity_Parent_Settings.this, "شماره تماس اضطاری بایستی با ۰۹ آغاز گردد", Toast.LENGTH_SHORT).show();
            return;
        }else{
            FetchDb.setSetting(this,"hint_question",SPN_Question.getSelectedItem().toString());
            FetchDb.setSetting(this,"hint_answer",answer);
            FetchDb.setSetting(this,"phonenumber",ET_number.getText().toString());
            Func.registerImsi(this);
            FetchDb.setSetting(this,"emergency_number",ET_emergency_numer.getText().toString());
        }
    }
    
    private boolean isAdminActive(){
        DevicePolicyManager mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName mAdminName=new ComponentName(this,PolicyAdmin.class);
        Log.d(G.LOG_TAG, "is Admin Active :" + mDPM.isAdminActive(mAdminName));
        return mDPM.isAdminActive(mAdminName);
    }
    
    private void startActivityAdmin(){
        ComponentName mAdminName=new ComponentName(this,PolicyAdmin.class);
        if(!isAdminActive()){
            Intent activateDeviceAdminIntent =new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);

            activateDeviceAdminIntent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mAdminName);
            activateDeviceAdminIntent.putExtra(
                    DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                    getResources().getString(R.string.admin_policy));

            startActivityForResult(activateDeviceAdminIntent,REQUEST_ACTIVE_ADMIN);
        }else{
            DevicePolicyManager mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
            mDPM.removeActiveAdmin(mAdminName);
            AdminCheckBox.setChecked(false);
        }

    }

    private void setupActionBaer(){

        Toolbar toolbar=(Toolbar)findViewById(R.id.act_parent_setttins_toolbar);
        setSupportActionBar(toolbar);

            String sexMode="0";
        try {
            sexMode=G.selectedKid.joKid.getString("sex");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if("0".equals(sexMode)){
            toolbar.setBackgroundResource(R.drawable.bgr_toolbar_boy);
        }else{
            toolbar.setBackgroundResource(R.drawable.bgr_toolbar_girl);
        }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if ("0".equals(sexMode)) {
                    
                    Window window = getWindow();
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(ContextCompat.getColor(this, R.color.Color_blue_accent));
                } else {
                    
                    Window window = getWindow();
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(ContextCompat.getColor(this, R.color.Color_red_accent));
                }
            }
        
        RelativeLayout save=(RelativeLayout)findViewById(R.id.act_parent_tick_rel);
        RelativeLayout cancel=(RelativeLayout)findViewById(R.id.act_parent_cross_rel);
        
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveSettings();
                finish();
                
            }
        });
        
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        
    }
}
