package com.xm.bus.location.ui;

import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.xm.bus.R;
import com.xm.bus.search.self.LoadingDialog;
import com.xm.bus.search.stop.RelationLineActivity;
import com.xm.bus.search.stop.RelationStopActivity;
import com.xm.bus.search.utils.HtmlBaseParse.STATE;
import com.xm.bus.search.utils.HtmlStopParse;
import java.io.Serializable;
import java.util.Map;

public class DrawerMainActivity extends FragmentActivity implements
		View.OnClickListener {
	ActionBar actionBar;
	private ArrayAdapter<String> arrayAdapter;
	private ImageButton backButton;
	private DrawerLayout drawerLayout;
	
	private String[] items;
	private EditText keyText;
	private ListView leftList;
	private ImageButton searchButton;
	
	@Override
	protected void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		setContentView(R.layout.drawer_layout);
		initViews();
	}
	
	private void initViews() {
		this.drawerLayout = ((DrawerLayout) findViewById(R.id.main_layout));
		this.items = getResources().getStringArray(R.array.left_array);
		this.leftList = ((ListView) findViewById(R.id.left_drawer));
		this.arrayAdapter = new ArrayAdapter<String>(this, R.layout.left_drawer_list_item, this.items);
		this.leftList.setAdapter(this.arrayAdapter);
		this.leftList.setOnItemClickListener(this.itemListener);
		initFragments();
	}
	
	private void initFragments() {
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.add(R.id.main_content, new DrawerFragment());
		transaction.commit();
	}
	
	private void initActionBar() {
		this.actionBar.setDisplayHomeAsUpEnabled(true);
		this.actionBar.setDisplayShowTitleEnabled(false);
		this.actionBar.setDisplayShowHomeEnabled(false);
		this.actionBar.setDisplayShowCustomEnabled(true);
		View localView = LayoutInflater.from(this).inflate(R.layout.actionbar_menu_item, null);
		this.backButton = ((ImageButton) localView.findViewById(R.id.back));
		this.searchButton = ((ImageButton) localView.findViewById(R.id.search_button));
		this.keyText = ((EditText) localView.findViewById(R.id.tip));
		this.backButton.setOnClickListener(this);
		this.searchButton.setOnClickListener(this);
		this.actionBar.setCustomView(localView);
	}

	

	
	@Override
	public void onBackPressed() {
		finish();
	}
	
	@Override
	public void onClick(View view){
		if(view.getId()==R.id.back){
			onBackPressed();
		}else if(view.getId()==R.id.search_button){
			
			new AsyncTask<Void, Void, STATE>() {
				protected  STATE doInBackground(Void...p) {
					STATE result=HtmlStopParse.getRelationStop(keyText.getText().toString().trim());
					return result;
				};
				protected void onPostExecute(STATE result) {
					LoadingDialog.getInstance(DrawerMainActivity.this).dismiss();
					if(result==STATE.ServerMaintenance){
						Toast.makeText(DrawerMainActivity.this, "对不起，服务器正在维护，请稍后再试", Toast.LENGTH_SHORT).show();
					}else if(result==STATE.LineNotExistError){
						Toast.makeText(DrawerMainActivity.this, "你所查询的线路不存在，请重新输入", Toast.LENGTH_SHORT).show();
					}else if(result==STATE.InputError){
						Toast.makeText(DrawerMainActivity.this, "你输入的线路或站点格式不对，请重新输入", Toast.LENGTH_SHORT).show();
					}else if(result==STATE.NetworkError){
						Toast.makeText(DrawerMainActivity.this, "网络繁忙，请重新尝试", Toast.LENGTH_SHORT).show();
					}else if(result==STATE.Success){
						Intent intent=new Intent();
						intent.putExtra("relationStopList", (Serializable)HtmlStopParse.relationStopList);
						intent.setClass(DrawerMainActivity.this, RelationStopActivity.class);
						startActivity(intent);
					}
				}

			}.execute();
			LoadingDialog.getInstance(this).show();
		}
	}
	
	
	
	AdapterView.OnItemClickListener itemListener = new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			
		}
		
	};
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			if (!this.drawerLayout.isDrawerOpen(this.leftList)){
				this.drawerLayout.closeDrawer(this.leftList);
			}else{
				this.drawerLayout.isDrawerOpen(this.leftList);
			}
		}
		return true;
	}
}