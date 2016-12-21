package ir.dorsa.dorsaworld;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;

import ir.dorsa.dorsaworld.other.G;

public class Activity_Parent_Hint extends AppCompatActivity {

    RelativeLayout relHint_1;
    RelativeLayout relHint_2;
    RelativeLayout relHint_3;
    RelativeLayout relHint_4;
    RelativeLayout relHint_5;
    
    Button btn_1;
    Button btn_2;
    Button btn_3;
    Button btn_4;
    Button btn_5;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.parent, menu);
        return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_hint);
        
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        RelativeLayout toolbarRel=(RelativeLayout)findViewById(R.id.act_parent_child_profile);
        TextView kidName=(TextView)findViewById(R.id.act_parent_child_name);
        TextView tab_2=(TextView)findViewById(R.id.act_parent_child_tab_2);
        TextView tab_1_title=(TextView)findViewById(R.id.act_parent_child_sett_title);
        ImageView kidAvatar=(ImageView)findViewById(R.id.act_parent_child_icon);

        String sexMode="0";
        try {
            sexMode=G.selectedKid.joKid.getString("sex");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if("0".equals(sexMode)){
            toolbarRel.setBackgroundResource(R.drawable.boy_bgr);
            kidName.setText("پارساجون");
            tab_1_title.setTextColor(ContextCompat.getColor(this, R.color.Color_blue));
            kidAvatar.setImageResource(R.drawable.icon_boy);
            tab_2.setBackgroundResource(R.drawable.tab_left_unselect_boy);
        }else{
            toolbarRel.setBackgroundResource(R.drawable.girly_bgr);
            kidName.setText("کتایون جون");
            tab_1_title.setTextColor(ContextCompat.getColor(this, R.color.Color_red_girl));
            kidAvatar.setImageResource(R.drawable.icon_girl);
            tab_2.setBackgroundResource(R.drawable.tab_left_unselect_girl);
        }
        
        relHint_1=(RelativeLayout)findViewById(R.id.act_parent_hint_rel_1);
        relHint_2=(RelativeLayout)findViewById(R.id.act_parent_hint_rel_2);
        relHint_3=(RelativeLayout)findViewById(R.id.act_parent_hint_rel_3);
        relHint_4=(RelativeLayout)findViewById(R.id.act_parent_hint_rel_4);
        relHint_5=(RelativeLayout)findViewById(R.id.act_parent_hint_rel_5);

        btn_1=(Button)findViewById(R.id.act_hint_rel_1_btn);
        btn_2=(Button)findViewById(R.id.act_hint_rel_2_btn);
        btn_3=(Button)findViewById(R.id.act_hint_rel_3_btn);
        btn_4=(Button)findViewById(R.id.act_hint_rel_4_btn);
        btn_5=(Button)findViewById(R.id.act_hint_rel_5_btn);
        
        btn_1.setOnClickListener(new clickHint(relHint_1,relHint_2));
        btn_2.setOnClickListener(new clickHint(relHint_2,relHint_3));
        btn_3.setOnClickListener(new clickHint(relHint_3,relHint_4));
        btn_4.setOnClickListener(new clickHint(relHint_4,relHint_5));
        
        btn_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        
    }
    
    class clickHint implements View.OnClickListener{

        private RelativeLayout showRel;
        private RelativeLayout hideRel;
        
        public clickHint(RelativeLayout mhideRel,RelativeLayout mShowRel){
            this.showRel=mShowRel;
            this.hideRel=mhideRel;
        }
        
        
        @Override
        public void onClick(View view) {
            showRel.setVisibility(View.VISIBLE);
            hideRel.setVisibility(View.GONE);
        }
    }
}
