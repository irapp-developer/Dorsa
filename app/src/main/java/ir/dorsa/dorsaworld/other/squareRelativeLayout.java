package ir.dorsa.dorsaworld.other;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by mehdi on 1/25/16 AD.
 */
public class squareRelativeLayout extends RelativeLayout {
    public squareRelativeLayout(Context context) {
        super(context);
    }

    public squareRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public squareRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public squareRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private static final double WIDTH_RATIO = 1;
    private static final double HEIGHT_RATIO = 1;
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = (int) (HEIGHT_RATIO / WIDTH_RATIO * widthSize);
        int newHeightSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, newHeightSpec);
    }
}
