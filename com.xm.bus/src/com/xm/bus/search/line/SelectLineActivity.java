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
import com.xm.bus.search.self.ExitApplication;
import com.xm.bus.search.self.LoadingDialog;
import com.xm.bus.search.self.RemindDialog;
import com.xm.bus.search.utils.HtmlBaseParse.STATE;
import com.xm.bus.search.utils.HtmlLineParse;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectLineActivity extends ListActivity {

	private ListView lv_line_content=null;
	private TextView tv_line_warning=null;
	
	//private ArrayAdapter<List<Map<String, String>>> adapter=null;
	private List<Map<String, String>> list=null;
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.line_select);
		ExitApplication.getInstance().addActivity(this);
		
		Intent intent=getIntent();
		list=(List<Map<String, String>>) intent.getSerializableExtra("line");
		
		tv_line_warning=(TextView) findViewById(R.id.tv_line_warning);
		tv_line_warning.setText(HtmlLineParse.titleInfo1);
		
		lv_line_content=(ListView) findViewById(android.R.id.list);
		SimpleAdapter adapter=new SimpleAdapter(this, list, R.layout.line_select_item, new String[]{"lineName"}, new int[]{R.id.lineName});
		lv_line_content.setAdapter(adapter);
		
		/*AlertDialog.Builder builder=new Builder(this);
		builder.setTitle("Debug");
		builder.setMessage(list.toString());
		builder.create().show();*/
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, R.id.action_quit, 1, "退出");
		menu.getItem(0).setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				RemindDialog.getInstance(SelectLineActivity.this).show("提醒","你确定要退出吗?",true,true);
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
		Map<String, String> map=new HashMap<String, String>();
		map=list.get(position);
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
				msg.obj=HtmlLineParse.getStopInLine(map);
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
			LoadingDialog.getInstance(SelectLineActivity.this).dismiss();
			if(type==STATE.ServerMaintenance){
				Toast.makeText(SelectLineActivity.this, "对不起，服务器正在维护，请稍后再试", Toast.LENGTH_SHORT).show();
			}else if(type==STATE.Success){
				Intent intent=new Intent();
				intent.putExtra("stopInfo", (Serializable)HtmlLineParse.stopList);
				intent.setClass(SelectLineActivity.this, StopInLineActivity.class);
				startActivity(intent);
			}else if(type==STATE.NetworkError){
				Toast.makeText(SelectLineActivity.this, "网络繁忙，请重新尝试", Toast.LENGTH_SHORT).show();
			}
		};
	};
}