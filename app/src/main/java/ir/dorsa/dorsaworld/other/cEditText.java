package ir.dorsa.dorsaworld.other;

/**
 * Created by mehdi on 1/19/16 AD.
 */

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

public class cEditText extends EditText {

    public cEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public cEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public cEditText(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "iran_sans_lightt.ttf");
            setTypeface(tf);
        }
    }

}