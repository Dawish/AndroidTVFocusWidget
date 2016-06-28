
package org.bangbang.support.v4.demo;

import org.bangbang.android.support.v4.R;
import org.bangbang.support.v4.widget.ListView;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * place selected item in the vertical center position, if possiable.
 * 
 * @author bysong
 *
 */
public class CenterListViewDemo extends Activity {
    private static final String TAG = CenterListViewDemo.class.getSimpleName();
    protected ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_center_listview);

        mListView = (ListView) findViewById(R.id.listView);
        mListView.setAdapter(new SimpleDataAdapter(this) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                view.setBackgroundResource(android.R.drawable.btn_default);
                return view;
            }
        });
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
            mListView.onKeyDown(event.getKeyCode(), event);
        }
    }
    
    public static class CenterListView extends ListView {

        public CenterListView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
        }

        public CenterListView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public CenterListView(Context context) {
            super(context);
        }
        
        // always, you can adjust this for keep preview space for head & footer.
        // set CenterGridViewDemo
        @Override
        public int getVerticalFadingEdgeLength() {
            int length = super.getVerticalFadingEdgeLength();
            if (getChildCount() > 0) {
                length = (getHeight() - getChildAt(0).getHeight() ) / 2;
            }
            return length;
        }
        @Override
        public int getMaxScrollAmount() {
            return getHeight() / 2;
        }
        
    }
}
