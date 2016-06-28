
package org.bangbang.song.focuslayer.animator;

import org.bangbang.song.focuslayer.BaseFocusLayout;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
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
public class ScaleAnimatorFocusLayout extends BaseFocusLayout implements AnimatorListener {
    static final String TAG = ScaleAnimatorFocusLayout.class.getSimpleName();

    public ScaleAnimatorFocusLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    public ScaleAnimatorFocusLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public ScaleAnimatorFocusLayout(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void doScalDown(View v) {
        super.doScalDown(v);

        Rect r = (Rect) v.getTag(ID_ORIGINAL_BOUND);
        if (null == r) {
            return;
        }

        // updatePosition(v, r);

        Rect rect = r;
        Rect scaledRect = mConfigure.mLastScaledFocusRect;

        ViewPropertyAnimator animator = v.animate();
        v.setPivotX(0);
        v.setPivotY(0);

        // step 2 calculate transformation.
        float toX = rect.left;
        float toY = rect.top;
        float toScaleX = ((float) rect.width()) / scaledRect.width();
        float toScaleY = ((float) rect.height()) / scaledRect.height();
        toScaleX = 1;
        toScaleY = 1;
        animator.x(toX);
        animator.y(toY);
        animator.scaleX(toScaleX);
        animator.scaleY(toScaleY);
        animator.setDuration(mConfigure.mDuration);
        if (mConfigure.DEBUG_SCALE_ANIMATION) {
            Log.d(TAG, "toX: " + toX + " toY: " + toY
                    + " toScaleX: " + toScaleX + " toScaleY: " + toScaleY);
        }
        animator.setListener(this);
        animator.start();
    }

    @Override
    protected void doScalUp(View v) {
        super.doScalUp(v);

        // step 1 update new position
        Rect rect = mConfigure.mCurrentScaledFocusRect;
        // updatePosition(v, rect);

        rect = (Rect) v.getTag(ID_ORIGINAL_BOUND);
        Rect scaledRect = mConfigure.mCurrentScaledFocusRect;

        ViewPropertyAnimator animator = v.animate();
        v.setPivotX(0);
        v.setPivotY(0);

        // step 2 calculate transformation.
        float toX = scaledRect.left;
        float toY = scaledRect.top;
        float toScaleX = ((float) scaledRect.width()) / rect.width();
        float toScaleY = ((float) scaledRect.height()) / rect.height();
        animator.x(toX);
        animator.y(toY);
        animator.scaleX(toScaleX);
        animator.scaleY(toScaleY);
        animator.setDuration(mConfigure.mDuration);
        if (mConfigure.DEBUG_SCALE_ANIMATION) {
            Log.d(TAG, "toX: " + toX + " toY: " + toY
                    + " toScaleX: " + toScaleX + " toScaleY: " + toScaleY);
        }
        animator.setListener(this);
        animator.start();
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

}
