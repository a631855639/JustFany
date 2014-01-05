package com.xm.bus.search.stop;

import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.xm.bus.R;
import com.xm.bus.common.MyApplication;
import com.xm.bus.search.common.ArrivalActivity;
import com.xm.bus.search.common.SearchApp;
import com.xm.bus.search.self.ExitApplication;
import com.xm.bus.search.self.LoadingDialog;
import com.xm.bus.search.self.RemindDialog;
import com.xm.bus.search.utils.Constant;
import com.xm.bus.search.utils.HtmlBaseParse.STATE;
import com.xm.bus.search.utils.HtmlStopParse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RelationLineActivity extends ListActivity {
	private TextView tv_relationLineWarning=null;
	private ListView lv_relationLineContent=null;
	private List<Map<String, String>> relationLineList=null;
	private SearchApp myApp=null;
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.relation_line_select);
		ExitApplication.getInstance().addActivity(this);
		myApp=(SearchApp) getApplication();
		
		Intent intent=getIntent();
		relationLineList=(List<Map<String, String>>) intent.getSerializableExtra("relationLine");
		
		tv_relationLineWarning=(TextView) findViewById(R.id.tv_relation_line_warning);
		tv_relationLineWarning.setText(HtmlStopParse.titleInfo2);
		
		lv_relationLineContent=(ListView) findViewById(android.R.id.list);
		SimpleAdapter adapter=new SimpleAdapter(this, relationLineList, R.layout.relation_line_select_item, new String[]{"relationLineName"}, new int[]{R.id.relationLineName});
		lv_relationLineContent.setAdapter(adapter);
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, R.id.action_quit, 1, "退出");
		menu.getItem(0).setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				RemindDialog.getInstance(RelationLineActivity.this).show("提醒","你确定要退出吗?",true,true);
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
		Map<String, String> map=new HashMap<String, String>();
		map=relationLineList.get(position);
		myApp.setUrl(Constant.BASE_URL+map.get("relationLineUrl"));
		LoadingDialog.getInstance(this).show();
		new AsyncTask<Map<String, String>, Void, STATE>() {
			@Override
			protected STATE doInBackground(Map<String, String>... params) {
				STATE result=HtmlStopParse.getArrivalInfo(params[0]);
				return result;
			}
			
			@Override
			protected void onPostExecute(STATE result) {
				LoadingDialog.getInstance(RelationLineActivity.this).dismiss();
				if(result==STATE.ServerMaintenance){
					Toast.makeText(RelationLineActivity.this, "对不起，服务器正在维护，请稍后再试", Toast.LENGTH_SHORT).show();
				}else if(result==STATE.Success){
					Intent intent=new Intent();
					intent.setClass(RelationLineActivity.this, ArrivalActivity.class);
					startActivity(intent);
				}else if(result==STATE.NetworkError){
					Toast.makeText(RelationLineActivity.this, "网络繁忙，请重新尝试", Toast.LENGTH_SHORT).show();
				}
			}
			
		}.execute(map);
	}
}