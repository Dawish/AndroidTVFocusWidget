
package custom.droid.market;

import java.util.HashMap;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import custom.droid.R;

public class MarketActivity extends Activity {

    private ViewPager mContentViewPager;

    private PagerTabStrip mTitle;

    private HashMap<String, View> mView = new HashMap<>(3);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market_demo);

        findView();
    }

    private void findView() {
        mView.put("page1", LayoutInflater.from(getApplication()).inflate(R.layout.layout_page_1, null));
        mView.put("page2", LayoutInflater.from(getApplication()).inflate(R.layout.layout_page_2, null));
        mView.put("page3", LayoutInflater.from(getApplication()).inflate(R.layout.layout_page_3, null));

        mTitle = (PagerTabStrip) findViewById(R.id.title);
        mContentViewPager = (ViewPager) findViewById(R.id.content);
        mContentViewPager.setAdapter(new PagerAdapter() {

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public int getCount() {
                return mView.size();
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                return mView.values().toArray()[position];
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) mView.values().toArray()[position]);
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return (CharSequence) mView.keySet().toArray()[position];
            }
        });
    }

}
