package org.bangbang.song.demo.focuslayer;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ClipChildrenActivity extends Activity {
    private static final String TAG = ClipChildrenActivity.class.getSimpleName();
    private ViewPager mViewPager;
    
    public class MyView extends TextView {
    
        private Paint mPaint;
    
    
        public MyView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            init();
        }
    
        public MyView(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }
    
        private void init() {
            mPaint = new Paint();
            mPaint.setColor(Color.RED);
        }
    
        public MyView(Context context) {
            super(context);
            init();
        }
        
        
        @Override
        public void draw(Canvas canvas) {
            // TODO Auto-generated method stub
            super.draw(canvas);
    
            canvas.drawText("1234567890", 40, 40, mPaint);
            canvas.drawText("1234567890", 0, 500, mPaint);
            RectF r = new RectF(8f, 8f, 16f, 600f);
            canvas.drawRect(r, mPaint);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_clipchildren);
        
        mViewPager = (ViewPager)findViewById(R.id.viewpager);
        mViewPager.setAdapter(new PagerAdapter() {
            
            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {
                // TODO Auto-generated method stub
                return arg0 == arg1;
            }
            
            @Override
            public int getCount() {
                return 3;
            }
            
            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                super.destroyItem(container, position, object);
                
                container.removeView((View) object);
            }
            
            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                // TODO Auto-generated method stub
                
                View view = new MyView(getApplicationContext());
                container.addView(view);
                
                return view;
            }
        });
        mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
            
            @Override
            public void onPageSelected(int arg0) {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

                getWindow().getDecorView().invalidate();
            }
            
            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO Auto-generated method stub
                
            }
        });
        
        ViewGroup p = mViewPager;
        p.setClipChildren(false);
        p.setClipToPadding(false);
        
        p = ((ViewGroup)mViewPager.getParent());
        p.setClipChildren(false);
        p.setClipToPadding(false);
    }
}
