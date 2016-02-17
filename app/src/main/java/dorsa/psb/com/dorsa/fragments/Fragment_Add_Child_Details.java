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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;

import dorsa.psb.com.dorsa.R;
import dorsa.psb.com.dorsa.other.Func;
import dorsa.psb.com.dorsa.other.G;

/**
 * Created by mehdi on 1/21/16 AD.
 */
public class Fragment_Add_Child_Details extends Fragment {

    private View pView;
    private boolean isAvatarSelected = false;
    private boolean isNameEntered = false;


     ImageView Kid_sex_male;
     ImageView Kid_sex_fmale;

     ImageView Kid_sex_male_storke;
     ImageView Kid_sex_fmale_storke;
     ImageView tick_sex;
     LinearLayout sexRel;

     EditText name;
     TextView name_hint;
     ImageView tick_name;
     RelativeLayout nameRel;

     TextView birthday;
     TextView birthday_hint;
     ImageView tick_birthday;
     RelativeLayout birthdayRel;
    
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        pView = inflater.inflate(R.layout.fragment_add_child_details, container, false);

        

        Button BTN_Add = (Button) pView.findViewById(R.id.frg_add_kid_details_btn_add);
         Kid_sex_male = (ImageView) pView.findViewById(R.id.frg_add_kid_details_sex_male);
         Kid_sex_fmale = (ImageView) pView.findViewById(R.id.frg_add_kid_details_sex_fmale);

         Kid_sex_male_storke = (ImageView) pView.findViewById(R.id.frg_add_kid_details_sex_male_stroke);
         Kid_sex_fmale_storke = (ImageView) pView.findViewById(R.id.frg_add_kid_details_sex_fmale_stroke);
         tick_sex = (ImageView) pView.findViewById(R.id.frg_add_kid_details_tick_sex);
         sexRel = (LinearLayout) pView.findViewById(R.id.frg_add_kid_details_sex_linear);

         name = (EditText) pView.findViewById(R.id.frg_add_kid_details_name_edittext);
         name_hint = (TextView) pView.findViewById(R.id.frg_add_kid_details_name_hint);
         tick_name = (ImageView) pView.findViewById(R.id.frg_add_kid_details_tick_name);
         nameRel = (RelativeLayout) pView.findViewById(R.id.frg_add_kid_details_name_linear);

         birthday = (TextView) pView.findViewById(R.id.frg_add_kid_details_birth_text);
         birthday_hint = (TextView) pView.findViewById(R.id.frg_add_kid_details_birth_hint);
         tick_birthday = (ImageView) pView.findViewById(R.id.frg_add_kid_details_tick_birth);
         birthdayRel = (RelativeLayout) pView.findViewById(R.id.frg_add_kid_details_birth_linear);


        //-------- set sex select listner --------------
        Kid_sex_male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Kid_sex_male_storke.setVisibility(View.VISIBLE);
                Kid_sex_fmale_storke.setVisibility(View.INVISIBLE);
                tick_sex.setVisibility(View.VISIBLE);
                isAvatarSelected = true;
                if (G.kid_details.sexMode != 0) {//boy selected
                    if (G.kid_details.MaleRel != null) {
                        G.kid_details.MaleRel.setVisibility(View.VISIBLE);
                        G.kid_details.FMaleRel.setVisibility(View.GONE);
                        G.kid_details.kidsImg.setImageResource(R.drawable.icon_boy);
                        Fragment_kid_img.invisibleAllStrokes();
                        G.kid_details.boys_stroke[2].setVisibility(View.VISIBLE);
                    }
                }
                G.kid_details.sexMode = 0;
            }
        });

        Kid_sex_fmale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Kid_sex_male_storke.setVisibility(View.INVISIBLE);
                Kid_sex_fmale_storke.setVisibility(View.VISIBLE);
                tick_sex.setVisibility(View.VISIBLE);
                isAvatarSelected = true;
                if (G.kid_details.sexMode != 1) {//boy selected
                    if (G.kid_details.FMaleRel != null) {
                        G.kid_details.MaleRel.setVisibility(View.GONE);
                        G.kid_details.FMaleRel.setVisibility(View.VISIBLE);
                        G.kid_details.kidsImg.setImageResource(R.drawable.icon_girl);
                        Fragment_kid_img.invisibleAllStrokes();
                        G.kid_details.girls_stroke[2].setVisibility(View.VISIBLE);
                    }
                }
                G.kid_details.sexMode = 1;
            }
        });
        BTN_Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isAvatarSelected) {
                    Animation shake = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_shake);
                    sexRel.startAnimation(shake);
                } else if (!isNameEntered) {
                    Animation shake = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_shake);
                    nameRel.startAnimation(shake);
                } else if (G.kid_details.birthDay.length() == 0) {
                    Animation shake = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_shake);
                    birthdayRel.startAnimation(shake);
                } else {
                    G.addChild.name = name.getText().toString();
                    G.mViewPager.setCurrentItem(1);
                }


            }
        });
        //-------- set name listner --------------
        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.toString().length() > 0) {
                    name_hint.setVisibility(View.INVISIBLE);
                    tick_name.setVisibility(View.VISIBLE);
                    isNameEntered = true;
                } else {
                    name_hint.setVisibility(View.VISIBLE);
                    tick_name.setVisibility(View.INVISIBLE);
                    isNameEntered = false;
                }

            }
        });
        //----------- set birth listner ------------
        birthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Func.dialogBirthday(Fragment_Add_Child_Details.this.getActivity(), birthday, birthday_hint, tick_birthday);
            }
        });
        
        
        if(G.addChild.isEditedMode){// its editing
            setDefaultValues();
        }
        
        return pView;
    }



    private void setDefaultValues(){
        try {
            //-------------- set sex -------------------
            if(G.selectedKid.joKid.getString("sex").equals("0")){//means male
                Kid_sex_male_storke.setVisibility(View.VISIBLE);
                Kid_sex_fmale_storke.setVisibility(View.INVISIBLE);
            }else{
                Kid_sex_male_storke.setVisibility(View.INVISIBLE);
                Kid_sex_fmale_storke.setVisibility(View.VISIBLE);
            }
            tick_sex.setVisibility(View.VISIBLE);
            //-------------- set Name -------------------
            name.setText(G.selectedKid.joKid.getString("name"));
            tick_name.setVisibility(View.VISIBLE);
            //-------------- set sex -------------------
            birthday.setText(G.selectedKid.joKid.getString("birthday"));
            tick_birthday.setVisibility(View.VISIBLE);
            birthday_hint.setVisibility(View.INVISIBLE);

            isAvatarSelected=true;
            isNameEntered=true;
            G.kid_details.birthDay=G.selectedKid.joKid.getString("birthday");
            G.addChild.name=G.selectedKid.joKid.getString("name");
            G.kid_details.sexMode=Integer.parseInt(G.selectedKid.joKid.getString("sex"));
            
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
