package ir.dorsa.dorsaworld;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.squareup.picasso.Picasso;

import ir.dorsa.dorsaworld.other.Func;
import ir.dorsa.dorsaworld.other.G;

/**
 * Created by mehdi on 2/8/16 AD.
 */
public class Activity_Select_Background extends AppCompatActivity {
    int images[] = new int[]{
            R.drawable.bgr_default,
            R.drawable.bgr_1_1,
            R.drawable.bgr_1_2,
            R.drawable.bgr_1,
            R.drawable.bgr_2,
            R.drawable.bgr_3,
            R.drawable.bgr_4,
            R.drawable.bgr_5,
            R.drawable.bgr_6,
            R.drawable.bgr_7,
            R.drawable.bgr_8,
            R.drawable.bgr_9};
    int images_thumb[] = new int[]{
            R.drawable.bgr_default_thumb,
            R.drawable.bgr_1_1_thumb,
            R.drawable.bgr_1_2_thumb,
            R.drawable.bgr_1_thumb,
            R.drawable.bgr_2_thumb,
            R.drawable.bgr_3_thumb,
            R.drawable.bgr_4_thumb,
            R.drawable.bgr_5_thumb,
            R.drawable.bgr_6_thumb,
            R.drawable.bgr_7_thumb,
            R.drawable.bgr_8_thumb,
            R.drawable.bgr_9_thumb};
    

    
    private int selectedItam = 0;
    private RelativeLayout thumb_lay;
    private ImageView mainImage;

    @Override
    protected void onDestroy() {
        mainImage.setImageDrawable(null);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_background);


        
        mainImage = (ImageView) findViewById(R.id.act_select_bgr_main_img);
        thumb_lay=(RelativeLayout)findViewById(R.id.act_select_bgr_thumb_lay);
        
        
        Button Btn_Ok = (Button) findViewById(R.id.act_select_bgr_btn_ok);
        Button Btn_Cancel = (Button) findViewById(R.id.act_select_bgr_btn_cancel);
        View Overlay_Back = findViewById(R.id.act_select_bgr_actionbar_back_overlay);


        Picasso.with(Activity_Select_Background.this).load(images_thumb[0]).into(mainImage);
//        mainImage.setImageResource(images[0]);


        for(int i=0;i<images_thumb.length;i++){

            View child = getLayoutInflater().inflate(R.layout.row_select_bgr_img, null);
            
            ImageView ImgThumb=(ImageView)child.findViewById(R.id.row_select_bgr);
            Picasso.with(this).load(images_thumb[i]).into(ImgThumb);
//            ImgThumb.setImageResource(images_thumb[i]);
            
            RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(Func.dpToPx(this,120),Func.dpToPx(this,120));
            if(i==0){
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT,RelativeLayout.TRUE);
                ImageView stroke = (ImageView) child.findViewById(R.id.row_select_stroke);
                stroke.setImageResource(R.drawable.stroke_yellow);
            }else{
                params.addRule(RelativeLayout.RIGHT_OF,2000+(i-1));
                        
            }
            child.setId(2000 + i);
            child.setOnClickListener(new thumbClick(i));
            thumb_lay.addView(child,params);
            
        }


        /*thumb_lay.forceLayout();
        
        RelativeLayout newView = (RelativeLayout) thumb_lay.findViewById(2000 + 0);
        ImageView stroke = (ImageView) newView.findViewById(R.id.row_select_stroke);
        stroke.setImageResource(R.drawable.stroke_yellow);
        Animation scaleIn = AnimationUtils.loadAnimation(Activity_Select_Background.this, R.anim.scale_in);
        thumb_lay.bringChildToFront(newView);
        newView.startAnimation(scaleIn);*/
        
        
        //---------- set buttons ---------------------
        Btn_Ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                G.selectImage.SelectedImageResource = images[selectedItam];
                setResult(RESULT_OK);
                finish();

            }
        });

        Btn_Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });


        Overlay_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

    }

    class thumbClick implements View.OnClickListener {

        private int position = 0;

        public thumbClick(int pos) {
            position = pos;
        }

        @Override
        public void onClick(View v) {
            if (position != selectedItam) {
//                ((BitmapDrawable)mainImage.getDrawable()).getBitmap().recycle();
                Picasso.with(Activity_Select_Background.this).load(images_thumb[position]).placeholder(mainImage.getDrawable()).into(mainImage);
                
//                mainImage.setImageResource(images[position]);
                View oldView = thumb_lay.findViewById(2000 + selectedItam);
                View newView = thumb_lay.findViewById(2000 + position);
                
                ImageView oldStroke = (ImageView) oldView.findViewById(R.id.row_select_stroke);
                oldStroke.setImageResource(R.drawable.stroke_black);
                ImageView newstroke = (ImageView) newView.findViewById(R.id.row_select_stroke);
                newstroke.setImageResource(R.drawable.stroke_yellow);
               /* Animation scaleIn = AnimationUtils.loadAnimation(Activity_Select_Background.this, R.anim.scale_in);
                Animation scaleOut = AnimationUtils.loadAnimation(Activity_Select_Background.this, R.anim.scale_out);
                thumb_lay.bringChildToFront(newView);

                oldView.startAnimation(scaleOut);
                newView.startAnimation(scaleIn);*/
                selectedItam = position;
            }
        }
    }

}
