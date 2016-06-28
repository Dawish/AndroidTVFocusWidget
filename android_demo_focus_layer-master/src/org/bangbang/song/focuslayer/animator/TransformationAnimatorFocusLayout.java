
package org.bangbang.song.focuslayer.animator;

import org.bangbang.song.focuslayer.BaseFocusLayout;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewPropertyAnimator;

/**
 * impl by {@link Animator}
 * 
 * @author bysong
 */
public class TransformationAnimatorFocusLayout extends BaseFocusLayout implements AnimatorListener {
    static final String TAG = TransformationAnimatorFocusLayout.class.getSimpleName();

    public TransformationAnimatorFocusLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    public TransformationAnimatorFocusLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public TransformationAnimatorFocusLayout(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void doScalDown(final View v) {
        super.doScalDown(v);

        Rect r = (Rect) v.getTag(ID_ORIGINAL_BOUND);
        if (null == r) {
            return;
        }

        final Rect rect = r;
        // XXX doScalDown will be called before doScaleUp
        // focus will be lost in old view and gained in new view.
//        final Rect scaledRect = mConfigure.mLastScaledFocusRect;
        final Rect scaledRect = mConfigure.mCurrentScaledFocusRect;

        ValueAnimator a = ValueAnimator.ofObject(new RectTypeEvaluator(), new Rect[] {scaledRect,rect,});
        a.addUpdateListener(new AnimatorUpdateListener() {
			
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				Rect r = (Rect) animation.getAnimatedValue();
//				Log.d(TAG, "new Rect: " + r);
				
				updatePosition(TransformationAnimatorFocusLayout.this, v, r);
			}
		});

        if (mConfigure.DEBUG_SCALE_ANIMATION) {
            Log.d(TAG, "ScalDown: fromRect: " + scaledRect + " toRect: " + rect);
        }
        
        a.setDuration(mConfigure.mDuration);
        a.start();
    }

    @Override
    protected void doScalUp(final View v) {
        super.doScalUp(v);

        Rect rect = (Rect) v.getTag(ID_ORIGINAL_BOUND);
        Rect scaledRect = mConfigure.mCurrentScaledFocusRect;

        ValueAnimator a = ValueAnimator.ofObject(new RectTypeEvaluator(), new Rect[] {rect, scaledRect});
        a.addUpdateListener(new AnimatorUpdateListener() {
			
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				Rect r = (Rect) animation.getAnimatedValue();
//				Log.d(TAG, "new Rect: " + r);
				
				updatePosition(TransformationAnimatorFocusLayout.this, v, r);
			}
		});

        if (mConfigure.DEBUG_SCALE_ANIMATION) {
            Log.d(TAG, "scaleUp: fromRect: " + rect + " toRect: " + scaledRect);
        }
        
        a.setDuration(mConfigure.mDuration);
        a.start();
    }

    @Override
    public void onAnimationStart(Animator animation) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onAnimationCancel(Animator animation) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onAnimationRepeat(Animator animation) {
        // TODO Auto-generated method stub
        
    }
    
    public static class RectTypeEvaluator implements TypeEvaluator<Rect> {

		@Override
		public Rect evaluate(float fraction, Rect startValue, Rect endValue) {
			int l = (int) (startValue.left + (fraction) * (endValue.left - startValue.left));
			int t = (int) (startValue.top + (fraction) * (endValue.top - startValue.top));
			int r = (int) (startValue.right + (fraction) * (endValue.right - startValue.right));
			int b = (int) (startValue.bottom + (fraction) * (endValue.bottom - startValue.bottom));
			Rect rect = new Rect(l,t,r,b);
			return rect;
		}
    	
    }
}
