package ir.dorsa.dorsaworld.other;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class cViewPager extends ViewPager{

	public cViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	public cViewPager(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	 @Override
	    public boolean onInterceptTouchEvent(MotionEvent event) {
	        // Never allow swiping to switch between pages
	        return false;
	    }

	    @Override
	    public boolean onTouchEvent(MotionEvent event) {
	        // Never allow swiping to switch between pages
	        return false;
	    }
	

}
