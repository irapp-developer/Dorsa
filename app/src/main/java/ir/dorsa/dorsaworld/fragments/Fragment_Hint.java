package ir.dorsa.dorsaworld.fragments;

import android.app.Activity;
import android.os.Build;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import ir.dorsa.dorsaworld.R;
import ir.dorsa.dorsaworld.other.FetchDb;
import ir.dorsa.dorsaworld.other.Func;
import ir.dorsa.dorsaworld.other.G;

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
        final EditText emergency_number=(EditText)pView.findViewById(R.id.frg_hint_backdour_edittext);

        final TextView answer_hint=(TextView)pView.findViewById(R.id.frg_hint_hint_answer);
        
        final RelativeLayout REL_phone=(RelativeLayout)pView.findViewById(R.id.frg_hint_phone_rel);
        final RelativeLayout REL_answer=(RelativeLayout)pView.findViewById(R.id.frg_hint_answer_rel);

        final Spinner SPN_question=(Spinner)pView.findViewById(R.id.frg_hint_spinner_question); 
        
        
        ImageView BTN_confirm=(ImageView)pView.findViewById(R.id.frg_hint_confirm);

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
                }else if(!phoneNumber.getText().toString().startsWith("09")){
                    REL_phone.startAnimation(shake);
                    Toast.makeText(Fragment_Hint.this.getContext(), "شماره موبایل بایستی با ۰۹ آغاز گردد", Toast.LENGTH_LONG).show();
                }else if(phoneNumber.getText().toString().length()<11){
                    REL_phone.startAnimation(shake);
                    Toast.makeText(Fragment_Hint.this.getContext(), "شماره تماس را کامل وارد نمایید", Toast.LENGTH_SHORT).show();
                }else if(answer.getText().toString().length()==0){
                    REL_answer.startAnimation(shake);
                }else if(emergency_number.getText().toString().length()==0){
                    emergency_number.startAnimation(shake);
                }else if(!emergency_number.getText().toString().startsWith("09")){
                    emergency_number.startAnimation(shake);
                    Toast.makeText(Fragment_Hint.this.getContext(), "شماره موبایل بایستی با ۰۹ آغاز گردد", Toast.LENGTH_LONG).show();
                }else if(emergency_number.getText().toString().length()<11){
                    emergency_number.startAnimation(shake);
                    Toast.makeText(Fragment_Hint.this.getContext(), "شماره تماس را کامل وارد نمایید", Toast.LENGTH_SHORT).show();
                }else{//save password and hint
                    FetchDb.setSetting(getActivity(),"password",G.Intro.password);
                    FetchDb.setSetting(getActivity(),"passmode",G.Intro.passmode);
                    FetchDb.setSetting(getActivity(),"phonenumber",phoneNumber.getText().toString());
                    FetchDb.setSetting(getActivity(),"emergency_number",emergency_number.getText().toString());
                    FetchDb.setSetting(getActivity(), "hint_question", SPN_question.getSelectedItem().toString());
                    FetchDb.setSetting(getActivity(), "hint_answer", answer.getText().toString());

                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//Check system version and 
                        if (!Func.isUsageStateEnable(getActivity())) {//Check is access to read task ok
                            if(G.mViewPager!=null)G.mViewPager.setCurrentItem(3);
                        }else{//Check  access to read task is ok
                            getActivity().setResult(Activity.RESULT_OK);
                            getActivity().finish();
                        }
                    }else{//its lower version and just finish
                        getActivity().setResult(Activity.RESULT_OK);
                        getActivity().finish();
                    }
                    
                  
                    
//                    G.mViewPager.setCurrentItem(3);    
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
