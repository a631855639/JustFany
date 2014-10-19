package com.xm.bus.search.line;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
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
import com.xm.bus.common.base.HtmlLineParse;
import com.xm.bus.common.base.HtmlBaseParse.STATE;
import com.xm.bus.common.db.DBHelper;
import com.xm.bus.common.model.LineDetail;
import com.xm.bus.common.model.Stop;
import com.xm.bus.common.ui.ExitApplication;
import com.xm.bus.common.ui.LoadingDialog;
import com.xm.bus.common.ui.RemindDialog;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectLineActivity extends ListActivity {

	private ListView lv_line_content=null;
	private TextView tv_line_warning=null;
	
	//private ArrayAdapter<List<Map<String, String>>> adapter=null;
	private List<Map<String, String>> list=null;
	private Map<String, String> map;
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.line_select);
		ExitApplication.getInstance().addActivity(this);
		
		Intent intent=getIntent();
		list=(List<Map<String, String>>) intent.getSerializableExtra("line");
		
		tv_line_warning=(TextView) findViewById(R.id.tv_line_warning);
		tv_line_warning.setText("共找到"+list.size()+"线路");
		
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
	@SuppressWarnings("unchecked")
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		map=new HashMap<String, String>();
		map=list.get(position);
		/*QueryThread thread=new QueryThread(map);
		thread.start();*/
		LoadingDialog.getInstance(this).show();
		if(!DBHelper.getInstance(SelectLineActivity.this).isStopsExists(map.get("lineName"))){
			new AsyncTask<Map<String, String>, Void, STATE>() {
				@Override
				protected STATE doInBackground(Map<String, String>... params) {
					return HtmlLineParse.getInstance(SelectLineActivity.this).getStopInLine(params[0]);
				}
				
				protected void onPostExecute(STATE result) {
					LoadingDialog.getInstance(SelectLineActivity.this).dismiss();
					if(result==STATE.ServerMaintenance){
						Toast.makeText(SelectLineActivity.this, "对不起，服务器正在维护，请稍后再试", Toast.LENGTH_SHORT).show();
					}else if(result==STATE.Success){
						for (Map<String, String> mapStop : HtmlLineParse.getInstance(SelectLineActivity.this).getStopList()) {
							Stop stop=new Stop(map.get("lineName"), mapStop.get("stopName"), mapStop.get("stopUrl"));
							DBHelper.getInstance(SelectLineActivity.this).addStop(stop);
						}
						LineDetail lineDetail=new LineDetail(map.get("lineNum"), map.get("lineName"), HtmlLineParse.getInstance(SelectLineActivity.this).getTheFirstAndLastTime1(), HtmlLineParse.getInstance(SelectLineActivity.this).getTheFirstAndLastTime2(), String.valueOf(HtmlLineParse.getInstance(SelectLineActivity.this).getStopList().size()), map.get("lineUrl"));
						DBHelper.getInstance(SelectLineActivity.this).updateLineDetail(lineDetail);
						Intent intent=new Intent();
						intent.putExtra("stopInfo", (Serializable)HtmlLineParse.getInstance(SelectLineActivity.this).getStopList());
						intent.putExtra("lineName", map.get("lineName"));
						intent.setClass(SelectLineActivity.this, StopInLineActivity.class);
						startActivity(intent);
					}else if(result==STATE.NetworkError){
						Toast.makeText(SelectLineActivity.this, "网络繁忙，请重新尝试", Toast.LENGTH_SHORT).show();
					}
				};
			}.execute(map);
		}else{
			LoadingDialog.getInstance(SelectLineActivity.this).dismiss();
			//Toast.makeText(SelectLineActivity.this, "非联网查询", Toast.LENGTH_SHORT).show();
			Intent intent=new Intent();
			LineDetail lineDetail=DBHelper.getInstance(SelectLineActivity.this).getLineDetail(map.get("lineNum"),map.get("lineName"));
			intent.putExtra("stopInfo", (Serializable)DBHelper.getInstance(SelectLineActivity.this).getStopsListByLineName(map.get("lineName")));
			intent.putExtra("lineDetail", lineDetail);
			intent.putExtra("lineName", map.get("lineName"));
			intent.setClass(SelectLineActivity.this, StopInLineActivity.class);
			startActivity(intent);
		}
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
				msg.obj=HtmlLineParse.getInstance(SelectLineActivity.this).getStopInLine(map);
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
				intent.putExtra("stopInfo", (Serializable)HtmlLineParse.getInstance(SelectLineActivity.this).getStopList());
				intent.setClass(SelectLineActivity.this, StopInLineActivity.class);
				startActivity(intent);
			}else if(type==STATE.NetworkError){
				Toast.makeText(SelectLineActivity.this, "网络繁忙，请重新尝试", Toast.LENGTH_SHORT).show();
			}
		};
	};
}