package org.bangbang.support.v4.demo;

import android.os.Bundle;

public class SmoothScrollListViewDemo extends CenterListViewDemo {
    private static final String TAG = SmoothScrollListViewDemo.class.getSimpleName();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        
        mListView.setSmoothScrollWhenTrakBall(true);
    }
}
