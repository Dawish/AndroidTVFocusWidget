package org.bangbang.support.v4.demo;

import org.bangbang.android.support.v4.R;
import org.bangbang.support.v4.widget.GridView;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

public class SmoothScrollGridViewDemo extends Activity {
    private static final String TAG = SmoothScrollGridViewDemo.class.getSimpleName();
    private GridView mGridView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_smooth_scroll_gridview);
        
        mGridView = (GridView)findViewById(R.id.gridview);
        
        mGridView.setAdapter(new SimpleDataAdapter(this) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // TODO Auto-generated method stub
                View v = super.getView(position, convertView, parent);
                v.setBackgroundResource(android.R.drawable.btn_default);
                return v;
            }
        });
        mGridView.setSmoothScrollWhenTrakBall(true);
    }
}
