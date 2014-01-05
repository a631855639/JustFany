package com.xm.bus.search.line;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.xm.bus.search.utils.HtmlLineParse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StopInLineActivity extends ListActivity {
	private TextView tv_stop_warning=null;
	private ListView lv_stop_content=null;
	private TextView tv_theFirstAndLastTime1=null;
	private TextView tv_theFirstAndLastTime2=null;
	private List<Map<String, String>> data=new ArrayList<Map<String,String>>();
	private SearchApp myApp;
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.stop_in_line_select);
		ExitApplication.getInstance().addActivity(this);
		
		myApp=(SearchApp) getApplication();
		Intent intent=getIntent();
		data=(List<Map<String, String>>) intent.getSerializableExtra("stopInfo");
		
		tv_stop_warning=(TextView) findViewById(R.id.tv_stop_warning);
		lv_stop_content=(ListView) findViewById(android.R.id.list);
		tv_theFirstAndLastTime1=(TextView) findViewById(R.id.tv_theFirstAndLastTime_info1);
		tv_theFirstAndLastTime1.setText(HtmlLineParse.theFirstAndLastTime1);
		
		tv_theFirstAndLastTime2=(TextView) findViewById(R.id.tv_theFirstAndLastTime_info2);
		tv_theFirstAndLastTime2.setText(HtmlLineParse.theFirstAndLastTime2);
		String stopWraning="当前线路： "+HtmlLineParse.titleInfo2+"(共"+data.size()+"站)";
		tv_stop_warning.setText(stopWraning);
		
		
		SimpleAdapter adapter=new SimpleAdapter(this, data, R.layout.stop_in_line_select_item, new String[]{"stopName"}, new int[]{R.id.stopName});
		lv_stop_content.setAdapter(adapter);
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, R.id.action_quit, 1, "退出");
		menu.getItem(0).setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				RemindDialog.getInstance(StopInLineActivity.this).show("提醒","你确定要退出吗?",true,true);
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
		Map<String, String> map=data.get(position);
		myApp.setUrl(Constant.BASE_URL+map.get("stopUrl"));
		QueryThread thread=new QueryThread(map);
		thread.start();
		LoadingDialog.getInstance(this).show();
	}
	
	class QueryThread extends Thread{
		private Map<String, String> map=new HashMap<String, String>();
		
		public QueryThread(Map<String, String> map) {
			this.map = map;
		}

		@Override
		public void run() {
			while(!Thread.currentThread().isInterrupted()){
				Message msg=Message.obtain();
				msg.obj=HtmlLineParse.getArrivalInfo(map);
				handler.sendMessage(msg);
				Thread.currentThread().interrupt();
			}
		}
	}
	
	@SuppressLint("HandlerLeak")
	private Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			STATE type=(STATE) msg.obj;
			LoadingDialog.getInstance(StopInLineActivity.this).dismiss();
			if(type==STATE.ServerMaintenance){
				Toast.makeText(StopInLineActivity.this, "对不起，服务器正在维护，请稍后再试", Toast.LENGTH_SHORT).show();
			}else if(type==STATE.Success){
				Intent intent=new Intent();
				intent.setClass(StopInLineActivity.this, ArrivalActivity.class);
				startActivity(intent);
			}else if(type==STATE.NetworkError){
				Toast.makeText(StopInLineActivity.this, "网络繁忙，请重新尝试", Toast.LENGTH_SHORT).show();
			}
		};
	};
}