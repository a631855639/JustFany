package com.xm.bus.search.change;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.xm.bus.R;
import com.xm.bus.search.common.ArrivalActivity;
import com.xm.bus.search.common.SearchApp;
import com.xm.bus.search.self.ExitApplication;
import com.xm.bus.search.self.LoadingDialog;
import com.xm.bus.search.self.RemindDialog;
import com.xm.bus.search.utils.Constant;
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

public class PlanDetailActivity extends ListActivity {
	private TextView head_info=null;
	private TextView plan_title=null;
	private ListView lv_content=null;
	private List<Map<String, String>> planDetaiList=null;
	private Map<String, String> map=null;
	private SearchApp myApp;
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.plan_line_detail);
		ExitApplication.getInstance().addActivity(this);
		myApp=(SearchApp) getApplication();
		
		Intent intent=getIntent();
		planDetaiList=(List<Map<String, String>>) intent.getSerializableExtra("planDetaiList");
		
		head_info=(TextView) findViewById(R.id.head_plan_detail_info);
		head_info.setText(HtmlChangeParse.titleInfo1);
		plan_title=(TextView) findViewById(R.id.plan_title);
		plan_title.setText(HtmlChangeParse.titleInfo2);
		
		lv_content=(ListView) findViewById(android.R.id.list);
		SimpleAdapter adapter=new SimpleAdapter(this, planDetaiList, R.layout.plan_line_detail_item, new String[]{"relationLineName", "up","down"},new int[]{R.id.plan_line_name,R.id.up,R.id.down});
		lv_content.setAdapter(adapter);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, R.id.action_quit, 1, "退出");
		menu.getItem(0).setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				RemindDialog.getInstance(PlanDetailActivity.this).show("提醒","你确定要退出吗?",true,true);
				return true;
			}
		});
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public void onBackPressed() {
		finish();
	}
	@SuppressWarnings("unchecked")
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		map=new HashMap<String, String>();
		map=planDetaiList.get(position);
		myApp.setUrl(Constant.BASE_URL+map.get("relationLineUrl"));
		LoadingDialog.getInstance(this).show();
		new AsyncTask<Map<String, String>, Void, STATE>() {
			@Override
			protected STATE doInBackground(Map<String, String>... params) {
				STATE result=HtmlChangeParse.getArrivalInfo(params[0]);
				return result;
			}
			
			@Override
			protected void onPostExecute(STATE result) {
				LoadingDialog.getInstance(PlanDetailActivity.this).dismiss();
				if(result==STATE.ServerMaintenance){
					Toast.makeText(PlanDetailActivity.this, "对不起，服务器正在维护，请稍后再试", Toast.LENGTH_SHORT).show();
				}else if(result==STATE.Success){
					Intent intent=new Intent();
					intent.setClass(PlanDetailActivity.this, ArrivalActivity.class);
					startActivity(intent);
				}else if(result==STATE.NetworkError){
					Toast.makeText(PlanDetailActivity.this, "网络繁忙，请重新尝试", Toast.LENGTH_SHORT).show();
				}
			}
		}.execute(map);
	}
}
