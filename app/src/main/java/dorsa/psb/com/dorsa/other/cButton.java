package dorsa.psb.com.dorsa.other;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Created by mehdi on 9/20/15 AD.
 */
public class cButton extends Button {
    public static Typeface FONT_NAME;

    public cButton(Context context) {
        super(context);
    }

    public cButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (FONT_NAME == null)
            FONT_NAME = Typeface.createFromAsset(context.getAssets(), "iran_sans_lightt.ttf");
        this.setTypeface(FONT_NAME);
    }

    public cButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (FONT_NAME == null)
            FONT_NAME = Typeface.createFromAsset(context.getAssets(), "iran_sans_lightt.ttf");
        this.setTypeface(FONT_NAME);
    }


}
