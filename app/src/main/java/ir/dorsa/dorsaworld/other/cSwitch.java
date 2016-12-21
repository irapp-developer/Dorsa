package ir.dorsa.dorsaworld.other;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.Switch;

/**
 * Created by mehdi on 1/24/16 AD.
 */
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class cSwitch extends Switch{
    public static Typeface FONT_NAME;
    public cSwitch(Context context) {
        super(context);
        if(FONT_NAME == null) FONT_NAME = Typeface.createFromAsset(context.getAssets(), "iran_sans_lightt.ttf");
        this.setTypeface(FONT_NAME);
    }

    public cSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
        if(FONT_NAME == null) FONT_NAME = Typeface.createFromAsset(context.getAssets(), "iran_sans_lightt.ttf");
        this.setTypeface(FONT_NAME);
    }

    public cSwitch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if(FONT_NAME == null) FONT_NAME = Typeface.createFromAsset(context.getAssets(), "iran_sans_lightt.ttf");
        this.setTypeface(FONT_NAME);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
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
