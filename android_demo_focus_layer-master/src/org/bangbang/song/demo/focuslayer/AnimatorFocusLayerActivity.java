
package org.bangbang.song.demo.focuslayer;

import org.bangbang.song.focuslayer.animator.AnimatorFocusLayer;

import android.os.Bundle;
import android.view.View;

public class AnimatorFocusLayerActivity extends BaseAnimationFocusLayerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected View getFocusLayer() {
        return new AnimatorFocusLayer(this);
    }
    
	@Override
	protected int onCreateContentRes() {
		return R.layout.activity_animatorfocuslayer;
	}
}
