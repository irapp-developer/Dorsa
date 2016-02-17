package dorsa.psb.com.dorsa.other;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.TextView;

import dorsa.psb.com.dorsa.R;


/**
 * Created by mehdi on 2/9/16 AD.
 */
public class StrokeTextView extends TextView {

    private final Canvas mCanvas = new Canvas();
    private final Paint mPaint = new Paint();
    private Bitmap mCache;
    private boolean mUpdateCachedBitmap;
    private int mStrokeColor;
    private float mStrokeWidth;
    private int mTextColor;

    public StrokeTextView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public StrokeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public StrokeTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.StrokedTextAttrs, defStyle, 0);
        mStrokeColor = a.getColor(R.styleable.StrokedTextAttrs_strokeColor, 0xFF000000);
        mStrokeWidth = a.getFloat(R.styleable.StrokedTextAttrs_strokeWidth, 0.0f);
        mTextColor = a.getColor(R.styleable.StrokedTextAttrs_strokeTextColor, 0xFFFFFFFF);
        a.recycle();
        mUpdateCachedBitmap = true;
        // Setup the text paint
        Typeface FONT_NAME = Typeface.createFromAsset(context.getAssets(), "iran_sans_lightt.ttf");
        mPaint.setTypeface(FONT_NAME);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    protected void onTextChanged(CharSequence text, int start, int before, int after) {
        super.onTextChanged(text, start, before, after);
        mUpdateCachedBitmap = true;
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w > 0 && h > 0) {
            mUpdateCachedBitmap = true;
            mCache = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        } else {
            mCache = null;
        }
    }

    protected void onDraw(Canvas canvas) {

                
        
        if (mCache != null) {
            if (mUpdateCachedBitmap) {
                final int w = getMeasuredWidth();
                final int h = getMeasuredHeight();
                final String text = getText().toString();
                final Rect textBounds = new Rect();
                final Paint textPaint = getPaint();
                final int textWidth = (int) textPaint.measureText(text);
                textPaint.getTextBounds("x", 0, 1, textBounds);
                // Clear the old cached image
                mCanvas.setBitmap(mCache);
                mCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
                // Draw the drawable
                final int drawableLeft = getPaddingLeft();
                final int drawableTop = getPaddingTop();
                final Drawable[] drawables = getCompoundDrawables();
                for (int i = 0; i < drawables.length; ++i) {
                    if (drawables[i] != null) {
                        drawables[i].setBounds(drawableLeft, drawableTop,
                                drawableLeft + drawables[i].getIntrinsicWidth(),
                                drawableTop + drawables[i].getIntrinsicHeight());
                        drawables[i].draw(mCanvas);
                    }
                }
                final int left = w - getPaddingRight() - textWidth;
                final int bottom = (h + textBounds.height()) / 2;
                // Draw the outline of the text
                mPaint.setStrokeWidth(mStrokeWidth);
                mPaint.setColor(mStrokeColor);
                mPaint.setTextSize(getTextSize());
                mCanvas.drawText(text, left, bottom, mPaint);
                
                // Draw the text itself
                mPaint.setStrokeWidth(0);
                mPaint.setColor(mTextColor);
                mCanvas.drawText(text, left, bottom, mPaint);
                mUpdateCachedBitmap = false;
            }
            canvas.drawBitmap(mCache, 0, 0, mPaint);
        } else {
            super.onDraw(canvas);
        }
    }
    
    private  int colorCount=0;
    
    private   int[] colors=new int[]{
            Color.parseColor("#a364d9"),
            Color.parseColor("#ee6579"),
            Color.parseColor("#db3937"),
            Color.parseColor("#f4631e"),
            Color.parseColor("#f8a227"),
            Color.parseColor("#fecc2f"),
            Color.parseColor("#b3c123"),
            Color.parseColor("#33beb7"),
            Color.parseColor("#40a3d8")
    
    
    };
    
}
