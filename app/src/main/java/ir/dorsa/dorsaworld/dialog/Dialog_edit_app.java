package ir.dorsa.dorsaworld.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;

import ir.dorsa.dorsaworld.R;
import ir.dorsa.dorsaworld.other.G;

/**
 * Created by mehdi on 11/23/16.
 */
public class Dialog_edit_app extends Dialog {
    public Dialog_edit_app(Context context) {
        super(context, android.R.style.Theme_Holo_Light_Dialog_NoActionBar);
        setContentView(R.layout.dialog_edit_kid_app);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        
        
        ImageView bgrTop=(ImageView)findViewById(R.id.dialog_edit_kid_top_bgr);
        ImageView cluds=(ImageView) findViewById(R.id.dialog_edit_kid_clods);
        

        try {
            String sexMode= G.selectedKid.joKid.getString("sex");
            if("1".equals(sexMode)){
                bgrTop.setImageResource(R.drawable.bgr_red);
                cluds.setImageResource(R.drawable.clouds_girl);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public Dialog_edit_app(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected Dialog_edit_app(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public void setAppDetails(String name, Drawable drawableIcon){
        ImageView icon=(ImageView) findViewById(R.id.dialog_edit_kid_icon);
        TextView appName=(TextView)findViewById(R.id.dialog_edit_kid_descr);
        
        icon.setImageDrawable(drawableIcon);
        appName.setText(name);
                
    }

    public void setClickListners(String internetTitle, View.OnClickListener clickInternet, View.OnClickListener clickRemove){
        LinearLayout layBtns=(LinearLayout)findViewById(R.id.dialog_edit_kid_btn_lay);
        TextView btnRemove=(TextView)findViewById(R.id.dialog_edit_kid_btn_no);
        TextView btnRemoveInternet=(TextView)findViewById(R.id.dialog_edit_kid_btn_cancel);

        TextView btnRemove_2=(TextView)findViewById(R.id.dialog_edit_kid_single_btn_cancel);

        btnRemoveInternet.setText(internetTitle);

        if(clickInternet!=null){
            btnRemove.setOnClickListener(clickRemove);
            btnRemoveInternet.setOnClickListener(clickInternet);
        }else{
            layBtns.setVisibility(View.GONE);
            btnRemove_2.setVisibility(View.VISIBLE);
            btnRemove_2.setOnClickListener(clickRemove);

        }

    }
}
