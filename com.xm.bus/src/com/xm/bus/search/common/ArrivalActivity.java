package com.xm.bus.search.common;


import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.xm.bus.common.Constant;
import com.xm.bus.common.base.HtmlBaseParse;
import com.xm.bus.common.base.HtmlBaseParse.STATE;
import com.xm.bus.common.service.UpdateWidgetServie;
import com.xm.bus.common.ui.ExitApplication;
import com.xm.bus.common.ui.RemindDialog;
import com.xm.bus.search.model.ArrivalInfo;
import com.xm.bus.search.self.ElasticScrollView;
import com.xm.bus.search.self.ElasticScrollView.OnRefreshListener;
import com.xm.bus.MainDrawerActivity;
import com.xm.bus.R;


public class ArrivalActivity extends Activity implements OnClickListener{
	private SearchApp myApp;
	private Button bt_remind;
	private boolean isRemind;
	private boolean hasWidget;
	private SharedPreferences sp;
	//��վ����
	private AlarmManager alarm;
	private PendingIntent refreshIntent;
	private Intent intentService;
	private long refresh_time;
	private ElasticScrollView scrollView;
	
	private TextView tv_current_line=null;//��ǰ��·
	private TextView tv_current_stop=null;//��ǰվ��
	private TextView tv_next_stop_info=null;//��һվ��վ��Ϣ
	private TextView tv_next_bus_info=null;//��һ����������Ϣ
	private ImageView bus_stop_image=null;//
	private String remindText;
	private View view;
	private LinearLayout loadingLayout;
	private LinearLayout contentLayout;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.arriva_content);
		ExitApplication.getInstance().addActivity(this);
		myApp = (SearchApp) getApplication();
		sp=getSharedPreferences(Constant.SETTING, 0);
		isRemind=sp.getBoolean(Constant.IS_REMIND, false);
		hasWidget=sp.getBoolean(Constant.HAS_WIDGET, false);
		refresh_time=sp.getLong(Constant.REFRESH_TIME, 20000);
		scrollView=(ElasticScrollView) findViewById(R.id.arrival_content);
		initViews();
		
	}
	/**
	 * ��ʼ��ʵʱ��վViews
	 */
	private void initViews(){
		LayoutInflater inflater=getLayoutInflater();
		view=inflater.inflate(R.layout.arrival_item, null);
		loadingLayout=(LinearLayout) view.findViewById(R.id.loading_layout);
		contentLayout=(LinearLayout) view.findViewById(R.id.content_layout);
		
		bt_remind=(Button) view.findViewById(R.id.remind);
		bt_remind.setOnClickListener(this);
		if(isRemind){
			bt_remind.setText("ȡ������");
		}else{
			bt_remind.setText("��վ����");
		}
		intentService=new Intent(this ,UpdateWidgetServie.class); 
		//intentService.putExtra("url", myApp.getUrl());
		refreshIntent=PendingIntent.getService(this, 0, intentService, 0);
		alarm=(AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
		
		tv_current_line=(TextView) view.findViewById(R.id.tv_current_line);
		tv_current_stop=(TextView) view.findViewById(R.id.tv_current_stop);
		tv_next_stop_info=(TextView) view.findViewById(R.id.tv_next_stop_info);
		tv_next_bus_info=(TextView) view.findViewById(R.id.tv_next_bus_info);
		bus_stop_image=(ImageView) view.findViewById(R.id.bus_stop_image);
		initViewsDate();
		addView();
	}
	
	private void addView(){
		scrollView.addChild(view);
		scrollView.setonRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				new AsyncTask<Void, STATE, STATE>() {
					@Override
					protected STATE doInBackground(Void... params) {
						return HtmlBaseParse.getInstacne(ArrivalActivity.this).getArrivalInfo(myApp.getUrl());
					}
					protected void onPostExecute(STATE result) {
						if(result==STATE.Success){
							initViewsDate();
							Log.v("onRefresh", "���³ɹ�!");
						}
						scrollView.onRefreshComplete();
					};
				}.execute();
			}
		});
	}
	/**
	 * ��ʼ��ʵʱ��վ����
	 */
	private void initViewsDate(){
		try{
			ArrivalInfo infos=HtmlBaseParse.getInstacne(this).getArrivalInfo();
			/*if(infos==null){
				infos=myApp.getInfos();
			}*/
			bus_stop_image.setImageBitmap(infos.getPic());
			tv_current_line.setText("��ǰ��·��"+infos.getCurrLine());
			tv_current_stop.setText("��ǰվ�㣺"+infos.getCurrentStation());
			//��վ��Ϣ
			if(infos.getStopsNum().equals("0")&&infos.getKilometers().equals("0")){
				tv_next_stop_info.setText("�Ѿ���վ��");
			}else if(!infos.getStopsNum().equals("")&&!infos.getKilometers().equals("")){
				tv_next_stop_info.setText("��Ŀ�ĵػ���"+infos.getStopsNum()+"վ������һվ����"+infos.getKilometers());
			}else{
				tv_next_stop_info.setVisibility(View.GONE);
			}
			//��һ���������ƻ�
			if(infos.getNextTime().equals("")){
				tv_next_bus_info.setVisibility(View.GONE);
			}else{
				tv_next_bus_info.setVisibility(View.VISIBLE);
				tv_next_bus_info.setText(infos.getNextTime());
			}
			if(infos.getStopsNum().equals("")){
				remindText=infos.getNextTime();
			}else{
				remindText="����'"+infos.getCurrentStation()+"'վ����"+infos.getStopsNum()+"վ";
			}
			loadingLayout.setVisibility(View.GONE);
			contentLayout.setVisibility(View.VISIBLE);
		}catch(Exception e){
			loadingLayout.setVisibility(View.VISIBLE);
			contentLayout.setVisibility(View.GONE);
			new AsyncTask<Void, STATE, STATE>() {
				@Override
				protected STATE doInBackground(Void... params) {
					return HtmlBaseParse.getInstacne(ArrivalActivity.this).getArrivalInfo(myApp.getUrl());
				}
				protected void onPostExecute(STATE result) {
					if(result==STATE.Success){
						initViewsDate();
					}
				};
			}.execute();
		}
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.remind:
			if(!isRemind){//��վ����
				setRemind();
			}else{//ȡ������
				cancleRemind();
				Toast.makeText(this, "ȡ��������", Toast.LENGTH_SHORT).show();
			}
			break;

		default:
			break;
		}
		
	}
	/**
	 * ���õ�վ����
	 */
	private void setRemind(){
		bt_remind.setText("ȡ������");
		if(hasWidget){
			ComponentName thisWidget=new ComponentName(ArrivalActivity.this, RemindWidget.class);
			Intent intent=new Intent(this,ArrivalActivity.class);
			PendingIntent pIntent=PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
			RemoteViews views=new RemoteViews(ArrivalActivity.this.getPackageName(), R.layout.remind_content);
			views.setTextViewText(R.id.remind_text,remindText);
			views.setOnClickPendingIntent(R.id.remind_layout, pIntent);
			AppWidgetManager appWidgetManager=AppWidgetManager.getInstance(ArrivalActivity.this);
			appWidgetManager.updateAppWidget(thisWidget, views);
		}else{
			Toast.makeText(this, "���������ѣ�����Գ�������Ϊ������С�ؼ���������鿴��", Toast.LENGTH_SHORT).show();
		}
		sp.edit().putBoolean(Constant.IS_REMIND, true).commit();
		isRemind=true;
		alarm.setRepeating(AlarmManager.RTC, 0, refresh_time, refreshIntent);
		this.startService(intentService);
		
		if(hasWidget){
			this.finish();
			Intent intentHome = new Intent(Intent.ACTION_MAIN);
			intentHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//ע��,�����µĶ�ջ
			intentHome.addCategory(Intent.CATEGORY_HOME);
	        this.startActivity(intentHome);
		}
	}

	/**
	 * ȡ������
	 */
	private void cancleRemind(){
		isRemind=false;
		sp.edit().putBoolean(Constant.IS_REMIND, false).commit();
		bt_remind.setText("��վ����");
		if(alarm!=null){
			alarm.cancel(refreshIntent);
		}
		this.stopService(intentService);
		if(hasWidget){
			ComponentName thisWidget=new ComponentName(this, RemindWidget.class);
			Intent intent=new Intent(this,MainDrawerActivity.class);
			PendingIntent pIntent=PendingIntent.getActivity(this, 0, intent, 0);
			RemoteViews views=new RemoteViews(this.getPackageName(), R.layout.widget_main);
			views.setOnClickPendingIntent(R.id.widget_layout, pIntent);
			AppWidgetManager appWidgetManager=AppWidgetManager.getInstance(this);
			appWidgetManager.updateAppWidget(thisWidget, views);
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		String str="";
		if(isRemind){
			str="ȡ������";
		}else{
			str="��վ����";
		}
		menu.add(0, 1, 2, str);
		menu.add(1, 2, 2, "�˳�");
		menu.add(2, 3, 2, R.string.action_remind);
		menu.getItem(0).setOnMenuItemClickListener(
				new OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						if(!isRemind){//��վ����
							setRemind();
							item.setTitle("ȡ������");
						}else{//ȡ������
							cancleRemind();
							item.setTitle("��վ����");
						}
						return true;
					}
				});
		menu.getItem(1).setOnMenuItemClickListener(
				new OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						RemindDialog.getInstance(ArrivalActivity.this).show(
								"����", "��ȷ��Ҫ�˳���?", true, true);
						return true;
					}
				});
		menu.getItem(2).setOnMenuItemClickListener(
				new OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						RemindDialog.getInstance(ArrivalActivity.this).show(
								"����", "����ˢ��", false, false);
						return true;
					}
				});
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public void onBackPressed() {
		cancleRemind();
		finish();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		alarm=null;
	}
	
}