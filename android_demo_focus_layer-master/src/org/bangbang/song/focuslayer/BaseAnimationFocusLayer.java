
package org.bangbang.song.focuslayer;

import org.bangbang.song.android.commonlib.FPSLoger;
import org.bangbang.song.android.commonlib.Grid;
import org.bangbang.song.android.commonlib.Grid.GridDrawer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsoluteLayout;

/**
 * the animated bitmap MUST not has alpha channel, otherwise, 
 * there is a ghost on screen.
 * 
 * @author bysong
 */
public class BaseAnimationFocusLayer extends
        AbsoluteLayout // yes we need this layout absolutely.
        implements IFocusAnimationLayer {
    private static final String TAG = BaseAnimationFocusLayer.class.getSimpleName();

    protected static final int OFFSET_X = 0;
    protected static final int OFFSET_Y = 0;

    private Grid.GridDrawer mGridDrawer;
    private boolean mFirstFocus = true;
    private FPSLoger mFps;
    
    /** used for transfer */
    public View mFocusRectView;
    /** used for scale */
    public View mLastFocusView;
    /** used for scale */
    public View mCurrentFocusView;

    protected AnimationConfigure mConfigure;

    public BaseAnimationFocusLayer(Context context) {
        this(context, null);
    }

    public BaseAnimationFocusLayer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseAnimationFocusLayer(Context context, AttributeSet attrs,
            int defStyle) {
        super(context, attrs, defStyle);

        init();
    }

    private void init() {
        LayoutInflater inflater = ((LayoutInflater) getContext().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE));
        
        mConfigure = new AnimationConfigure();
        mConfigure.mDisableAutoGenBitmap = false;
        
        mLastFocusView = onInflateScaleAnimationView(inflater);
        mCurrentFocusView = onInflateScaleAnimationView(inflater);
        addView(mLastFocusView);
        addView(mCurrentFocusView);

        mFocusRectView = onInflateTranslateAnimationView(inflater);
        addView(mFocusRectView);

        setId(Utils.FOCUS_LAYER_ID);

        setBackgroundColor(Color.TRANSPARENT);
        if (mConfigure.DRAW_GRIG) {
            setWillNotDraw(false);
            mGridDrawer = new GridDrawer(50, 50);
        }
        if (mConfigure.TRACK_FPS) {
            setWillNotDraw(false);
            
            mFps = new FPSLoger(TAG);
        }
    }

    /**
     * inflate focus view, but NOT add to view hierarchy, caller will do this
     * for u.
     * 
     * @param layoutInflater
     * @return
     */
    protected View onInflateTranslateAnimationView(
            LayoutInflater layoutInflater) {
        View v = new View(getContext());
        v.setBackgroundResource(mConfigure.mFocusDrawable);

        return v;
    }
    
    protected View onInflateScaleAnimationView(
            LayoutInflater layoutInflater) {
        return onInflateTranslateAnimationView(layoutInflater);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mConfigure.DRAW_GRIG) {
            int w = getWidth();
            int h = getHeight();
            mGridDrawer.onDraw(canvas, w, h);
        }
        
        if (mConfigure.TRACK_FPS && mFps != null) {
            mFps.onDraw();
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            onNewFocus(v);
        }
    }

    protected void onNewFocus(View focus) {
        if (null == focus) {
            return;
        }
        
        mConfigure.onNewFocus(this, focus);

        if (mFirstFocus) {
            mFirstFocus = false;
            initFocusTarget();
        }
        
        // update view & bitmap
        if (!mConfigure.mDisableScaleAnimation && mConfigure.mCurrentFocusBitmap != null) {
            mCurrentFocusView.setBackgroundColor(Color.BLACK);
//            mCurrentFocusView.setScaleType(ScaleType.FIT_XY);
//            mCurrentFocusView.setImageBitmap(mCurrentFocusBitmap);
            mCurrentFocusView.setBackgroundDrawable(new BitmapDrawable(mConfigure.mCurrentFocusBitmap));
        }
        if (!mConfigure.mDisableScaleAnimation && mConfigure.mLastFocusBitmap != null) {
            mLastFocusView.setBackgroundColor(Color.BLACK);
//            mLastFocusView.setImageBitmap(mLastFocusBitmap);
//            mLastFocusView.setScaleType(ScaleType.FIT_XY);
            mLastFocusView.setBackgroundDrawable(new BitmapDrawable(mConfigure.mLastFocusBitmap));
        }
    }



    private void deubgFocusRect() {
        int width = mFocusRectView.getWidth();
        int height = mFocusRectView.getHeight();
        int left = mFocusRectView.getLeft();
        int top = mFocusRectView.getTop();
        Log.d(TAG, "mFocusRectView left: " + left + " top: " + top + " width: " + width
                + " height: " + height);
    }
    
    @Override
    protected void onAnimationStart() {
        // TODO Auto-generated method stub
        super.onAnimationStart();
        Log.d(TAG, "onAnimationStart");
    }
    @Override
    protected void onAnimationEnd() {
        // TODO Auto-generated method stub
        super.onAnimationEnd();
        Log.d(TAG, "onAnimationEnd");
    }

    private void initFocusTarget() {
            // no animation in firstly focus on.
            
            int width = mConfigure.mCurrentScaledFocusRect.width();
            int height = mConfigure.mCurrentScaledFocusRect.height();
            int x = mConfigure.mCurrentScaledFocusRect.left;
            int y = mConfigure.mCurrentScaledFocusRect.top;
            
            updateViewLayout(mFocusRectView, new AbsoluteLayout.LayoutParams(
                    width,
                    height,
                    x,
                    y
                    ));
    //      updateViewLayout(mCurrentFocusView, new AbsoluteLayout.LayoutParams(
    //              width,
    //              height,
    //              x,
    //              y
    //              ));
    //      updateViewLayout(mLastFocusView, new AbsoluteLayout.LayoutParams(
    //              width, 
    //              height, 
    //              x,
    //              y
    //              ));
        }

    @Override
    public void onFocusSessionEnd(View lastFocus) {
        mConfigure.onFocusSessionEnd(lastFocus);
    }


}
