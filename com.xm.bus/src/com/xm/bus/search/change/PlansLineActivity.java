package com.xm.bus.search.change;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.xm.bus.R;
import com.xm.bus.search.common.SearchApp;
import com.xm.bus.search.self.ExitApplication;
import com.xm.bus.search.self.LoadingDialog;
import com.xm.bus.search.self.RemindDialog;
import com.xm.bus.search.utils.HtmlBaseParse.STATE;
import com.xm.bus.search.utils.HtmlChangeParse;

import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class PlansLineActivity extends ListActivity {
	private TextView head_info;
	private ListView lv_content;
	private List<Map<String, String>> plansLineList;
	private Map<String, String> planLineMap;
	private SearchApp myApp;
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.plans_line_select);
		ExitApplication.getInstance().addActivity(this);
		myApp=(SearchApp) getApplication();
		
		Intent intent=getIntent();
		plansLineList=(List<Map<String, String>>) intent.getSerializableExtra("planLineList");
		
		head_info=(TextView) findViewById(R.id.tv_plans_line_warning);
		head_info.setText(HtmlChangeParse.titleInfo1);
		
		lv_content=(ListView) findViewById(android.R.id.list);
		SimpleAdapter adapter=new SimpleAdapter(this, plansLineList, R.layout.plans_line_select_item, new String[]{"changeLineName"}, new int[]{R.id.planLineName});
		lv_content.setAdapter(adapter);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, R.id.action_quit, 1, "退出");
		menu.getItem(0).setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				RemindDialog.getInstance(PlansLineActivity.this).show("提醒","你确定要退出吗?",true,true);
				return true;
			}
		});
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public void onBackPressed() {
		finish();
	}
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		planLineMap=new HashMap<String, String>();
		planLineMap=plansLineList.get(position);
		LoadingDialog.getInstance(this).show();
		new AsyncTask<Integer, Void, STATE>() {
			@Override
			protected STATE doInBackground(Integer... params) {
				STATE result=HtmlChangeParse.getChangeDetail(planLineMap,myApp.getFrom(),myApp.getTo());
				return result;
			}
			protected void onPostExecute(STATE result) {
				LoadingDialog.getInstance(PlansLineActivity.this).dismiss();
				if(result==STATE.ServerMaintenance){
					Toast.makeText(PlansLineActivity.this, "对不起，服务器正在维护，请稍后再试", Toast.LENGTH_SHORT).show();
				}else if(result==STATE.Success){
					Intent intent=new Intent();
					intent.putExtra("planDetaiList", (Serializable)HtmlChangeParse.changeDetailList);
					intent.setClass(PlansLineActivity.this, PlanDetailActivity.class);
					startActivity(intent);
				}else if(result==STATE.NetworkError){
					Toast.makeText(PlansLineActivity.this, "网络繁忙，请重新尝试", Toast.LENGTH_SHORT).show();
				}
			};
		}.execute(1);
	}
}
