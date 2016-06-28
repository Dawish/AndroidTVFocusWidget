
package custom.droid.layout;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.HorizontalScrollView;

public class ScreenHorizontalScrollView extends HorizontalScrollView {

    private static final String TAG = "ScreenHorizontalScrollView";

    public ScreenHorizontalScrollView(Context context) {
        this(context, null);
        Log.d(TAG, "ScreenHorizontalScrollView(Context context)");
    }

    public ScreenHorizontalScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        Log.d(TAG, "ScreenHorizontalScrollView(Context context, AttributeSet attrs)");
    }

    public ScreenHorizontalScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Log.d(TAG, "ScreenHorizontalScrollView(Context context, AttributeSet attrs, int defStyle)");
    }

    @Override
    protected int computeHorizontalScrollOffset() {
        Log.d(TAG, "computeHorizontalScrollOffset");
        return super.computeHorizontalScrollOffset();
    }

    @Override
    protected int computeHorizontalScrollRange() {
        Log.d(TAG, "computeHorizontalScrollRange");
        return super.computeHorizontalScrollRange();
    }

    @Override
    protected int computeScrollDeltaToGetChildRectOnScreen(Rect rect) {
        Log.d(TAG, "computeScrollDeltaToGetChildRectOnScreen");
        return super.computeScrollDeltaToGetChildRectOnScreen(rect);
    }

    @Override
    protected void measureChild(View child, int parentWidthMeasureSpec, int parentHeightMeasureSpec) {
        Log.d(TAG, "measureChild");
        super.measureChild(child, parentWidthMeasureSpec, parentHeightMeasureSpec);
    }

    @Override
    protected void measureChildWithMargins(View child, int parentWidthMeasureSpec, int widthUsed,
            int parentHeightMeasureSpec, int heightUsed) {
        Log.d(TAG, "measureChildWithMargins");
        super.measureChildWithMargins(child, parentWidthMeasureSpec, widthUsed, parentHeightMeasureSpec, heightUsed);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.d(TAG, "onLayout, l : " + l + " t : " + t + " r : " + r + " b : " + b);
        super.onLayout(changed, l, t, r, b);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.d(TAG, "onMeasure, measured height : " + getMeasuredHeight() + " measured width : " + getMeasuredWidth()
                + " heightMeasureSpec : " + heightMeasureSpec + " widthMeasureSpec : " + widthMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        Log.d(TAG, "onOverScrolled");
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        Log.d(TAG, "onSizeChanged");
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    public boolean pageScroll(int direction) {
        Log.d(TAG, "pageScroll");
        return super.pageScroll(direction);
    }
}
