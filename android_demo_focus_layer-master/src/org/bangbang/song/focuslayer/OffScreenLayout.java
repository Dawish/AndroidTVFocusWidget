package org.bangbang.song.focuslayer;

import org.bangbang.song.demo.focuslayer.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class OffScreenLayout extends FrameLayout {
    private static final String TAG = OffScreenLayout.class.getSimpleName();
    private View mOffscreenView;
    private View mSrc;
    private View mDest;

    public OffScreenLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    public OffScreenLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public OffScreenLayout(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }
    
    @Override
    protected void onFinishInflate() {
        // TODO Auto-generated method stub
        super.onFinishInflate();
        
        mSrc = findViewById(R.id.src);
        mDest = findViewById(R.id.dest);
        mSrc.setSelected(true);
        mDest.setSelected(true);
        
    }
    
    @Override
    protected void onAttachedToWindow() {
        // TODO Auto-generated method stub
        super.onAttachedToWindow();

        ((ViewGroup)getParent().getParent()).setClipChildren(false);
        ((ViewGroup)getParent().getParent()).setClipToPadding(false);
        ((ViewGroup)getParent()).setClipChildren(false);
        ((ViewGroup)getParent()).setClipToPadding(false);
        setClipChildren(false);
        setClipToPadding(false);
    }
    
    @Override
    public void addView(View child, int index, android.view.ViewGroup.LayoutParams params) {
        // TODO Auto-generated method stub
        super.addView(child, index, params);
        if ("offscreen".equals(child.getTag())) {
            mOffscreenView = child;
        }
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // TODO Auto-generated method stub
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mOffscreenView != null) {
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(200, MeasureSpec.EXACTLY);
            heightMeasureSpec  = MeasureSpec.makeMeasureSpec(200, MeasureSpec.EXACTLY);
            mOffscreenView.measure(widthMeasureSpec, heightMeasureSpec);
        }
    }
    
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        // TODO Auto-generated method stub
        super.onLayout(changed, left, top, right, bottom);
        
        if (mOffscreenView != null) {
            mOffscreenView.layout(300, -100, mOffscreenView.getWidth() + 300, mOffscreenView.getHeight() + 300);
        }
    }
    
    @Override
    protected void dispatchDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.dispatchDraw(canvas);
        
        canvas.drawText("abcdefghijklmnopqrstuvwxyz1234567890", -150, 50, new Paint());
        
        
        Log.d(TAG, "clip bound: " + canvas.getClipBounds());
    }
    
    @Override
    protected void measureChild(View child, int parentWidthMeasureSpec, int parentHeightMeasureSpec) {
        // TODO Auto-generated method stub
        super.measureChild(child, parentWidthMeasureSpec, parentHeightMeasureSpec);
    }
    
    
    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        if (child == mOffscreenView) {
//            return false;
        }
        
        return super.drawChild(canvas, child, drawingTime);
    }
    
}
