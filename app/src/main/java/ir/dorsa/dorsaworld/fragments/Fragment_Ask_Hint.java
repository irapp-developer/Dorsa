package ir.dorsa.dorsaworld.fragments;

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
import android.widget.TextView;
import android.widget.Toast;

import ir.dorsa.dorsaworld.R;
import ir.dorsa.dorsaworld.other.FetchDb;
import ir.dorsa.dorsaworld.other.G;

/**
 * Created by mehdi on 1/19/16 AD.
 */
public class Fragment_Ask_Hint extends Fragment {
    private View pView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        pView = inflater.inflate(R.layout.fragment_ask_hint, container, false);

        TextView hintText=(TextView)pView.findViewById(R.id.frg_reset_text_question);
        
        final EditText answer=(EditText)pView.findViewById(R.id.frg_reset_answer);
        final TextView answer_hint=(TextView)pView.findViewById(R.id.frg_reset_hint_answer);
        final RelativeLayout REL_answer=(RelativeLayout)pView.findViewById(R.id.frg_reset_answer_rel);
        
        
        Button BTN_confirm=(Button)pView.findViewById(R.id.frg_reset_confirm);

        hintText.setText(FetchDb.getSetting(getActivity(), "hint_question"));
//        FetchDb.setSetting(getActivity(),"hint_answer",answer.getText().toString());

//------------- set hints ------------------
      

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
                if(answer.getText().toString().length()==0){
                    REL_answer.startAnimation(shake);
                }else if(FetchDb.getSetting(getActivity(), "hint_answer").equals(answer.getText().toString())){//got to select pass
                    G.ResetPass.mViewPager.setCurrentItem(1);    
                }else{
                    Toast.makeText(Fragment_Ask_Hint.this.getActivity(), "جواب اشتباه است", Toast.LENGTH_SHORT).show();
                }
                
                
                
            }
        });

        return pView;
    }

}
