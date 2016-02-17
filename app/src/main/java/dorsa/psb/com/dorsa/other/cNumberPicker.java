package dorsa.psb.com.dorsa.other;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.lang.reflect.Field;

import dorsa.psb.com.dorsa.R;

/**
 * Created by mehdi on 1/23/16 AD.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class cNumberPicker extends NumberPicker {

    Typeface type;

    public cNumberPicker(Context context) {
        super(context);
        type = Typeface.createFromAsset(getContext().getAssets(), "iran_sans_lightt.ttf");
        setDividerColor();
    }

    public cNumberPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        type = Typeface.createFromAsset(getContext().getAssets(), "iran_sans_lightt.ttf");
        setDividerColor();
    }

    public cNumberPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        type = Typeface.createFromAsset(getContext().getAssets(), "iran_sans_lightt.ttf");
        setDividerColor();
    }


    @Override
    public void addView(View child) {
        super.addView(child);
        updateView(child);
    }

    @Override
    public void addView(View child, int index,
                        android.view.ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        updateView(child);
    }

    @Override
    public void addView(View child, android.view.ViewGroup.LayoutParams params) {
        super.addView(child, params);
        updateView(child);
    }
    

    private void updateView(View view) {

        if (view instanceof TextView) {
            
            ((EditText) view).setTypeface(type);
            ((EditText) view).setTextSize(16);
            ((EditText) view).setTextColor(getResources().getColor(R.color.yellow_text_color));
        }
    }

    public void setDividerColor() {
        try {
            Field fDividerDrawable = NumberPicker.class.getDeclaredField("mSelectionDivider");
            fDividerDrawable.setAccessible(true);
            Drawable d = (Drawable) fDividerDrawable.get(this);
            d.setColorFilter(Color.parseColor("#ebfe43"), PorterDuff.Mode.SRC_ATOP);
            d.invalidateSelf();
            postInvalidate(); // Drawable is dirty
        } catch (Exception e) {

        }
    }
}
