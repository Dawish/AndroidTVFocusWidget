package org.bangbang.song.focuslayer;

import org.bangbang.song.android.commonlib.ViewUtil;
import org.bangbang.song.demo.focuslayer.R;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.View;

public class AnimationConfigure {
    private static final String TAG = AnimationConfigure.class.getSimpleName();
    private static final int DEFAULT_ANIMATION_DURATION = 322;
    private static final float DEFAULT_SCALE_FACOTR = 1.3f;

    public static final boolean DEBUG = true;
    public static final boolean DRAW_GRIG = false && DEBUG;
    public static final boolean DEBUG_TRANSFER_ANIMATION = true && DEBUG;
    public static final boolean DEBUG_SCALE_ANIMATION = true && DEBUG;
    public static final boolean TRACK_FPS = true && DEBUG;
    
    public Rect mTmpRect = new Rect();
    
    public Rect mLastFocusRect = new Rect();
    public Rect mLastScaledFocusRect = new Rect();
    public Bitmap mLastFocusBitmap = null;
    
    public Rect mCurrentFocusRect = new Rect();
    public Rect mCurrentScaledFocusRect = new Rect();
    public Bitmap mCurrentFocusBitmap = null;
    
    public Matrix mMatrix;
    public RectF mTmpRectF;
    public int mFocusDrawable;
    
    /** in millisec */
    public int mDuration;
    public float mScaleFactor;
    public boolean mDisableScaleAnimation;
    public boolean mDisableAutoGenBitmap;
    private boolean mFirstFocus = true;
    
    public AnimationConfigure() {
        mDuration = DEFAULT_ANIMATION_DURATION;
        mScaleFactor = DEFAULT_SCALE_FACOTR;
        mDisableScaleAnimation = false;
        mDisableAutoGenBitmap = true;
        mMatrix = new Matrix();
        
        mFocusDrawable = R.drawable.search_button_hover;
    }
    
    public void setDuration(int duration) {
        mDuration = duration;
    }

    public int getDration() {
        return mDuration;
    }

    public void setScaleFactor(float factor) {
        mScaleFactor = factor;
    }

    public float getScaleFactor() {
        return mScaleFactor;
    }
    
    protected void onNewFocus(View layer, View focus) {
        if (null == focus) {
            return;
        }

        focus.getFocusedRect(mTmpRect);
        // focus.getDrawingRect(mTmpRect);
        ViewUtil.offsetRectBetween(focus, layer, mTmpRect);

        mLastFocusRect = new Rect(mCurrentFocusRect);
        mCurrentFocusRect = new Rect(mTmpRect);

        int width = focus.getWidth();
        int height = focus.getHeight();
        if (width <= 0 || height <= 0) {
            Log.w(TAG, "invalid w or h. w: " + width + " h: " + height);
            return;
        }

        // calculate focus rect.
        if (!mDisableScaleAnimation) {
            mMatrix.reset();
            // adjust rect by scale factor.
            mTmpRectF = new RectF(mCurrentFocusRect.left, mCurrentFocusRect.top,
                    mCurrentFocusRect.right, mCurrentFocusRect.bottom);
            mMatrix.setScale(mScaleFactor, mScaleFactor, mTmpRectF.centerX(), mTmpRectF.centerY());
            mMatrix.mapRect(mTmpRectF);
            mCurrentScaledFocusRect = new Rect((int) mTmpRectF.left, (int) mTmpRectF.top,
                    (int) mTmpRectF.right, (int) mTmpRectF.bottom);

            mMatrix.reset();
            mTmpRectF = new RectF(mLastFocusRect.left, mLastFocusRect.top,
                    mLastFocusRect.right, mLastFocusRect.bottom);
            mMatrix.setScale(mScaleFactor, mScaleFactor, mTmpRectF.centerX(),
                    mTmpRectF.centerY());
            mMatrix.mapRect(mTmpRectF);
            mLastScaledFocusRect = new Rect((int) mTmpRectF.left,
                    (int) mTmpRectF.top, (int) mTmpRectF.right,
                    (int) mTmpRectF.bottom);
        } else {
            mLastScaledFocusRect = new Rect(mLastFocusRect);
            mCurrentScaledFocusRect = new Rect(mCurrentFocusRect);
        }
        dumpRect(focus);
        
        if (mFirstFocus) {
            mFirstFocus = false;
            initFocusTarget();
        }

        updateBitmap(focus);
    }

    private void dumpRect(View focus) {
        if (DEBUG) { 
            Log.d(TAG, "updateFocusView(). view: " + focus);
            Log.d(TAG, "mCurrentFocusRect: " + mCurrentFocusRect);
            Log.d(TAG, "mCurrentScaledFocusRect: " + mCurrentScaledFocusRect);
            Log.d(TAG, "mLastFocusRect: " + mLastFocusRect);
            Log.d(TAG, "mLastScaledFocusRect: " + mLastScaledFocusRect);
        }
    }

    private void updateBitmap(View focus) {
        // calculate bitmap
        if (!mDisableScaleAnimation && !mDisableAutoGenBitmap) {
            // recycle unused bitmap.
            // do we need this??? GC it automatically??? 
            if (null != mLastFocusBitmap) {
                mLastFocusBitmap.recycle();
            }
            
            mLastFocusBitmap = mCurrentFocusBitmap;
            if (null != focus) {
                mCurrentFocusBitmap = getBitmap(focus);
            }
        }
    }
    
    protected void onFocusSessionEnd(View lastFocus) {
        if (DEBUG) {
            Log.d(TAG, "onFocusSessionEnd.");
        }
        mLastFocusRect = new Rect(mCurrentFocusRect);
        mLastScaledFocusRect = new Rect(mCurrentScaledFocusRect);
        updateBitmap(null);
        
        mCurrentFocusRect.setEmpty();
        mCurrentScaledFocusRect.setEmpty();

        dumpRect(lastFocus);
    }
    
    
    private Bitmap getBitmap(View focus) {
        int oldWidth = focus.getWidth();
        int oldHeight = focus.getHeight();

        Bitmap bitmap = Bitmap.createBitmap(oldWidth, oldHeight, Bitmap.Config.ARGB_8888);
        focus.draw(new Canvas(bitmap));
        // bitmap = Bitmap.createScaledBitmap(bitmap,
        // mCurrentScaledFocusRect.width(), mCurrentScaledFocusRect.height(),
        // false);

        // return bitmap;

        int w = mCurrentScaledFocusRect.width();
        int h = mCurrentScaledFocusRect.height();
        Log.d(TAG, "focus bitmap W: " + w + " H: " + h);
        return ViewUtil.getBitmap(focus, w, h);
    }
    
    private void initFocusTarget() {
        // no animation in firstly focus on.
        mLastFocusRect = new Rect(mCurrentFocusRect);
        mLastScaledFocusRect = new Rect(mCurrentScaledFocusRect);
        //        mLastFocusRect.setEmpty();
        //        mLastScaledFocusRect.setEmpty();

        Log.d(TAG, "first focus: mCurrentFocusRect: " + mCurrentFocusRect);
        Log.d(TAG, "first focus: mCurrentScaledFocusRect: " + mCurrentScaledFocusRect);
        Log.d(TAG, "first focus: mLastFocusRect: " + mLastFocusRect);
        Log.d(TAG, "first focus: mLastScaledFocusRect: " + mLastScaledFocusRect);
    }
    
}
