
package org.bangbang.song.focuslayer.animator;

import org.bangbang.song.demo.focuslayer.R;
import org.bangbang.song.focuslayer.BaseAnimationFocusLayer;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.AnimatorSet.Builder;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsoluteLayout;

/**
 * impl focus layer by {@link Animator}.
 * 
 * @author bysong@tudou.com
 */
public class AnimatorFocusLayer extends BaseAnimationFocusLayer implements AnimatorUpdateListener {
    private static final String TAG = AnimatorFocusLayer.class.getSimpleName();

    public AnimatorFocusLayer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init();
    }

    public AnimatorFocusLayer(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public AnimatorFocusLayer(Context context) {
        super(context);

        init();
    }

    private void init() {
    }

    @Override
    protected void onNewFocus(View focus) {
        super.onNewFocus(focus);

        doAnimation();
    }

    private void doAnimation() {
        // focus rect.
        float fromX = mConfigure.mLastScaledFocusRect.left;
        float toX = mConfigure.mCurrentScaledFocusRect.left;
        float fromY = mConfigure.mLastScaledFocusRect.top;
        float toY = mConfigure.mCurrentScaledFocusRect.top;
        if (mConfigure.DEBUG_TRANSFER_ANIMATION) {
            Log.d(TAG, "mFocusRectView::x-y fromX: " + fromX + " toX: " + toX + " fromY: " + fromY
                    + " toY: " + toY);
        }
        ObjectAnimator animatorRectX = ObjectAnimator.ofFloat(mFocusRectView, "x", fromX, toX);
        animatorRectX.setDuration(mConfigure.mDuration);
        ObjectAnimator animatorRectY = ObjectAnimator.ofFloat(mFocusRectView, "y", fromY, toY);
        animatorRectY.setDuration(mConfigure.mDuration);

        float fromW = mConfigure.mLastScaledFocusRect.width();
        float toW = mConfigure.mCurrentScaledFocusRect.width();
        float fromH = mConfigure.mLastScaledFocusRect.height();
        float toH = mConfigure.mCurrentScaledFocusRect.height();
        if (mConfigure.DEBUG_TRANSFER_ANIMATION) {
            Log.d(TAG, "mFocusRectView::w-h fromW: " + fromW + " toW: " + toW + " fromH: " + fromH
                    + " toH: " + toH);
        }
        ValueAnimator animatorRectW = ObjectAnimator.ofFloat(mFocusRectView, "width", fromW, toW);
        animatorRectW.setDuration(mConfigure.mDuration);
        animatorRectW.addUpdateListener(this);
        ObjectAnimator animatorRectH = ObjectAnimator.ofFloat(mFocusRectView, "height", fromH, toH);
        animatorRectH.setDuration(mConfigure.mDuration);
        animatorRectH.addUpdateListener(this);

        AnimatorSet set = new AnimatorSet();
        // XXX do NOT work. bysong@tudou.com
        set.setDuration(mConfigure.mDuration);

        Builder animatorBuilder = set
                .play(animatorRectW)
                .with(animatorRectH)
                .with(animatorRectX)
                .with(animatorRectY);

        doScaleAnimation(animatorBuilder);

        set.start();
    }

    private void doScaleAnimation(Builder animatorBuilder) {
        if (!mConfigure.mDisableScaleAnimation) {
            float fromX, toX;
            float fromY, toY;
            float fromW, toW;
            float fromH, toH;

            // last focus view
            fromX = mConfigure.mLastScaledFocusRect.left;
            toX = mConfigure.mLastFocusRect.left;
            fromY = mConfigure.mLastScaledFocusRect.top;
            toY = mConfigure.mLastFocusRect.top;
            ObjectAnimator animatorLastX = ObjectAnimator.ofFloat(mLastFocusView, "x", fromX, toX);
            animatorLastX.setDuration(mConfigure.mDuration);
            ObjectAnimator animatorLastY = ObjectAnimator.ofFloat(mLastFocusView, "y", fromY, toY);
            animatorLastY.setDuration(mConfigure.mDuration);
            if (mConfigure.DEBUG_SCALE_ANIMATION) {
                Log.d(TAG, "mLastFocusView::x-y fromX: " + fromX + " toX: " + toX + " fromY: "
                        + fromY + " toY: " + toY);
            }

            fromW = mConfigure.mLastScaledFocusRect.width();
            toW = mConfigure.mLastFocusRect.width();
            fromH = mConfigure.mLastScaledFocusRect.height();
            toH = mConfigure.mLastFocusRect.height();
            ValueAnimator animatorLastW = ObjectAnimator.ofFloat(mLastFocusView, "width", fromW,
                    toW);
            animatorLastW.setDuration(mConfigure.mDuration);
            animatorLastW.addUpdateListener(this);
            ObjectAnimator animatorLastH = ObjectAnimator.ofFloat(mLastFocusView, "height", fromH,
                    toH);
            animatorLastH.setDuration(mConfigure.mDuration);
            animatorLastH.addUpdateListener(this);
            if (mConfigure.DEBUG_SCALE_ANIMATION) {
                Log.d(TAG, "mLastFocusView::w-h fromW: " + fromW + " toW: " + toW + " fromH: "
                        + fromH + " toH: " + toH);
            }

            // current focus view
            fromX = mConfigure.mCurrentFocusRect.left;
            toX = mConfigure.mCurrentScaledFocusRect.left;
            fromY = mConfigure.mCurrentFocusRect.top;
            toY = mConfigure.mCurrentScaledFocusRect.top;
            ObjectAnimator animatorCurrentX = ObjectAnimator.ofFloat(mCurrentFocusView, "x", fromX,
                    toX);
            animatorCurrentX.setDuration(mConfigure.mDuration);
            ObjectAnimator animatorCurrentY = ObjectAnimator.ofFloat(mCurrentFocusView, "y", fromY,
                    toY);
            animatorCurrentY.setDuration(mConfigure.mDuration);
            if (mConfigure.DEBUG_SCALE_ANIMATION) {
                Log.d(TAG, "mCurrentFocusView::x-y fromX: " + fromX + " toX: " + toX + " fromY: "
                        + fromY + " toY: " + toY);
            }

            fromW = mConfigure.mCurrentFocusRect.width();
            toW = mConfigure.mCurrentScaledFocusRect.width();
            fromH = mConfigure.mCurrentFocusRect.height();
            toH = mConfigure.mCurrentScaledFocusRect.height();
            ValueAnimator animatorCurrentW = ObjectAnimator.ofFloat(mCurrentFocusView, "width",
                    fromW, toW);
            animatorCurrentW.setDuration(mConfigure.mDuration);
            animatorCurrentW.addUpdateListener(this);
            ObjectAnimator animatorCurrentH = ObjectAnimator.ofFloat(mCurrentFocusView, "height",
                    fromH, toH);
            animatorCurrentH.setDuration(mConfigure.mDuration);
            if (mConfigure.DEBUG_SCALE_ANIMATION) {
                Log.d(TAG, "mCurrentFocusView::w-h fromW: " + fromW + " toW: " + toW + " fromH: "
                        + fromH + " toH: " + toH);
            }

            animatorBuilder
                    .with(animatorLastX)
                    .with(animatorLastY)
                    .with(animatorLastW)
                    .with(animatorLastH)

                    .with(animatorCurrentX)
                    .with(animatorCurrentY)
                    .with(animatorCurrentW)
                    .with(animatorCurrentH);
        }
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        // XXX why we need this.
        updateFocusLayoutparams();
    }

    private void updateFocusLayoutparams() {
        AbsoluteLayout.LayoutParams layoutParams = (LayoutParams) mFocusRectView.getLayoutParams();
        int w = mFocusRectView.getWidth();
        int h = mFocusRectView.getHeight();
        layoutParams.width = w;
        layoutParams.height = h;
        // Log.d(TAG, "updateFocusLayoutparams. x:" + layoutParams.x + " y:" +
        // layoutParams.y + " w:" + layoutParams.width + " h:" +
        // layoutParams.height);
        updateViewLayout(mFocusRectView, layoutParams);
    }

    @Override
    protected View onInflateScaleAnimationView(LayoutInflater layoutInflater) {
        View v = new AnimatableView(getContext());
        return v;
    }

    @Override
    protected View onInflateTranslateAnimationView(LayoutInflater layoutInflater) {
        View v = new AnimatableView(getContext());

        v.setBackgroundResource(R.drawable.search_button_hover);
        return v;
    }

    /**
     * <p>
     * to incorporate with {@link Animator}, add {@link #setWidth(float)} and
     * {@link #setHeight(float)} method.
     * <p>
     * after construct it, you should explicitly set width & height
     * 
     * @see #setWidth(float)
     * @see #setHeight(float)
     * @author bysong
     */
    public static class AnimatableView extends View implements IAnimatableView
    {

        private int mWidth;
        private int mHeight;

        public AnimatableView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);

            init();
        }

        public AnimatableView(Context context, AttributeSet attrs) {
            super(context, attrs);

            init();
        }

        public AnimatableView(Context context) {
            super(context);

            init();
        }

        void init() {
            mWidth = 0;
            mHeight = 0;
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);

            int measuredWidth = MeasureSpec.makeMeasureSpec(MeasureSpec.EXACTLY, mWidth);
            int measuredHeight = MeasureSpec.makeMeasureSpec(MeasureSpec.EXACTLY, mHeight);

            // Log.d(TAG, "measuredWidth: " +
            // MeasureSpec.toString(measuredWidth) + " measuredHeight: " +
            // MeasureSpec.toString(measuredHeight));
            setMeasuredDimension(measuredWidth, measuredHeight);
        }

        /* (non-Javadoc)
         * @see org.bangbang.song.focuslayer.animator.IAnimatableView#setWidth(float)
         */
        @Override
        public void setWidth(float w) {
            // Log.d(TAG, "setWidth(). w: " + w);
            mWidth = (int) w;

            // must explicitly 1) request layout & 2)redraw
            requestLayout();
            invalidate();
        }

        /* (non-Javadoc)
         * @see org.bangbang.song.focuslayer.animator.IAnimatableView#setHeight(float)
         */
        @Override
        public void setHeight(float h) {
            // Log.d(TAG, "setHeight(). h: " + h);
            mHeight = (int) h;

            // must explicitly 1) request layout & 2)redraw
            requestLayout();
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            // Log.d(TAG, "onDraw(). view: " + this);
        }

    }
}
