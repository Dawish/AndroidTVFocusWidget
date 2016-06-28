
package custom.droid.widget;

import android.app.Activity;
import android.os.Bundle;
import custom.droid.R;

public class TextViewDemo extends Activity {

    private CustomTextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_textview_demo);
        initView();
    }

    private void initView() {
        mTextView = (CustomTextView) findViewById(R.id.textview);
        mTextView.setText(getString(R.string.text));
    }
}
