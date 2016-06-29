package org.evilbinary.tv.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by danxingxi on 6/18/15.
 */
public class FocusInterceptLinearLayout extends LinearLayout {

    public interface OnFocusSearchListener {
        public View onFocusSearch(View focused, int direction);
    }

    public interface OnChildFocusListener {
        public boolean onRequestFocusInDescendants(int direction,
                                                   Rect previouslyFocusedRect);
        public void onRequestChildFocus(View child, View focused);
    }


    private OnFocusSearchListener mListener;
    private OnChildFocusListener mOnChildFocusListener;

    public void setOnFocusSearchListener(OnFocusSearchListener listener) {
        mListener = listener;
    }

    public OnFocusSearchListener getOnFocusSearchListener() {
        return mListener;
    }

    public void setOnChildFocusListener(OnChildFocusListener listener) {
        mOnChildFocusListener = listener;
    }

    public OnChildFocusListener getOnChildFocusListener() {
        return mOnChildFocusListener;
    }


    public FocusInterceptLinearLayout(Context context) {
        super(context);
    }

    public FocusInterceptLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FocusInterceptLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected boolean onRequestFocusInDescendants(int direction,
                                                  Rect previouslyFocusedRect) {
        if (mOnChildFocusListener != null) {
            return mOnChildFocusListener.onRequestFocusInDescendants(direction,
                    previouslyFocusedRect);
        }
        return super.onRequestFocusInDescendants(direction, previouslyFocusedRect);
    }

    @Override
    public View focusSearch(View focused, int direction) {
        if (mListener != null) {
            View view = mListener.onFocusSearch(focused, direction);
            if (view != null) {
                return view;
            }
        }
        return super.focusSearch(focused, direction);
    }

    @Override
    public void requestChildFocus(View child, View focused) {
        super.requestChildFocus(child, focused);
        if (mOnChildFocusListener != null) {
            mOnChildFocusListener.onRequestChildFocus(child, focused);
        }
    }
}
