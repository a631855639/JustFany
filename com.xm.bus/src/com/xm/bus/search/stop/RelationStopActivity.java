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
import com.xm.bus.search.self.ExitApplication;
import com.xm.bus.search.self.LoadingDialog;
import com.xm.bus.search.self.RemindDialog;
import com.xm.bus.search.utils.HtmlBaseParse.STATE;
import com.xm.bus.search.utils.HtmlStopParse;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RelationStopActivity extends ListActivity {
	private ListView lv_relation_stop_content=null;
	private TextView tv_relation_stop_warning=null;
	private List<Map<String, String>> relationStopList=null;
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.relation_stop_select);
		ExitApplication.getInstance().addActivity(this);
		
		tv_relation_stop_warning=(TextView) findViewById(R.id.tv_relation_stop_warning);
		lv_relation_stop_content=(ListView) findViewById(android.R.id.list);
		
		Intent intent=getIntent();
		relationStopList=(List<Map<String, String>>) intent.getSerializableExtra("relationStopList");
		
		tv_relation_stop_warning.setText(HtmlStopParse.titleInfo1);
		SimpleAdapter adapter=new SimpleAdapter(this, relationStopList, R.layout.relation_stop_select_item, new String[]{"relationStopName"}, new int[]{R.id.relationStopName});
		lv_relation_stop_content.setAdapter(adapter);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, R.id.action_quit, 1, "退出");
		menu.getItem(0).setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				RemindDialog.getInstance(RelationStopActivity.this).show("提醒","你确定要退出吗?",true,true);
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
		map=relationStopList.get(position);
		LoadingDialog.getInstance(this).show();
		
		new AsyncTask<Map<String, String>, Void, STATE>() {
			protected  STATE doInBackground(Map<String, String>... maps) {
				STATE result=HtmlStopParse.getRelationLine(maps[0]);
				return result;
			};
			protected void onPostExecute(STATE result) {
				LoadingDialog.getInstance(RelationStopActivity.this).dismiss();
				if(result==STATE.ServerMaintenance){
					Toast.makeText(RelationStopActivity.this, "对不起，服务器正在维护，请稍后再试", Toast.LENGTH_SHORT).show();
				}else if(result==STATE.Success){
					Intent intent=new Intent();
					intent.putExtra("relationLine", (Serializable)HtmlStopParse.relationLineList);
					intent.setClass(RelationStopActivity.this, RelationLineActivity.class);
					startActivity(intent);
					//Toast.makeText(RelationStopActivity.this, "操作成功", Toast.LENGTH_SHORT).show();
				}else if(result==STATE.NetworkError){
					Toast.makeText(RelationStopActivity.this, "网络繁忙，请重新尝试", Toast.LENGTH_SHORT).show();
				}
			}

		}.execute(map);
	}
}