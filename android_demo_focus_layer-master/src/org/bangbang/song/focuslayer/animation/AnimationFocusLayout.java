package org.bangbang.song.focuslayer.animation;

import org.bangbang.song.focuslayer.BaseFocusLayout;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.Transformation;

/**
 * 
 * impl by {@link Animation}
 * 
 * @author bysong
 * @see #onInvalidateParent()
 */
public class AnimationFocusLayout extends BaseFocusLayout implements AnimationListener {
    static final String TAG = AnimationFocusLayout.class.getSimpleName();
    public AnimationFocusLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    public AnimationFocusLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public AnimationFocusLayout(Context context) {
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
        
        updatePosition(v, r);
        
        Rect rect = r;
        v.getDrawingRect(mTmpRect);
        Rect scaledRect = mConfigure.mLastScaledFocusRect;
        scaledRect = new Rect(mTmpRect);
        
        float fromX = (float) scaledRect.width() / rect.width();
        float toX = 1;
        float fromY = (float) scaledRect.height() / rect.height();
        float toY = 1;
        float pivotX = .5f;
        float pivotY = .5f;
        if (mConfigure.DEBUG_SCALE_ANIMATION) {
            Log.d(TAG, "scale fromX: " + fromX + " toX: " + toX + " fromY: " + fromY + " toY: "
                    + toY + " pivotX: " + pivotX + " pivotY: " + pivotY);
        }
        ScaleAnimation s = new ScaleAnimation(fromX, toX, fromY, toY, Animation.RELATIVE_TO_SELF,
                pivotX, Animation.RELATIVE_TO_SELF, pivotY);
        s.setDuration(mConfigure.mDuration);
        s.setInterpolator(new LinearInterpolator());
        s.setAnimationListener(this);
        v.startAnimation(s);
    }
    
    @Override
    protected void doScalUp(View v) {
        super.doScalUp(v);
        
        // step 1 update new position
        Rect rect = mConfigure.mCurrentScaledFocusRect;
        updatePosition(v, rect);
        
        rect = (Rect) v.getTag(ID_ORIGINAL_BOUND);
        Rect scaledRect = mConfigure.mCurrentScaledFocusRect;
        
        // step 2 calculate transformation.
        float fromX = (float) rect.width() / scaledRect.width();
        float toX = 1;
        float fromY = (float) rect.height() / scaledRect.height();
        float toY = 1;
        float pivotX = scaledRect.exactCenterX();
        float pivotY = scaledRect.exactCenterY();
        
        pivotX = 0.5f;
        pivotY = 0.5f;
        if (mConfigure.DEBUG_SCALE_ANIMATION) {
            Log.d(TAG, "scale fromX: " + fromX + " toX: " + toX + " fromY: " + fromY + " toY: "
                    + toY + " pivotX: " + pivotX + " pivotY: " + pivotY);
        }
        ScaleAnimation s = new ScaleAnimation(fromX, toX, fromY, toY, Animation.RELATIVE_TO_SELF,
                pivotX, Animation.RELATIVE_TO_SELF, pivotY) {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                // TODO Auto-generated method stub
                super.applyTransformation(interpolatedTime, t);
                
                onInvalidateParent();
            }
        };
        s.setDuration(mConfigure.mDuration);
        s.setInterpolator(new LinearInterpolator());
        s.setAnimationListener(this);
        v.startAnimation(s);
    }

    /**
     * the scale up animation will overlap parent's (or grandfather's) region, so
     * you should invalidate it properly.
     */
    protected void onInvalidateParent() {
        
    }

    @Override
    public void onAnimationStart(Animation animation) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
        // TODO Auto-generated method stub
        
    }    
    
}