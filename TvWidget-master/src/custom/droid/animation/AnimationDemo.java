
package custom.droid.animation;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.FrameLayout;
import custom.droid.ICustomInterface;
import custom.droid.R;

public class AnimationDemo extends Activity implements ICustomInterface, OnFocusChangeListener {

    private FrameLayout mContainer;

    private AnimationFocusView mAnimationFocusView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation_demo);

        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshView();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.start:
                int l = (int) (Math.random() * 10 + 10);
                int t = (int) (Math.random() * 10 + 10);
                int r = (int) (l + Math.random() * 500 + 100);
                int b = (int) (t + Math.random() * 500 + 100);
                Rect rect = new Rect(l, t, r, b);
                mAnimationFocusView.startAnimation(rect);

                break;
            default:
                break;
        }
    }

    @Override
    public void initView() {
        mContainer = (FrameLayout) findViewById(R.id.container);
        mAnimationFocusView = new AnimationFocusView(this);
        mContainer.addView(mAnimationFocusView);
        findViewById(R.id.start).setOnClickListener(this);

        findViewById(R.id.start).setOnFocusChangeListener(this);
        findViewById(R.id.other).setOnFocusChangeListener(this);
    }

    @Override
    public void refreshView() {
        View v = mContainer.getFocusedChild();
        if (v != null) {
            Rect rect = new Rect(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
            mAnimationFocusView.startAnimation(rect);
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            if (v != null) {
                Rect rect = new Rect(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
                mAnimationFocusView.startAnimation(rect);
            }
        }
    }
}
