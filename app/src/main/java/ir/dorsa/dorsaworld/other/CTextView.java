package ir.dorsa.dorsaworld.other;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by mehdi on 7/10/15 AD.
 */
public class CTextView extends TextView {
    public static Typeface FONT_NAME;


    public CTextView(Context context) {
        super(context);
        isInEditMode();
        if(FONT_NAME == null) FONT_NAME = Typeface.createFromAsset(context.getAssets(), "iran_sans_lightt.ttf");
        this.setTypeface(FONT_NAME);
    }
    public CTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        isInEditMode();
        try {
            if (FONT_NAME == null)
                FONT_NAME = Typeface.createFromAsset(context.getAssets(), "iran_sans_lightt.ttf");
            this.setTypeface(FONT_NAME);
        }catch (Exception ex){
            
        }
    }
    public CTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        isInEditMode();
        if(FONT_NAME == null) FONT_NAME = Typeface.createFromAsset(context.getAssets(), "iran_sans_lightt.ttf");
        this.setTypeface(FONT_NAME);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        isInEditMode();
        String mText=text.toString()
                .replace("0", "۰")
                .replace("1","۱")
                .replace("2","۲")
                .replace("3","۳")
                .replace("4","۴")
                .replace("5","۵")
                .replace("6","۶")
                .replace("7","۷")
                .replace("8","۸")
                .replace("9","۹")
                ;
        super.setText(mText, type);
    }
}
