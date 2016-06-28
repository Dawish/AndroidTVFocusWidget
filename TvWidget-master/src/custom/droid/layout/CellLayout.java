
package custom.droid.layout;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

public class CellLayout extends ViewGroup {

    private static final String TAG = "CellLayout";

    public CellLayout(Context context) {
        this(context, null);
    }

    public CellLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CellLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.d(TAG, "onLayout, changed : " + changed + " l : " + l + " t : " + t + " r : " + r + " b : " + b);
        int cHeight = 0;
        int cWidth = 0;
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != View.GONE) {
                final LayoutParams lp = (LayoutParams) child.getLayoutParams();
                cWidth = child.getMeasuredWidth();
                cHeight = child.getMeasuredHeight();

                int cl, ct, cr, cb;
                cl = lp.leftMargin;
                ct = lp.topMargin;

                cr = cWidth + cl + lp.rightMargin;
                cb = cHeight + ct + lp.bottomMargin;

                Log.d(TAG, "onLayout, cl : " + cl + " ct : " + ct + " cr : " + cr + " cb : " + cb);
                child.layout(cl, ct, cr, cb);
            }
        }
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        Log.d(TAG,
                "onFocusChanged, gainFocus : " + gainFocus + " direction : " + direction + " rect : "
                        + (previouslyFocusedRect != null ? previouslyFocusedRect.toString() : " null ") + " id : "
                        + Integer.toHexString(getId()));
        if (getParent() instanceof Screen) {
            Screen screen = (Screen) getParent();
            screen.onFocusChanged(this, previouslyFocusedRect);
        }
    }

    public static class LayoutParams extends ViewGroup.MarginLayoutParams {

        public LayoutParams(Context context, AttributeSet attr) {
            super(context, attr);
            leftMargin = 5;
            topMargin = 5;
        }
    }
}
