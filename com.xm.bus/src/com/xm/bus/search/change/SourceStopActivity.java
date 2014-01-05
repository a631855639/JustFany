package com.xm.bus.search.change;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.xm.bus.R;
import com.xm.bus.search.common.SearchApp;
import com.xm.bus.search.self.ExitApplication;
import com.xm.bus.search.self.LoadingDialog;
import com.xm.bus.search.self.RemindDialog;
import com.xm.bus.search.utils.HtmlBaseParse.STATE;
import com.xm.bus.search.utils.HtmlChangeParse;
import com.xm.bus.search.utils.HtmlStopParse;

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

public class SourceStopActivity extends ListActivity{
	private ListView lv_relation_stop_content=null;
	private TextView tv_relation_stop_warning=null;
	private List<Map<String, String>> sourceStopList=null;
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
		sourceStopList=(List<Map<String, String>>) intent.getSerializableExtra("sourceStopList");
		
		tv_relation_stop_warning.setText("�������("+myApp.getFrom()+")"+HtmlStopParse.titleInfo1);
		SimpleAdapter adapter=new SimpleAdapter(this, sourceStopList, R.layout.relation_stop_select_item, new String[]{"relationStopName"}, new int[]{R.id.relationStopName});
		lv_relation_stop_content.setAdapter(adapter);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, R.id.action_quit, 1, "�˳�");
		menu.getItem(0).setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				RemindDialog.getInstance(SourceStopActivity.this).show("����","��ȷ��Ҫ�˳���?",true,true);
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
		myApp.setFrom(sourceStopList.get(position).get("relationStopName"));
		LoadingDialog.getInstance(this).show();
		
		new AsyncTask<Integer, Void, STATE>() {
			int type=0;
			protected  STATE doInBackground(Integer... maps) {
				STATE result=HtmlChangeParse.getDestinationStop(myApp.getTo());
				if(HtmlChangeParse.destinationStopList.size()==1){//Ŀ�ĵ����վ��ֻ��һ��
					myApp.setTo(HtmlChangeParse.destinationStopList.get(0).get("relationStopName"));
					result=HtmlChangeParse.getChangePlans(myApp.getFrom(), myApp.getTo());
					type=1;
				}
				return result;
			};
			protected void onPostExecute(STATE result) {
				LoadingDialog.getInstance(SourceStopActivity.this).dismiss();
				if(result==STATE.ServerMaintenance){
					Toast.makeText(SourceStopActivity.this, "�Բ��𣬷���������ά�������Ժ�����", Toast.LENGTH_SHORT).show();
				}else if(result==STATE.Success){
					Intent intent=new Intent();
					if(type==1){//Ŀ�ĵ����վ��ֻ��һ��
						intent.putExtra("planLineList", (Serializable)HtmlChangeParse.planLineList);
						intent.setClass(SourceStopActivity.this, PlansLineActivity.class);
					}else{
						intent.putExtra("destinationStopList", (Serializable)HtmlChangeParse.destinationStopList);
						intent.setClass(SourceStopActivity.this, DestinationStopActivity.class);
					}
					startActivity(intent);
					//Toast.makeText(RelationStopActivity.this, "�����ɹ�", Toast.LENGTH_SHORT).show();
				}else if(result==STATE.NetworkError){
					Toast.makeText(SourceStopActivity.this, "���緱æ�������³���", Toast.LENGTH_SHORT).show();
				}else if(result==STATE.LineNotExistError){
					Toast.makeText(SourceStopActivity.this, "�ܱ�Ǹ����վ֮��û�й������˷��� ", Toast.LENGTH_LONG).show();
				}
			}

		}.execute();
	}
	
	
}
