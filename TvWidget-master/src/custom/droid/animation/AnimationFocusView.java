
package custom.droid.animation;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import custom.droid.CustomApplication;
import custom.droid.R;

public class AnimationFocusView extends View implements AnimatorUpdateListener {

    private static final String TAG = "AnimationFocusView";

    private AnimationObject mAnimationObject;

    private Animator mAnimator;

    private Drawable mDrawable;

    public AnimationFocusView(Context context) {
        this(context, null);
    }

    public AnimationFocusView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnimationFocusView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // FIXME init drawable resource
        mDrawable = context.getResources().getDrawable(R.drawable.default_focus);

        mAnimationObject = new AnimationObject();

        // init animator & interpolator
        mAnimator = new AnimatorSet();
        mAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mDrawable != null) {
            CustomApplication.DLOGD(TAG, "onDraw, " + mAnimationObject.toString());

            // draw at (mAnimationObject.getX(), mAnimationObject.getY())
            canvas.translate(mAnimationObject.getX(), mAnimationObject.getY());
            // must do this
            mDrawable.setBounds(0, 0, mAnimationObject.getWidth(), mAnimationObject.getHeight());
            // draw drawable into canvas
            mDrawable.draw(canvas);
        }
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        invalidate();
    }

    public void startAnimation(Rect r) {
        if (mAnimator != null) {
            if (mAnimator.isRunning()) {
                mAnimator.cancel();
            }
            CustomApplication.DLOGD(TAG, "startAnimation, " + r.toString());
            CustomApplication.DLOGD(TAG, "startAnimation, " + mAnimationObject.toString());

            // value(arg1) change from arg2 to arg3
            PropertyValuesHolder pvhW = PropertyValuesHolder.ofInt("width", mAnimationObject.getWidth(), r.width());
            PropertyValuesHolder pvhH = PropertyValuesHolder.ofInt("height", mAnimationObject.getHeight(), r.height());
            PropertyValuesHolder pvTX = PropertyValuesHolder.ofFloat("x", mAnimationObject.getX(), r.left);
            PropertyValuesHolder pvTY = PropertyValuesHolder.ofFloat("y", mAnimationObject.getY(), r.top);
            PropertyValuesHolder pvSX = PropertyValuesHolder.ofFloat("scaleX", mAnimationObject.getScaleX(),
                    mAnimationObject.getScaleX());
            PropertyValuesHolder pvSY = PropertyValuesHolder.ofFloat("scaleY", mAnimationObject.getScaleY(),
                    mAnimationObject.getScaleY());

            ObjectAnimator allAnimator = ObjectAnimator.ofPropertyValuesHolder(mAnimationObject, pvhW, pvhH, pvTX,
                    pvTY, pvSX, pvSY).setDuration(500);
            // listen the callback for draw drawable
            allAnimator.addUpdateListener(this);

            ((AnimatorSet) mAnimator).playTogether(allAnimator);
            mAnimator.start();
        }
    }
}
