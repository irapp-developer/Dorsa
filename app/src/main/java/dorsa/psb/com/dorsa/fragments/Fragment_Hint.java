package dorsa.psb.com.dorsa.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import dorsa.psb.com.dorsa.R;
import dorsa.psb.com.dorsa.other.FetchDb;
import dorsa.psb.com.dorsa.other.G;

/**
 * Created by mehdi on 1/19/16 AD.
 */
public class Fragment_Hint extends Fragment {
    private View pView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        pView = inflater.inflate(R.layout.fragment_hint, container, false);

        final EditText phoneNumber=(EditText)pView.findViewById(R.id.frg_hint_phone_number);
        final EditText answer=(EditText)pView.findViewById(R.id.frg_hint_answer);

        final TextView phoneNumber_hint=(TextView)pView.findViewById(R.id.frg_hint_hint_phone_number);
        final TextView answer_hint=(TextView)pView.findViewById(R.id.frg_hint_hint_answer);
        
        final RelativeLayout REL_phone=(RelativeLayout)pView.findViewById(R.id.frg_hint_phone_rel);
        final RelativeLayout REL_answer=(RelativeLayout)pView.findViewById(R.id.frg_hint_answer_rel);

        final Spinner SPN_question=(Spinner)pView.findViewById(R.id.frg_hint_spinner_question); 
        
        
        Button BTN_confirm=(Button)pView.findViewById(R.id.frg_hint_confirm);

//------------- set hints ------------------
        phoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            if(s.toString().length()>0){
                phoneNumber_hint.setVisibility(View.GONE);
            }else{
                phoneNumber_hint.setVisibility(View.VISIBLE);
            }
            }
        });

        answer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().length()>0){
                    answer_hint.setVisibility(View.GONE);
                }else{
                    answer_hint.setVisibility(View.VISIBLE);
                }
            }
        });
        //--------- set button ---------
        BTN_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation shake= AnimationUtils.loadAnimation(getActivity(),R.anim.anim_shake);
                if(phoneNumber.getText().toString().length()==0){
                    REL_phone.startAnimation(shake);
                }else if(answer.getText().toString().length()==0){
                    REL_answer.startAnimation(shake);
                }else{//save password and hint
                    FetchDb.setSetting(getActivity(),"password",G.Intro.password);
                    FetchDb.setSetting(getActivity(),"passmode",G.Intro.passmode);
                    FetchDb.setSetting(getActivity(),"phonenumber",phoneNumber.getText().toString());
                    FetchDb.setSetting(getActivity(),"hint_question",SPN_question.getSelectedItem().toString());
                    FetchDb.setSetting(getActivity(),"hint_answer",answer.getText().toString());
                    
                    G.mViewPager.setCurrentItem(3);    
                }
                
                
                
            }
        });
        
        
      /*  final Spinner spinner = (Spinner) pView.findViewById(R.id.frg_hint_spinner_question);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, R.array.hint_questions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);*/
        return pView;
    }

     String[] Months = new String[] { "January", "February",
            "March", "April", "May", "June", "July", "August", "September",
            "October", "November", "December" };


}
