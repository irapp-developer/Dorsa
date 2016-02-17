package dorsa.psb.com.dorsa.other;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.TypefaceSpan;

public class CustomTypefaceSpan extends TypefaceSpan {

private final Typeface newType;
private int mColor;
private int size;    

public CustomTypefaceSpan(String family, Typeface type,int color,int size) {
    super(family);
    newType = type;
    this.mColor=color;
    this.size=size;
}

@Override
public void updateDrawState(TextPaint ds) {
    applyCustomTypeFace(ds, newType,mColor,size);
}

@Override
public void updateMeasureState(TextPaint paint) {
    applyCustomTypeFace(paint, newType,mColor,size);
}

private static void applyCustomTypeFace(Paint paint, Typeface tf,int color,int size) {
    int oldStyle;
    Typeface old = paint.getTypeface();
    if (old == null) {
        oldStyle = 0;
    } else {
        oldStyle = old.getStyle();
    }

    int fake = oldStyle & ~tf.getStyle();
    if ((fake & Typeface.BOLD) != 0) {
        paint.setFakeBoldText(true);
    }

    if ((fake & Typeface.ITALIC) != 0) {
        paint.setTextSkewX(-0.25f);
    }
    paint.setColor(color);

    paint.setTypeface(tf);
    paint.setTextSize(size);
}
}
