
package custom.droid.actionbar;

import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import custom.droid.R;

public class ActionBarDemo extends Activity implements TabListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_actionbar_demo);

        ActionBar bar = getActionBar();
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        int flags = ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE;
        int change = bar.getDisplayOptions() ^ flags;
        bar.setDisplayOptions(change, flags);

        View v = getLayoutInflater().inflate(R.layout.layout_actionbar, null);
        LayoutParams lp = new ActionBar.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.BOTTOM | Gravity.LEFT;
        bar.setCustomView(v, lp);
        bar.getCustomView().requestFocus();

        addTab();
        addTab();
        addTab();
    }

    public void addTab() {
        Tab t = getActionBar().newTab();
        t.setCustomView(R.layout.layout_actionbar_tab);
        final ActionBar bar = getActionBar();
        final int tabCount = bar.getTabCount();
        final String text = "Tab " + tabCount;
        bar.addTab(t.setText(text).setTabListener(this));
    }

    public void removeTab() {
        final ActionBar bar = getActionBar();
        if (bar.getTabCount() > 0) {
            bar.removeTabAt(bar.getTabCount() - 1);
        }
    }

    @Override
    public void onTabReselected(Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabSelected(Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {

    }
}
