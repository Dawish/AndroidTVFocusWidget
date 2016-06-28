
package custom.droid.ninepatch;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import custom.droid.ICustomInterface;
import custom.droid.R;

public class NinepatchDemo extends Activity implements ICustomInterface {

    private static final String TAG = "NinepatchDemo";

    private TextView mButtonWithPadding;

    private TextView mButtonWithoutPadding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ninepatch_demo);

        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshView();
    }

    @Override
    public void onClick(View view) {
    }

    @Override
    public void initView() {
        mButtonWithPadding = (TextView) findViewById(R.id.btn);
        mButtonWithoutPadding = (TextView) findViewById(R.id.btn_without_padding);
    }

    @Override
    public void refreshView() {
        Log.d(TAG,
                "mButtonWithPadding, l : " + mButtonWithPadding.getPaddingLeft() + " t : "
                        + mButtonWithPadding.getPaddingTop() + " r : " + mButtonWithPadding.getPaddingRight() + " b : "
                        + mButtonWithPadding.getPaddingBottom());

        Log.d(TAG, "mButtonWithoutPadding, l : " + mButtonWithoutPadding.getPaddingLeft() + " t : "
                + mButtonWithoutPadding.getPaddingTop() + " r : " + mButtonWithoutPadding.getPaddingRight() + " b : "
                + mButtonWithoutPadding.getPaddingBottom());
    }
}
