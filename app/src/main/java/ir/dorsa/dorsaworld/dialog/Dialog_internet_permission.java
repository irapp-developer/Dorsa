package ir.dorsa.dorsaworld.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;

import ir.dorsa.dorsaworld.R;
import ir.dorsa.dorsaworld.other.G;

/**
 * Created by mehdi on 11/23/16.
 */
public class Dialog_internet_permission extends Dialog {
    TextView btnYes;
    TextView btnNo;

    public Dialog_internet_permission(Context context) {
        super(context, android.R.style.Theme_Holo_Light_Dialog_NoActionBar);
        setContentView(R.layout.dialog_internet_access);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        
        ImageView bgrTop=(ImageView)findViewById(R.id.dialog_internet_per_top_bgr);
        ImageView cluds=(ImageView) findViewById(R.id.dialog_internet_per_clods);
        ImageView mushak=(ImageView) findViewById(R.id.dialog_internet_per_mushak);
        String sexMode="0";//male
        try {
            sexMode=G.selectedKid.joKid.getString("sex");
          
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if("1".equals(sexMode)){
            bgrTop.setImageResource(R.drawable.bgr_red);
            cluds.setImageResource(R.drawable.clouds_girl);
            mushak.setImageResource(R.drawable.mushak_girl);
        }
        btnYes = (TextView) findViewById(R.id.dialog_internet_per_btn_yes);
        btnNo = (TextView) findViewById(R.id.dialog_internet_per_btn_no);
        TextView btnCancel = (TextView) findViewById(R.id.dialog_internet_per_btn_cancel);
        
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancel();
            }
        });


    }

    public void setBtnListner(View.OnClickListener clickYes, View.OnClickListener clickNo) {
        btnYes.setOnClickListener(clickYes);
        btnNo.setOnClickListener(clickNo);

    }

    public Dialog_internet_permission(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected Dialog_internet_permission(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }
}
