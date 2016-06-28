
package org.bangbang.song.focuslayer;

import org.bangbang.song.android.commonlib.FPSLoger;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;

/**
 * @author bysong
 */
@SuppressLint("NewApi")
public class FastFocusLayer extends SurfaceView implements IFocusAnimationLayer, Callback {
    private static final String TAG = FastFocusLayer.class.getSimpleName();
    private static final boolean DEBUG = true;

    private WorkThread mWorker;
    private Handler mHandler;

    private SurfaceHolder mHolder;

    private AnimationConfigure mConfigure;
    private FPSLoger mFps;
    private Paint mPaint;
    private Canvas mCanvas = null;

    private boolean mAnimationEnd;
    private ValueAnimator mTransferAnimation;
    private AnimatableRect mTransalteRect;
    private AnimatableRect mScaleUpRect;
    private AnimatableRect mScaleDownRect;
    private Rect mTmp;
    private boolean mRequestAnimation;
    private Drawable mTranslateDrawable;

    public FastFocusLayer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init();
    }

    public FastFocusLayer(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public FastFocusLayer(Context context) {
        super(context);

        init();
    }

    void init() {
        setId(Utils.FOCUS_LAYER_ID);
        mConfigure = new AnimationConfigure();
        mConfigure.mDuration = 100;

        mHolder = getHolder();
        // for show what we draw on content.
        mHolder.setFormat(PixelFormat.TRANSLUCENT);
        setZOrderOnTop(true);

        getHolder().addCallback(this);

        mPaint = new Paint();
        mPaint.setColor(Color.RED);
        mPaint.setTextSize(20);
        Typeface font = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD);
        mPaint.setTypeface(font);
        mFps = new FPSLoger(TAG);

        mTransalteRect = new AnimatableRect();
        mScaleUpRect = new AnimatableRect();
        mScaleDownRect = new AnimatableRect();
        mTranslateDrawable = getResources().getDrawable(mConfigure.mFocusDrawable);
        mWorker = new WorkThread();
        mWorker.start();
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        mConfigure.onNewFocus(this, v);

        mAnimationEnd = false;
        mRequestAnimation = true;
        requestDrawFrame();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "surfaceCreated");
        mWorker.startOrPause(false);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(TAG, "surfaceChanged");
        mWorker.startOrPause(false);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "surfaceDestroyed");
        mWorker.startOrPause(true);
    }

    @Override
    protected void onAttachedToWindow() {
        // TODO Auto-generated method stub
        super.onAttachedToWindow();
        // mWorker.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        // TODO Auto-generated method stub
        super.onDetachedFromWindow();
    }

    private void requestDrawFrame() {
        if (null != mHandler) {
            mHandler.removeMessages(0);
            mHandler.sendEmptyMessage(0);
        } else {
            Log.w(TAG, "mHandler is null!!! ignore.");
        }
    }

    /**
     * all states(bitmap, rect... and so on) are ready.
     * 
     * @param canvas
     */
    @SuppressLint("WrongCall")
    public void onDrawFrame(Canvas canvas) {
//        Log.d(TAG, "onDrawFrame()");
        if (mConfigure.TRACK_FPS) {
            mFps.onDraw();
            mFps.onDrawStart();
        }
        
        canvas.drawColor(Color.BLACK, PorterDuff.Mode.CLEAR);
        canvas.drawColor(Color.TRANSPARENT);

        mTmp = mTransalteRect.toRect();
        if (DEBUG) {
        	Log.d(TAG, "drawRect. rect: " + mTmp);
        }
        mTranslateDrawable.setBounds(mTmp);
        mTranslateDrawable.draw(canvas);

        if (mConfigure.mCurrentFocusBitmap != null) {
            mTmp = mScaleUpRect.toRect();
            if (DEBUG) {
            	Log.d(TAG, "draw current bitmap. rect: " + mTmp);
            }
            mCanvas.drawBitmap(mConfigure.mCurrentFocusBitmap, 
                    null, mTmp, mPaint);
        }
        if (mConfigure.mLastFocusBitmap != null) {
            mTmp = mScaleDownRect.toRect();
            if (DEBUG) {
            	Log.d(TAG, "draw last bitmap. rect: " + mTmp);
            }
            mCanvas.drawBitmap(mConfigure.mLastFocusBitmap, 
                    null, mTmp, mPaint);
        }
        
        if (mConfigure.TRACK_FPS) {
            mFps.onDrawEnd();
        }
    }

    @Override
    public void onFocusSessionEnd(View lastFocus) {
        mConfigure.onFocusSessionEnd(lastFocus);
    }

    public class MyHandler extends Handler {
        public void handleMessage(Message msg) {
            try {
                mCanvas = mHolder.lockCanvas();
                if (null != mCanvas) {
                    // if (null != mTransferAnimation) {
                    // Log.d(TAG, "running: " + mTransferAnimation.isRunning() +
                    // " started: " + mTransferAnimation.isStarted());
                    // }
//                    Log.d(TAG, "mAnimationEnd: " + mAnimationEnd);
                    if (mRequestAnimation) {
                        mRequestAnimation = false;
                        startAnimation();
                    } else if (mTransferAnimation.isRunning()) {
                    }

                    onDrawFrame(mCanvas);
                }
            } finally {
                if (null != mCanvas) {
                    mHolder.unlockCanvasAndPost(mCanvas);
                }
            }
        }

        private void startAnimation() {
            mTransferAnimation = ValueAnimator.ofObject(new AnimatableEvaluator(),
                    new AnimatableRect[] {
                            AnimatableRect.fromRect(mConfigure.mLastScaledFocusRect),
                            AnimatableRect.fromRect(mConfigure.mCurrentScaledFocusRect)
                    });
            mTransferAnimation.addUpdateListener(new AnimatorUpdateListener() {

                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mTransalteRect = (AnimatableRect) animation.getAnimatedValue();
                    // Log.d(TAG, "onAnimationUpdate. rect: " + mTransalteRect);
                    requestDrawFrame();
                }
            });
            mTransferAnimation.addListener(new AnimatorListener() {

                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    Log.d(TAG, "onAnimationEnd");
                    mAnimationEnd = true;
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }
            });

//            mTransferAnimation.setRepeatCount(0);
//            mTransferAnimation.setDuration(mConfigure.mDuration);
//            mTransferAnimation.start();
            
            // scale up animation
            Animator top = ObjectAnimator.ofInt(mScaleUpRect, "top", 
                    new int[] {mConfigure.mCurrentFocusRect.top, mConfigure.mCurrentScaledFocusRect.top});

            Animator left = ObjectAnimator.ofInt(mScaleUpRect, "left", 
                    new int[] {mConfigure.mCurrentFocusRect.left, mConfigure.mCurrentScaledFocusRect.left});

            Animator right = ObjectAnimator.ofInt(mScaleUpRect, "right", 
                    new int[] {mConfigure.mCurrentFocusRect.right, mConfigure.mCurrentScaledFocusRect.right});

            Animator bottom = ObjectAnimator.ofInt(mScaleUpRect, "bottom", 
                    new int[] {mConfigure.mCurrentFocusRect.bottom, mConfigure.mCurrentScaledFocusRect.bottom});
            
            // scale down animation.
            Animator pHoloder = ObjectAnimator.ofPropertyValuesHolder(mScaleDownRect, new PropertyValuesHolder[] {
                    PropertyValuesHolder.ofInt("top", new int[] {mConfigure.mLastScaledFocusRect.top, 
                            mConfigure.mLastFocusRect.top}),
                    PropertyValuesHolder.ofInt("left", new int[] {mConfigure.mLastScaledFocusRect.left, 
                            mConfigure.mLastFocusRect.left}),
                    PropertyValuesHolder.ofInt("right", new int[] {mConfigure.mLastScaledFocusRect.right, 
                            mConfigure.mLastFocusRect.right}),
                   PropertyValuesHolder.ofInt("bottom", new int[] {mConfigure.mLastScaledFocusRect.bottom, 
                           mConfigure.mLastFocusRect.bottom}),
            });
            
            AnimatorSet set = new AnimatorSet();
            set.setDuration(mConfigure.mDuration);
            set.playTogether(new Animator[] {mTransferAnimation, left, top, right, bottom, pHoloder});
            set.start();
            Log.d(TAG, "start animtion");
        }
    }

    class WorkThread extends Thread {
        private boolean mPaused = false;

        public WorkThread() {
            super(TAG + ":workthread");
        }

        public void startOrPause(boolean pause) {
            mPaused = pause;
        }

        @Override
        public void run() {
            super.run();

            Looper.prepare();
            mHandler = new MyHandler();
            Looper.loop();

        }
    }

    public static class AnimatableRect {
        private static final boolean DEBUG = false;
        public int left, top;
        public int right, bottom;

        public AnimatableRect() {
            this(0, 0, 0, 0);
        }

        public AnimatableRect(int left, int top, int right, int bottom) {
            this.left = left;
            this.top = top;
            this.right = right;
            this.bottom = bottom;
        }

        public static AnimatableRect fromRect(Rect r) {
            AnimatableRect rect = new AnimatableRect(r.left, r.top, r.right, r.bottom);
            return rect;
        }
        
        public Rect toRect() {
            Rect r = new Rect();
            r.left = this.left;
            r.top = this.top;
            r.right = this.right;
            r.bottom = this.bottom;
            
            return r;
        }

        public int getLeft() {
            return left;
        }

        public void setLeft(int left) {
            this.left = left;
            if (DEBUG) {
                Log.d(TAG, "setLeft. left: " + left);
            }
        }

        public int getTop() {
            return top;
        }

        public void setTop(int top) {
            this.top = top;
            if (DEBUG) {
                Log.d(TAG, "setTop. top: " + top);
            }
        }

        public int getRight() {
            return right;
        }

        public void setRight(int right) {
            this.right = right;
            if (DEBUG) {
                Log.d(TAG, "setRight. right: " + right);
            }
        }

        public int getBottom() {
            return bottom;
        }

        public void setBottom(int bottom) {
            this.bottom = bottom;
            if (DEBUG) {
                Log.d(TAG, "setBottom. bottom: " + bottom);
            }
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder(32);
            sb.append("MyRect(");
            sb.append(left);
            sb.append(", ");
            sb.append(top);
            sb.append(" - ");
            sb.append(right);
            sb.append(", ");
            sb.append(bottom);
            sb.append(")");
            return sb.toString();
        }
    }

    public static class AnimatableEvaluator implements TypeEvaluator<AnimatableRect> {

        @Override
        public AnimatableRect evaluate(float fraction, AnimatableRect startValue,
                AnimatableRect endValue) {
            return new AnimatableRect(startValue.left
                    + (int) ((endValue.left - startValue.left) * fraction),
                    startValue.top + (int) ((endValue.top - startValue.top) * fraction),
                    startValue.right + (int) ((endValue.right - startValue.right) * fraction),
                    startValue.bottom + (int) ((endValue.bottom - startValue.bottom) * fraction));
        }
    }
}
