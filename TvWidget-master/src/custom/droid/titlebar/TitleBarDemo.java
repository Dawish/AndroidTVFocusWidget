
package custom.droid.titlebar;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import custom.droid.ICustomInterface;
import custom.droid.R;

public class TitleBarDemo extends Activity implements ICustomInterface {

    private TextView mSummaryTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_titlebar_demo);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.layout_titlebar_with_summary);

        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshView();
    }

    @Override
    public void onClick(View view) {
        // FIXME
    }

    @Override
    public void initView() {
        mSummaryTv = (TextView) findViewById(R.id.summary);
    }

    @Override
    public void refreshView() {
        updateTitleBar();
    }

    private void updateTitleBar() {
        // mSummaryTv.setText(R.string.titlebar_summary);
    }
}
