
package custom.droid.indicator;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

public class TitleIndicator extends ViewGroup {

    public TitleIndicator(Context context) {
        this(context, null);
    }

    public TitleIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TitleIndicator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }
}
