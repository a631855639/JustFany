package com.xm.bus.search.change;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.xm.bus.R;
import com.xm.bus.common.base.HtmlChangeParse;
import com.xm.bus.common.base.HtmlStopParse;
import com.xm.bus.common.base.HtmlBaseParse.STATE;
import com.xm.bus.common.ui.ExitApplication;
import com.xm.bus.common.ui.LoadingDialog;
import com.xm.bus.common.ui.RemindDialog;
import com.xm.bus.search.common.SearchApp;

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

public class DestinationStopActivity extends ListActivity{
	private ListView lv_relation_stop_content=null;
	private TextView tv_relation_stop_warning=null;
	private List<Map<String, String>> destinationStopList=null;
	private SearchApp myApp;
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.relation_stop_select);
		ExitApplication.getInstance().addActivity(this);
		myApp=(SearchApp) getApplication();
		
		tv_relation_stop_warning=(TextView) findViewById(R.id.tv_relation_stop_warning);
		lv_relation_stop_content=(ListView) findViewById(android.R.id.list);
		
		Intent intent=getIntent();
		destinationStopList=(List<Map<String, String>>) intent.getSerializableExtra("destinationStopList");
		
		tv_relation_stop_warning.setText("与目的地("+myApp.getTo()+")相关的车站有"+destinationStopList.size());
		SimpleAdapter adapter=new SimpleAdapter(this, destinationStopList, R.layout.relation_stop_select_item, new String[]{"relationStopName"}, new int[]{R.id.relationStopName});
		lv_relation_stop_content.setAdapter(adapter);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, R.id.action_quit, 1, "退出");
		menu.getItem(0).setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				RemindDialog.getInstance(DestinationStopActivity.this).show("提醒","你确定要退出吗?",true,true);
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
		myApp.setTo(destinationStopList.get(position).get("relationStopName"));
		LoadingDialog.getInstance(this).show();
		
		new AsyncTask<Void, Void, STATE>() {
			protected  STATE doInBackground(Void... maps) {
				STATE result=HtmlChangeParse.getInstance(DestinationStopActivity.this).getChangePlans(myApp.getFrom(), myApp.getTo());
				return result;
			};
			protected void onPostExecute(STATE result) {
				LoadingDialog.getInstance(DestinationStopActivity.this).dismiss();
				if(result==STATE.ServerMaintenance){
					Toast.makeText(DestinationStopActivity.this, "对不起，服务器正在维护，请稍后再试", Toast.LENGTH_SHORT).show();
				}else if(result==STATE.Success){
					Intent intent=new Intent();
					intent.putExtra("planLineList", (Serializable)HtmlChangeParse.getInstance(DestinationStopActivity.this).getPlanLineList());
					intent.setClass(DestinationStopActivity.this, PlansLineActivity.class);
					startActivity(intent);
				}else if(result==STATE.NetworkError){
					Toast.makeText(DestinationStopActivity.this, "网络繁忙，请重新尝试", Toast.LENGTH_SHORT).show();
				}else if(result==STATE.LineNotExistError){
					Toast.makeText(DestinationStopActivity.this, "很抱歉，两站之间没有公交换乘方案 ", Toast.LENGTH_LONG).show();
				}
			}

		}.execute();
	}
	
	
}
