
package org.bangbang.song.demo.focuslayer;

import org.bangbang.song.focuslayer.Utils;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalFocusChangeListener;

public abstract class BaseGLFocusLayerActivity extends Activity {
	private GLSurfaceView mGlView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_glfocuslayer);		
		
		mGlView = (GLSurfaceView)findViewById(Utils.FOCUS_LAYER_ID);
		onInitGLView(mGlView);
		
        getWindow().getDecorView().getViewTreeObserver().addOnGlobalFocusChangeListener(new OnGlobalFocusChangeListener() {
            
            @Override
            public void onGlobalFocusChanged(View oldFocus, View newFocus) {
                Utils.onFocusChange(oldFocus, false);
                Utils.onFocusChange(newFocus, true);
            }
        });
	}

	protected abstract void onInitGLView(GLSurfaceView view);

	@Override
	protected void onResume() {
		super.onResume();
		mGlView.onResume();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		mGlView.onPause();
	}
	
	
}
