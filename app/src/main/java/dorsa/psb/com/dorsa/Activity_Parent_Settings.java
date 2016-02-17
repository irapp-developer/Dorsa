package dorsa.psb.com.dorsa;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import dorsa.psb.com.dorsa.other.CustomTypefaceSpan;
import dorsa.psb.com.dorsa.other.FetchDb;
import dorsa.psb.com.dorsa.other.Func;

/**
 * Created by mehdi on 2/9/16 AD.
 */
public class Activity_Parent_Settings extends AppCompatActivity {

    private Spinner SPN_Question;

    private int REQUEST_PASSWORD_PATTERN = 854;
    private int REQUEST_PASSWORD_TEXT = 754;
    private int REQUEST_NEW_PASSWORD_PATTERN = 654;


    private String question = "";
    private String answer = "";
    private String phoneNumber = "";

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            onBackPressed();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        saveSettings();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        saveSettings();
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


        final RelativeLayout RelChangePass = (RelativeLayout) findViewById(R.id.act_parent_setttins_pass);

        SPN_Question= (Spinner) findViewById(R.id.act_parent_setttins_hint_question);

        final EditText ET_answer = (EditText) findViewById(R.id.act_parent_setttins_hint_answer);
        final TextView answer_hitnt = (TextView) findViewById(R.id.act_parent_setttins_hint_answer_hint);

        final EditText ET_number = (EditText) findViewById(R.id.act_parent_setttins_number_desc);
        final TextView number_hint = (TextView) findViewById(R.id.act_parent_setttins_number_desc_hint);


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
        ET_number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 0) {
                    number_hint.setVisibility(View.INVISIBLE);
                } else {
                    number_hint.setVisibility(View.VISIBLE);
                }
                phoneNumber = s.toString();
            }
        });

        ET_answer.setText(answer);
        ET_number.setText(phoneNumber);

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
        
        if(answer.length()>0){
            FetchDb.setSetting(this,"hint_question",SPN_Question.getSelectedItem().toString());
            FetchDb.setSetting(this,"hint_answer",answer);    
        }
        
        if(phoneNumber.length()>0){
            FetchDb.setSetting(this,"phonenumber",phoneNumber);    
        }
    }
    
    private void setupActionBaer(){
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        Typeface font = Typeface.createFromAsset(getAssets(), "iran_sans_lightt.ttf");
        SpannableStringBuilder SS_all = new SpannableStringBuilder("تنظیمات");
        SS_all.setSpan(new CustomTypefaceSpan("", font, Color.parseColor("#ffffff"),26), 0, SS_all.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        actionBar.setTitle(SS_all);
    }    
    
    
}
