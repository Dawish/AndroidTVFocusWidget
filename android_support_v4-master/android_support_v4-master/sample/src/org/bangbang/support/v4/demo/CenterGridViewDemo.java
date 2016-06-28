
package org.bangbang.support.v4.demo;

import org.bangbang.android.support.v4.R;
import org.bangbang.support.v4.widget.GridView;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * place selected item in the vertical center position, if possiable.
 * 
 * @author bysong
 *
 */
public class CenterGridViewDemo extends Activity {
    private static final String TAG = CenterGridViewDemo.class.getSimpleName();
    protected GridView mGridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_center_gridview);

        mGridView = (GridView) findViewById(R.id.gridView);
        mGridView.setAdapter(new SimpleDataAdapter(this) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                view.setBackgroundResource(android.R.drawable.btn_default);
                return view;
            }
        });
        
        mGridView.setSmoothScrollWhenTrakBall(true);
    }

    public void onClick(View v) {
        KeyEvent event = null;
        switch (v.getId()) {
            case R.id.previous:
                event = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_UP);;
                break;
            case R.id.next:
                event = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_DOWN);;
                break;
            default:
                break;
        }
        if (null != event) {
            mGridView.onKeyDown(event.getKeyCode(), event);
        }
    }
    
    public static class CenterGridView extends GridView {

        private int mCachedLenght;
		public CenterGridView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
        }

        public CenterGridView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public CenterGridView(Context context) {
            super(context);
        }
        
        @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
        	// TODO Auto-generated method stub
        	super.onLayout(changed, l, t, r, b);   
        	
        	if (getChildCount() > 0 && mCachedLenght > 0) {
        		mCachedLenght = (getHeight() - getChildAt(0).getHeight() ) / 2;
        	}
        }
        

        @Override
        public int getVerticalFadingEdgeLength() {
        	int length = super.getVerticalFadingEdgeLength();
        	if (getChildCount() > 0) {
        		length = (getHeight() - getChildAt(0).getHeight() ) / 2;
        	}
        	
            // always, you can adjust this for keep preview space for head & footer.
            // TRICK，unlike Listview, in GridView this method may be called when all child views
            // have detached (GridView call detachAllViewsFromParent()), so we 
            // will not dependent on childview's count, we must use other way 
            // to get correct result (hard-coding??? or cache it as long as 
        	 // we have one child (assume child's height is un-mutate) or other)
        	 // this is so different from ListView's behavior.
        	length = 60;
        	length = mCachedLenght;
        	
        	Log.d(TAG, "getVerticalFadingEdgeLength(). length: " + length);
        	return length;
        }
//        @Override
        public int getMaxScrollAmount() {
            return getHeight() / 2;
        }
        
    }
}
