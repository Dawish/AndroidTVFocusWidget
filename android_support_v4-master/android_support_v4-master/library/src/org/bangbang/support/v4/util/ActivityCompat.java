package org.bangbang.support.v4.util;

import android.app.Activity;
import android.view.KeyEvent;

/**
 * this is a supplement to {@link android.support.v4.app.ActivityCompat}, if
 * android support has add such stuffing, we will stop supporting it.
 * 
 * @author bysong
 */
public class ActivityCompat {
    private static final String TAG = ActivityCompat.class.getSimpleName();
    
    public static void call_super_onKeyShortcut(final Activity activity, int keyCode, KeyEvent event) {
    }
}
