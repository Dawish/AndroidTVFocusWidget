
package custom.droid.layout;

import android.app.Activity;
import android.os.Bundle;
import custom.droid.R;

public class LayoutDemo extends Activity {

    private static final String TAG = "LayoutDemo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout_demo);
    }
}
