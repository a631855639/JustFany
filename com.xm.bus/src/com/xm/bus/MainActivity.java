package com.xm.bus;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.Menu;
import android.view.MenuItem;
import com.xm.bus.common.MyApplication;
import com.xm.bus.search.self.ExitApplication;
import com.xm.bus.search.self.RemindDialog;

public class MainActivity extends FragmentActivity {
	private FragmentTabHost mTabHost;
	
	@Override
	public void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		setContentView(R.layout.tabs_fragment);
		ExitApplication.getInstance().addActivity(this);
		//getWindow().setFlags(1024, 1024);
		mTabHost = ((FragmentTabHost) findViewById(android.R.id.tabhost));
		mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
		mTabHost.addTab(mTabHost.newTabSpec("查询").setIndicator("查询",
						getResources().getDrawable(R.drawable.search_user)),SearchActivity.class, null);
		mTabHost.addTab(mTabHost.newTabSpec("定位").setIndicator("定位",
						getResources().getDrawable(R.drawable.location)),LocationActivity.class, null);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu paramMenu) {
		getMenuInflater().inflate(R.menu.main, paramMenu);
		paramMenu.getItem(0).setOnMenuItemClickListener(
				new MenuItem.OnMenuItemClickListener() {
					public boolean onMenuItemClick(MenuItem paramMenuItem) {
						RemindDialog.getInstance(MainActivity.this).show("提醒","你确定要退出吗?", true, true);
						return true;
					}
				});
		paramMenu.getItem(1).setOnMenuItemClickListener(
				new MenuItem.OnMenuItemClickListener() {
					public boolean onMenuItemClick(MenuItem paramMenuItem) {
						RemindDialog.getInstance(MainActivity.this).show("关于","当前版本： 1.0 (该软件所用到的信息均来至于网络，如需使用正版，请移至www.5320000.com)",false, false);
						return true;
					}
				});
		return true;
	}
	@Override
	public void onBackPressed() {
		RemindDialog.getInstance(this).show("提醒", "你确定要退出吗?", true, true);
	}
	@Override
	protected void onDestroy() {
		MyApplication localMyApplication = (MyApplication) getApplication();
		if (localMyApplication.mBMapManager != null) {
			localMyApplication.mBMapManager.destroy();
			localMyApplication.mBMapManager = null;
		}
		this.mTabHost.destroyDrawingCache();
		this.mTabHost = null;
		super.onDestroy();
	}
}