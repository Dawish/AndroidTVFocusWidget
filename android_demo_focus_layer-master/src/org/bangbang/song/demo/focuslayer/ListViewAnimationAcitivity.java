package org.bangbang.song.demo.focuslayer;

import org.bangbang.song.focuslayer.BaseFocusLayout;
import org.bangbang.song.focuslayer.animation.AnimationFocusLayout;
import org.bangbang.song.focuslayer.animator.ScaleAnimatorFocusLayout;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @author bysong
 * @see {@link ViewGroup#setClipChildren(boolean)}
 * @see {@link ViewGroup#setClipToPadding(boolean)}
 *
 */
public class ListViewAnimationAcitivity extends Activity {
    private static final String TAG = ListViewAnimationAcitivity.class.getSimpleName();
    private ListView mListView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_listview);
        mListView = (ListView)findViewById(R.id.listView);
        mListView.setAdapter(new Adapter(this));
        
        mListView.setClipChildren(false);
        mListView.setClipToPadding(false);
    }
    
    class Adapter extends SimpleDataAdapter {

        public Adapter(Context context) {
            super(context);
        }
        
        
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Log.d(TAG, "getView. position: " + position 
                    + " convertView: " + convertView);
//            return super.getView(position, convertView, parent);
            
            if (convertView == null) {
                convertView = View.inflate(getApplicationContext(), R.layout.listview_item, null);
            }
            TextView title = ((TextView)convertView.findViewById(R.id.textView));
            title.setText(getItem(position));
            AbsoluteLayout g = (AbsoluteLayout) convertView.findViewById(R.id.container);
            // restore original x,y,w,h
            if (title.getTag(BaseFocusLayout.ID_ORIGINAL_BOUND) != null) {
//                Log.d(TAG, "restore original position.");
                Rect r = (Rect) title.getTag(BaseFocusLayout.ID_ORIGINAL_BOUND);
                BaseFocusLayout.updatePosition(g, title, r);
            }
            
            return convertView;
        }
    }
    
    public static class MyListView extends ListView {

        public MyListView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            // TODO Auto-generated constructor stub
        }

        public MyListView(Context context, AttributeSet attrs) {
            super(context, attrs);
            
//            setSmoothScrollbarEnabled(enabled)
            setChildrenDrawingOrderEnabled(true);
        }

        public MyListView(Context context) {
            super(context);
            // TODO Auto-generated constructor stub
        }
        
        @Override
        protected int getChildDrawingOrder(int childCount, int i) {
            // TODO Auto-generated method stub
            int order = super.getChildDrawingOrder(childCount, i);
            
            int index = getSelectedItemPosition() - getFirstVisiblePosition();
            if (0 <= index && index < childCount) {
                if (i == childCount - 1) {
                    order =index;
                } else if (i >= index) {
                    order = i + 1;
                }
            }
//            Log.d(TAG, "getChildDrawingOrder. childCount: " + childCount + " i: " + i 
//                    + " order: " + order + " index: " + index);
            return order;
        }
        
        @Override
        protected void layoutChildren() {
            // TODO Auto-generated method stub
            super.layoutChildren();
            
            Log.d(TAG, "layoutChildren");
        }
        
    }
    
    public static class MyLayout extends 
//    AnimationFocusLayout 
    ScaleAnimatorFocusLayout
    {

        public MyLayout(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            // TODO Auto-generated constructor stub
        }

        public MyLayout(Context context, AttributeSet attrs) {
            super(context, attrs);
            
        }

        public MyLayout(Context context) {
            super(context);
            // TODO Auto-generated constructor stub
        }
        
        @Override
        public void setSelected(boolean selected) {
            // TODO Auto-generated method stub
            super.setSelected(selected);
            Log.d(TAG, "setSelected. selected: " + selected);
            
            if (selected) {
                onFocusChange(getChildAt(0), selected);
            } else {
//                View v = new View(getContext());
//                onFocusChange(v, true);
                onFocusSessionEnd(getChildAt(0));
            }

//          onFocusChange(getChildAt(0), true);
        }
            
        @Override
        public void dispatchSetSelected(boolean selected) {
            // TODO Auto-generated method stub
            super.dispatchSetSelected(selected);
        }
        
    }
    
    public static class MyButton extends Button {

        private Paint mPaint;


        public MyButton(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            // TODO Auto-generated constructor stub
        }

        public MyButton(Context context, AttributeSet attrs) {
            super(context, attrs);
            mPaint = new Paint();
            mPaint.setColor(Color.RED);
        }

        public MyButton(Context context) {
            super(context);
            // TODO Auto-generated constructor stub
        }
        
        @Override
        public void setTranslationX(float translationX) {
            // TODO Auto-generated method stub
            super.setTranslationX(translationX);
        }
        
        @Override
        public void draw(Canvas canvas) {
            // TODO Auto-generated method stub
            super.draw(canvas);
            
            if (isSelected())  {
                RectF r = new RectF(8, 8, this.getWidth(), this.getHeight());
//                canvas.drawRect(r, mPaint);
            }
        }
    }
}
