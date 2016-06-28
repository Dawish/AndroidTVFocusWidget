
package org.bangbang.song.demo.focuslayer;

import org.bangbang.song.focuslayer.animation.AnimationFocusLayer;

import android.os.Bundle;
import android.view.View;

public class AnimationFocusLayerActivity extends BaseAnimationFocusLayerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected View getFocusLayer() {
        return new AnimationFocusLayer(this);
    }
    
	@Override
	protected int onCreateContentRes() {
		return R.layout.activity_animationfocuslayer;
	}

}
