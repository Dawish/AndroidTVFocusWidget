
package custom.droid.layout;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import custom.droid.R;
import custom.droid.animation.AnimationFocusView;

public class Screen extends ViewGroup {

    private static final String TAG = "Screen";

    private AnimationFocusView mAnimationFocusView;

    public Screen(Context context) {
        this(context, null);
    }

    public Screen(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Screen(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.d(TAG, "onLayout, changed : " + changed + " l : " + l + " t : " + t + " r : " + r + " b : " + b);
        int height = 0;
        int width = 0;
        int maxLeft = 0;
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child instanceof CellLayout) {
                final LayoutParams lp = (LayoutParams) child.getLayoutParams();
                width = child.getMeasuredWidth();
                height = child.getMeasuredHeight();
                // location of child
                int cl, ct, cr, cb;
                cl = lp.leftMargin + maxLeft;
                ct = lp.topMargin;

                cr = width + cl + lp.rightMargin;
                cb = height + ct + lp.bottomMargin;
                Log.d(TAG, "onLayout, CellLayout@ cl : " + cl + " ct : " + ct + " cr : " + cr + " cb : " + cb
                        + " maxLeft : " + maxLeft);
                child.layout(cl, ct, cr, cb);
                maxLeft = cr;
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        Log.d(TAG, "onMeasure, widthMode : " + widthMode + " heightMode : " + heightMode);
        Log.d(TAG, "onMeasure, sizeWidth : " + sizeWidth + " sizeHeight : " + sizeHeight);

        // calc w & h of all children
        measureChildren(widthMeasureSpec, heightMeasureSpec);

        // FIXME
        int width = 0;
        int height = 0;
        int maxWidth = 0;
        int maxHeight = 0;

        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child instanceof CellLayout) {
                final LayoutParams lp = (LayoutParams) child.getLayoutParams();
                // FIXME horizontal or vertical
                width += child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
                height = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
                Log.d(TAG, "onMeasure, leftMargin : " + lp.leftMargin + " rightMargin : " + lp.rightMargin
                        + " topMargin : " + lp.topMargin + " bottomMargin : " + lp.bottomMargin + " width : " + width
                        + " height : " + height + " (widthMode == MeasureSpec.EXACTLY) : "
                        + (widthMode == MeasureSpec.EXACTLY) + " (heightMode == MeasureSpec.EXACTLY) : "
                        + (heightMode == MeasureSpec.EXACTLY));
                maxWidth = Math.max(maxWidth, width);
                maxHeight = Math.max(maxHeight, height);
            }
        }

        setMeasuredDimension(maxWidth, maxHeight);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    public class LayoutParams extends MarginLayoutParams {

        public LayoutParams(Context context, AttributeSet attrs) {
            super(context, attrs);
            leftMargin = 2;
            topMargin = 2;
        }
    }

    protected void onFocusChanged(View view, Rect rect) {
        Rect tmp = new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
        if (mAnimationFocusView == null) {
            View v = (View) getParent();
            mAnimationFocusView = (AnimationFocusView) v.findViewById(R.id.focus_view);
        }
        int[] loc = new int[2];
        LayoutUtils.getDescendantCoordRelativeToParent(view, (View) getParent(), loc, true);
        tmp.offsetTo(loc[0], loc[1]);
        tmp.inset(-5, -5);
        mAnimationFocusView.startAnimation(tmp);
    }
}
