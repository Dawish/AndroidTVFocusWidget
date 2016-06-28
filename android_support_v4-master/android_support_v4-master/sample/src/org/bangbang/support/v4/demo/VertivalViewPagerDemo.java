package org.bangbang.support.v4.demo;

import org.bangbang.android.support.v4.R;
import org.bangbang.support.v4.widget.VerticalViewPager;
import org.bangbang.support.v4.widget.ViewPager;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

public class VertivalViewPagerDemo extends Activity {
	private VerticalViewPager mVerticalViewpager;
	private ViewPager mViewPager;
	private ListAdapter mAdapter;

	private PagerAdapter mPagerAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vertical_viewpager);

		mVerticalViewpager = (VerticalViewPager) findViewById(R.id.vertical_viewpager);
		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		mViewPager.setOffscreenPageLimit(1);



		mAdapter = new SimpleDataAdapter(this){
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View v =  super.getView(position, convertView, parent);
//				v.setBackgroundColor(position % 2 == 0 ? Color.YELLOW : Color.GREEN);
				return v;
			}
		};
		mPagerAdapter = new PagerAdapter() {

			@Override
			public Object instantiateItem(View container, int position) {
				return addView((ViewGroup) container, position);
			}

			@Override
			public Object instantiateItem(ViewGroup container, int position) {
				return addView(container, position);
			}

			private Object addView(ViewGroup container, int position) {
				View v = mAdapter.getView(position, null, null);
				container.addView(v);

				return v;
			}

			@Override
			public void destroyItem(View container, int position, Object object) {

				removeView((ViewGroup) container, (View) object);
//				super.destroyItem(container, position, object);
			}

			@Override
			public void destroyItem(ViewGroup container, int position,
					Object object) {

				removeView(container, (View) object);
				super.destroyItem(container, position, object);
			}

			private void removeView(ViewGroup container, View view) {
				container.removeView(view);
			}

			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}

			@Override
			public int getCount() {
				return mAdapter.getCount();
			}
		};
		mVerticalViewpager.setAdapter(mPagerAdapter);
		mViewPager.setAdapter(mPagerAdapter);
	}

	public void onUp(View v) {
		if (v.getId() == R.id.up) {
			mViewPager.arrowScroll(View.FOCUS_RIGHT);
		} else {
			mVerticalViewpager.arrowScroll(View.FOCUS_UP);
		}
	}

	public void onDown(View v) {
		if (v.getId() == R.id.down) {
			mViewPager.arrowScroll(View.FOCUS_LEFT);
		} else {
			mVerticalViewpager.arrowScroll(View.FOCUS_DOWN);
		}
	}

}
