
package org.bangbang.song.focuslayer.animation;

import org.bangbang.song.focuslayer.BaseAnimationFocusLayer;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsoluteLayout;

/**
 * 
 * impl by {@link Animation}.
 * @author bysong
 *
 */
public class AnimationFocusLayer extends BaseAnimationFocusLayer {
    protected static final String TAG = AnimationFocusLayer.class.getSimpleName();

    public AnimationFocusLayer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init();
    }

    public AnimationFocusLayer(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public AnimationFocusLayer(Context context) {
        super(context);

        init();
    }

    private void init() {
//        mFocusRectView.setBackgroundColor(Color.RED);
    }

    @Override
    public void onNewFocus(View focus) {
        super.onNewFocus(focus);

        doAnimation();
    }

    private void doAnimation() {
        animateTranslateView();
        
        animateLastFocusView();
        animateCurrentFocusView();
    }

    private void animateTranslateView() {
        LayoutParams params = null;
        Rect currentRect = mConfigure.mCurrentScaledFocusRect;
        Rect lastRect = mConfigure.mLastScaledFocusRect;

        int width = currentRect.width();
        int height = currentRect.height();
        int x = currentRect.left + OFFSET_X;
        int y = currentRect.top + OFFSET_Y;
        if (mConfigure.DEBUG_TRANSFER_ANIMATION) {
            Log.d(TAG, "new layout params x: " + x + " y: " + y + " width: " + width + " height: "
                    + height);
        }
        params = new AbsoluteLayout.LayoutParams(width, height, x, y);
        updateViewLayout(mFocusRectView, params);
        
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.setInterpolator(new LinearInterpolator());

        // XXX this do not work. bysong@tudou.com
        animationSet.setDuration(mConfigure.mDuration);

        float fromX = (float) lastRect.width() / currentRect.width();
        float toX = 1;
        float fromY = (float) lastRect.height() / currentRect.height();
        float toY = 1;
        float pivotX = currentRect.exactCenterX();
        float pivotY = currentRect.exactCenterY();
        pivotX = (float) mFocusRectView.getWidth() / 2;
        pivotY = (float) mFocusRectView.getHeight() / 2;
        pivotX = (float) currentRect.width() / 2;
        pivotY = (float) currentRect.height() / 2;
        
        pivotX = (float) 0.5;
        pivotY = (float) 0.5;
        if (mConfigure.DEBUG_SCALE_ANIMATION) {
            Log.d(TAG, "scale fromX: " + fromX + " toX: " + toX + " fromY: " + fromY + " toY: "
                    + toY + " pivotX: " + pivotX + " pivotY: " + pivotY);
        }
        ScaleAnimation s = new ScaleAnimation(fromX, toX, fromY, toY, Animation.RELATIVE_TO_SELF,
                pivotX, Animation.RELATIVE_TO_SELF, pivotY);
        s.setDuration(mConfigure.mDuration);
        s.setInterpolator(new LinearInterpolator());
        
        float fromXDelta = lastRect.centerX() - currentRect.centerX();
        float toXDelta = 0;
        float fromYDelta = lastRect.centerY() - currentRect.centerY();
        float toYDelta = 0;
        if (mConfigure.DEBUG_TRANSFER_ANIMATION) {
            Log.d(TAG, "translate fromXDelta: " + fromXDelta + " toXDelta: " + toXDelta
                    + " fromYDelta: " + fromYDelta + " toYDelta: " + toYDelta);
        }
        TranslateAnimation t = new TranslateAnimation(fromXDelta, toXDelta, fromYDelta, toYDelta);
        t.setDuration(mConfigure.mDuration);
        t.setInterpolator(new LinearInterpolator());
        
        // order is important, scale firstly, then translate.
        animationSet.addAnimation(s);
        animationSet.addAnimation(t);
        
        mFocusRectView.startAnimation(animationSet);
    }

    private void animateLastFocusView() {
        Rect rect = mConfigure.mLastFocusRect;
        Rect scaledRect = mConfigure.mLastScaledFocusRect;
        LayoutParams params = null;

        int width = rect.width();
        int height = rect.height();
        int x = rect.left + OFFSET_X;
        int y = rect.top + OFFSET_Y;
        if (mConfigure.DEBUG_TRANSFER_ANIMATION) {
            Log.d(TAG, "new layout params x: " + x + " y: " + y + " width: " + width + " height: "
                    + height);
        }
        params = new AbsoluteLayout.LayoutParams(width, height, x, y);
        updateViewLayout(mLastFocusView, params);
        
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
        mLastFocusView.startAnimation(s);
    }

    private void animateCurrentFocusView() {
        Rect scaledRect = mConfigure.mCurrentScaledFocusRect;
        Rect rect = mConfigure.mCurrentFocusRect;
        LayoutParams params = null;

        int width = scaledRect.width();
        int height = scaledRect.height();
        int x = scaledRect.left + OFFSET_X;
        int y = scaledRect.top + OFFSET_Y;
        if (mConfigure.DEBUG_TRANSFER_ANIMATION) {
            Log.d(TAG, "new layout params x: " + x + " y: " + y + " width: " + width + " height: "
                    + height);
        }
        params = new AbsoluteLayout.LayoutParams(width, height, x, y);
        updateViewLayout(mCurrentFocusView, params);
        
        float fromX = (float) rect.width() / scaledRect.width();
        float toX = 1;
        float fromY = (float) rect.height() / scaledRect.height();
        float toY = 1;
        float pivotX = scaledRect.exactCenterX();
        float pivotY = scaledRect.exactCenterY();
        pivotX = (float) mFocusRectView.getWidth() / 2;
        pivotY = (float) mFocusRectView.getHeight() / 2;
        pivotX = (float) scaledRect.width() / 2;
        pivotY = (float) scaledRect.height() / 2;
        
        pivotX = (float) 0.5;
        pivotY = (float) 0.5;
        if (mConfigure.DEBUG_SCALE_ANIMATION) {
            Log.d(TAG, "scale fromX: " + fromX + " toX: " + toX + " fromY: " + fromY + " toY: "
                    + toY + " pivotX: " + pivotX + " pivotY: " + pivotY);
        }
        ScaleAnimation s = new ScaleAnimation(fromX, toX, fromY, toY, Animation.RELATIVE_TO_SELF,
                pivotX, Animation.RELATIVE_TO_SELF, pivotY);
        s.setDuration(mConfigure.mDuration);
        s.setInterpolator(new LinearInterpolator());
        mCurrentFocusView.startAnimation(s);
    }    
}
