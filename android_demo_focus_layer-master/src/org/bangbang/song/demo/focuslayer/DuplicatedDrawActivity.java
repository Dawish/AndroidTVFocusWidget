
package org.bangbang.song.demo.focuslayer;

import java.lang.ref.SoftReference;

import org.bangbang.song.android.commonlib.ViewUtil;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;

/**
 * draw view's content at another place.
 * 
 * @author bysong
 * 
 * XXX: for textview's marquee, this does not work.
 */
public class DuplicatedDrawActivity extends Activity {
    private static final String TAG = DuplicatedDrawActivity.class.getSimpleName();
    private MySource mSrc;
    private MyDestination mDest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_duplicated_draw);

        mSrc = (MySource) findViewById(R.id.src);
        mDest = (MyDestination) findViewById(R.id.dest);
        mSrc.setduplicateView(mDest);
    }

    public static class MySource extends Button {
    
        private SoftReference<View> mView = new SoftReference<View>(null);
        private boolean mDrawDuplicate;
    
        public MySource(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            // TODO Auto-generated constructor stub
        }
    
        public MySource(Context context, AttributeSet attrs) {
            super(context, attrs);
            // TODO Auto-generated constructor stub
        }
    
        public MySource(Context context) {
            super(context);
            // TODO Auto-generated constructor stub
        }
    
        @Override
        public void draw(Canvas canvas) {
            // TODO Auto-generated method stub
            super.draw(canvas);
            Log.d(TAG, "draw");
    
            if (mDrawDuplicate) { // stack overflow
                return;
            }
    
            mDrawDuplicate = true;
            drawDuplicteView();
            mDrawDuplicate = false;
        }
    
        private void drawDuplicteView() {
            View v = mView.get();
            if (null == v) {
                return;
            }
    
            Log.d(TAG, "drawDuplicteView");
            // TODO ugly duplicated drawing.
            Bitmap bitmap = ViewUtil.getBitmap(this, v.getWidth(), v.getHeight());
            v.setBackground(new BitmapDrawable(bitmap));
            v.invalidate();
        }
    
        public void setduplicateView(View view) {
            mView = new SoftReference<View>(view);
        }    
    }

    public static class MyDestination extends View {

        public MyDestination(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            // TODO Auto-generated constructor stub
        }

        public MyDestination(Context context, AttributeSet attrs) {
            super(context, attrs);
            // TODO Auto-generated constructor stub
        }

        public MyDestination(Context context) {
            super(context);
            // TODO Auto-generated constructor stub
        }

    }
}
