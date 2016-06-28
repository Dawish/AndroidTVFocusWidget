
package custom.droid.widget;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import custom.droid.R;

public class ProgressBarDemo extends Activity {

    private CustomProgressBar mCircleProgressBar;

    private Button mButton;

    private int mProcess = 0;

    private Handler mHandler = new Handler() {

        public void handleMessage(android.os.Message msg) {
            mProcess += 1;
            if (mProcess <= 160) {
                mCircleProgressBar.setProgress(mProcess);
                sendEmptyMessageDelayed(0, 50);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progressbar_demo);
        initView();
    }

    private void initView() {
        mCircleProgressBar = (CustomProgressBar) findViewById(R.id.progressbar);
        mCircleProgressBar.setMax(160);
        mCircleProgressBar.setProgress(10);
        mButton = (Button) findViewById(R.id.confirm);
        mButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                mProcess = 0;
                mCircleProgressBar.setProgress(mProcess);
                mCircleProgressBar.setVisibility(View.VISIBLE);
                mHandler.sendEmptyMessageDelayed(0, 50);
            }
        });

    }
}
