package org.bangbang.song.focuslayer;

import org.bangbang.song.demo.focuslayer.R;

import android.view.View;


public class Utils {
    public static final int FOCUS_LAYER_ID = R.id.menu_settings;
	private static final String TAG = Utils.class.getSimpleName();
    
    public static void onFocusChange(View v, boolean hasFocus) {
        if (null == v) {
            return;
        }
        
        IFocusAnimationLayer layper = (IFocusAnimationLayer) v.getRootView().findViewById(FOCUS_LAYER_ID);
        layper.onFocusChange(v, hasFocus);
    }
}
