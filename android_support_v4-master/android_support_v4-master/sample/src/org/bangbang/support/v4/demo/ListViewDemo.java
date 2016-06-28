package org.bangbang.support.v4.demo;

import org.bangbang.android.support.v4.R;
import org.bangbang.support.v4.widget.HListView;
import org.bangbang.support.v4.widget.ListView;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListAdapter;

public class ListViewDemo extends Activity {

	protected static final String TAG = ListViewDemo.class.getSimpleName();
	private ListView mList;
	private HListView mHList;
	private ListAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_listview);
		
		mList = (ListView) findViewById(R.id.list);
		mHList = (HListView) findViewById(R.id.hlist);
		
		mAdapter = new SimpleDataAdapter(this);
		
		mList.setAdapter(mAdapter);		
		mHList.setAdapter(mAdapter);
	}

}
