package org.bangbang.song.focuslayer;

import org.bangbang.song.android.commonlib.FPSLoger;
import org.bangbang.song.demo.focuslayer.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AbsoluteLayout;

/**
 * by adjusting view's Z-order(index), to animate. 
 * but animating can only draw in parent's bound.
 * <p>
 * 
 * z-order(child index) independent layout:
 * <p>
 * <ul>
 * <li>1) AbsoluteLayout
 * <li>2) FrameLayout
 * <li>3) all ViewGroup which only have ONE child.
 * </ul>
 * <p>
 * <em>note:</em>
 * we will add a {@link View.OnFocusChangeListener} on child only.
 * @author bysong
 *
 */
public class BaseFocusLayout extends AbsoluteLayout implements IFocusAnimationLayer {
    private static final String TAG = BaseFocusLayout.class.getSimpleName();
    
    protected static final boolean DEBUG = true;  
    // we don't case the id value, as long as it has one value.
    public static final int ID_ORIGINAL_BOUND = R.id.paste;
    protected AnimationConfigure mConfigure;
    private OnFocusChangeListener mListener;
    private FPSLoger mFPS;

    protected Rect mTmpRect;
    public BaseFocusLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        
        init();
    }

    public BaseFocusLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        init();
    }

    public BaseFocusLayout(Context context) {
        super(context);
        
        init();
    }
    
    void init() {
        mConfigure = new AnimationConfigure();
//        mConfigure.mScaleFactor = 3.1f;
        mConfigure.mDuration = 2000;
        mFPS = new FPSLoger(TAG);
        mTmpRect = new Rect();
        mListener = new OnFocusChangeListener() {
            
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                BaseFocusLayout.this.onFocusChange(v, hasFocus);
            }
        };
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (v.getParent() != this) {
            throw new IllegalStateException("view must be my child.");
        }
        
        if (hasFocus) {
            mConfigure.onNewFocus(this, v);
            if (v.getTag(ID_ORIGINAL_BOUND)  == null) {
                v.setTag(ID_ORIGINAL_BOUND, new Rect(v.getLeft(), v.getTop(),
                        v.getWidth() + v.getLeft(), v.getTop() + v.getHeight()));
            }
            // XXX user custom drawing order is better.
            v.bringToFront();
            doScalUp(v);
        } else {
            doScalDown(v);
        }
    }

    @Override
    public void onFocusSessionEnd(View lastFocus) {
        mConfigure.onFocusSessionEnd(lastFocus);
        
        doScalDown(lastFocus);
    }

    protected void doScalDown(View v) {
        if (DEBUG) {
            Log.d(TAG, "doScalDown. v: " + v);
        }
    }

    protected void doScalUp(View v) {
        if (DEBUG) {
            Log.d(TAG, "doScalUp. v: " + v);
        }
    }
    
    @Override
    public void addView(View child, int index, android.view.ViewGroup.LayoutParams params) {
        // TODO Auto-generated method stub
        super.addView(child, index, params);
        
        child.setOnFocusChangeListener(mListener);
    }
    
    @Override
    protected void dispatchDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.dispatchDraw(canvas);
        
        if (mConfigure.TRACK_FPS) {
            mFPS.onDraw();
        }
    }

    protected void updatePosition(View child, Rect bound) {
        if (mConfigure.DEBUG_TRANSFER_ANIMATION) {
            Log.d(TAG, "new layout params left: " + bound.left + " top: " + bound.top 
                    + " width: " + bound.width() + " height: " + bound.height());
        }
        
        updatePosition(this, child, bound);
    }
    
    public static void updatePosition(AbsoluteLayout parent, View child, Rect bound) {
        LayoutParams params = null;
        
        int width = bound.width();
        int height = bound.height();
        int x = bound.left;
        int y = bound.top;
        params = new AbsoluteLayout.LayoutParams(width, height, x, y);
        parent.updateViewLayout(child, params);
    }
}