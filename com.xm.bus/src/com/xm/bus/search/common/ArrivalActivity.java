package com.xm.bus.search.common;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import com.xm.bus.common.MyApplication;
import com.xm.bus.search.self.ExitApplication;
import com.xm.bus.search.self.MyAdapter;
import com.xm.bus.search.self.RefreshListView;
import com.xm.bus.search.self.RefreshListView.OnRefreshListener;
import com.xm.bus.search.self.RemindDialog;
import com.xm.bus.search.utils.HtmlBaseParse;
import com.xm.bus.search.utils.HtmlBaseParse.STATE;

import com.xm.bus.R;


public class ArrivalActivity extends Activity {

	private MyAdapter adapter;
	private RefreshListView listView;
	private SearchApp myApp;
	private STATE state = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.arriva_content);
		ExitApplication.getInstance().addActivity(this);
		myApp = (SearchApp) getApplication();

		adapter = new MyAdapter(this);
		listView = (RefreshListView) findViewById(R.id.arrival_content);

		listView.setAdapter(adapter);
		listView.setonRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				new AsyncTask<Integer, STATE, STATE>() {
					@Override
					protected STATE doInBackground(Integer... params) {
						if (state == null) {
							state = HtmlBaseParse.getArrivalInfo(myApp.getUrl());
						}
						return state;
					}

					protected void onPostExecute(STATE result) {
						if (result == STATE.Success) {
							adapter.notifyDataSetChanged();
						}
						listView.onRefreshComplete();
						state = null;
					};
				}.execute(1);

			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, R.id.action_quit, 1, "退出");
		menu.add(1, 2, 2, R.string.action_remind);
		menu.getItem(0).setOnMenuItemClickListener(
				new OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						RemindDialog.getInstance(ArrivalActivity.this).show(
								"提醒", "你确定要退出吗?", true, true);
						return true;
					}
				});
		menu.getItem(1).setOnMenuItemClickListener(
				new OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						RemindDialog.getInstance(ArrivalActivity.this).show(
								"提醒", "下拉刷新", false, false);
						return true;
					}
				});
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void onBackPressed() {
		finish();
	}
}