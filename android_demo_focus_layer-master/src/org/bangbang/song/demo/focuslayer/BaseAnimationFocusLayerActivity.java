
package org.bangbang.song.demo.focuslayer;

import org.bangbang.song.focuslayer.Utils;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnGlobalFocusChangeListener;
import android.widget.RelativeLayout;

public abstract class BaseAnimationFocusLayerActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(onCreateContentRes());
        
        ViewGroup g = (ViewGroup) findViewById(R.id.content);
        LayoutParams params = new RelativeLayout.LayoutParams(android.widget.RelativeLayout.LayoutParams.FILL_PARENT,
                android.widget.RelativeLayout.LayoutParams.FILL_PARENT);

        g.addView(getFocusLayer(), params);
        
        getWindow().getDecorView().getViewTreeObserver().addOnGlobalFocusChangeListener(new OnGlobalFocusChangeListener() {
            
            @Override
            public void onGlobalFocusChanged(View oldFocus, View newFocus) {
                Utils.onFocusChange(oldFocus, false);
                Utils.onFocusChange(newFocus, true);
            }
        });
    }

    protected abstract int onCreateContentRes();

	protected abstract View getFocusLayer();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

}
