package org.bangbang.song.demo.focuslayer;

import android.app.Activity;
import android.view.ViewGroup;

public class OffscreenLayoutActivity extends Activity {
    private static final String TAG = OffscreenLayoutActivity.class.getSimpleName();
    
    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_offscreenlayout);
        
        ((ViewGroup)getWindow().getDecorView()).setClipChildren(false);
    };
}
