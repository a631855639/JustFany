package com.xm.bus;



import java.io.File;
import java.io.InputStream;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.widget.DrawerLayout;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.Spinner;
import android.widget.Toast;





import com.xm.bus.common.Constant;
import com.xm.bus.common.MyApplication;
import com.xm.bus.common.db.DBHelper;
import com.xm.bus.common.model.VersionInfo;
import com.xm.bus.common.service.DownloadTask;
import com.xm.bus.common.service.HttpDownloader;
import com.xm.bus.common.ui.ExitApplication;
import com.xm.bus.common.ui.GuidePager;
import com.xm.bus.common.ui.LoadingDialog;
import com.xm.bus.common.ui.RemindDialog;
import com.xm.bus.common.utils.DownUtil;
import com.xm.bus.common.utils.XMLSaxParser;
import com.xm.bus.search.common.RemindWidget;

public class MainDrawerActivity extends FragmentActivity implements OnClickListener{
	private FragmentTabHost mTabHost;
	private DrawerLayout drawerLayout;
	private long currTime=0;//˫�������˳�
	private LinearLayout leftLayout;
	MyApplication App;
	
	private SharedPreferences sp_setting;
	private SharedPreferences sp_history;
	
	private Spinner refreshSpinner;
	private Spinner stopsSpinner;
	private Spinner recordSpinner;
	private ImageView ringSet;
	private ImageView vibrateSet;
	
	private boolean isRing;
	private boolean isVibrate;
	private int timesPosition;
	private int stopsPosition;
	private int limitPosition;
	
	private ProgressDialog progressDialog;
	private VersionInfo versionInfo;
	private Thread updateThread;
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		//�����ޱ���  
        requestWindowFeature(Window.FEATURE_NO_TITLE);  
        //����ȫ��  
		/*getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,   
                WindowManager.LayoutParams.FLAG_FULLSCREEN);  */
		setContentView(R.layout.tabs_drawer_main);
		ExitApplication.getInstance().addActivity(this);
		App = (MyApplication) getApplication();
		App.setWidth(getWindowManager().getDefaultDisplay().getWidth());
		App.setHeight(getWindowManager().getDefaultDisplay().getHeight());
		
		sp_setting=getSharedPreferences(Constant.SETTING, 0);
		sp_history=getSharedPreferences(Constant.HISTORY_RECORDS, 0);
		
		isRing=sp_setting.getBoolean(Constant.IS_RING, false);
		isVibrate=sp_setting.getBoolean(Constant.IS_VIBRATE, false);
		
		long times=sp_setting.getLong(Constant.REFRESH_TIME, 20000);
		String refreshTime=String.valueOf(times/1000)+"��";
		String[] timeList=getResources().getStringArray(R.array.refresh_times);
		for(int i=0;i<timeList.length;i++){
			if(refreshTime.equals(timeList[i])){
				timesPosition=i;
				break;
			}
		}
		
		int stops=sp_setting.getInt(Constant.REMIND_STOPS, 2);
		String alertStops=String.valueOf(stops)+"վ";
		String[] stopList=getResources().getStringArray(R.array.stops);
		for(int i=0;i<stopList.length;i++){
			if(alertStops.equals(stopList[i])){
				stopsPosition=i;
				break;
			}
		}
		
		int limit=sp_history.getInt(Constant.HISTORY_SHOW_LIMIT, 5);
		String limitStr=limit+"��";
		String[] limitList=getResources().getStringArray(R.array.history_limit);
		for (int i = 0; i < limitList.length; i++) {
			if(limitStr.equals(limitList[i])){
				limitPosition=i;
				break;
			}
		}
		initDrawer();
		initTabHost();
	}
	/*
	 * ����ѡ�
	 */
	private void initTabHost() {
		mTabHost = ((FragmentTabHost) findViewById(android.R.id.tabhost));
		mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
		mTabHost.addTab(mTabHost.newTabSpec("��ѯ").setIndicator("��ѯ",
						getResources().getDrawable(R.drawable.tabs_user)),SearchActivity.class, null);
		mTabHost.addTab(mTabHost.newTabSpec("��λ").setIndicator("��λ",getResources().getDrawable(R.drawable.tabs_location)),
						LocationActivity.class, 
						null);
	}
	/**
	 * ��ʼ�������˵���
	 */
	@SuppressWarnings("deprecation")
	private void initDrawer(){
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		leftLayout=(LinearLayout) findViewById(R.id.left_drawer);
		LayoutInflater inflater=getLayoutInflater();
		View view=inflater.inflate(R.layout.menu_item, leftLayout);
		RelativeLayout refreshLayout=(RelativeLayout) view.findViewById(R.id.refresh_layout);
		RelativeLayout historyLayout=(RelativeLayout) view.findViewById(R.id.history_record_layout);
		RelativeLayout stopsLayout=(RelativeLayout) view.findViewById(R.id.early_stops_layout);
		RelativeLayout ringLayout=(RelativeLayout) view.findViewById(R.id.ring_layout);
		RelativeLayout vibrateLayout=(RelativeLayout) view.findViewById(R.id.vibrate_layout);
		RelativeLayout clearLayout=(RelativeLayout) view.findViewById(R.id.clear_layout);
		RelativeLayout updateLayout=(RelativeLayout) view.findViewById(R.id.update_layout);
		RelativeLayout feedbackLayout=(RelativeLayout) view.findViewById(R.id.feedback_layout);
		RelativeLayout aboutLayout=(RelativeLayout) view.findViewById(R.id.about_layout);
		RelativeLayout quitLayout=(RelativeLayout) view.findViewById(R.id.quit_layout);
		
		refreshLayout.setOnClickListener(this);
		stopsLayout.setOnClickListener(this);
		ringLayout.setOnClickListener(this);
		vibrateLayout.setOnClickListener(this);
		clearLayout.setOnClickListener(this);
		updateLayout.setOnClickListener(this);
		feedbackLayout.setOnClickListener(this);
		aboutLayout.setOnClickListener(this);
		quitLayout.setOnClickListener(this);
		historyLayout.setOnClickListener(this);
		
		refreshSpinner=(Spinner) view.findViewById(R.id.refresh_time);
		stopsSpinner=(Spinner) view.findViewById(R.id.remind_stops);
		refreshSpinner.setSelection(timesPosition);
		refreshSpinner.setOnItemSelectedListener(OISListener);
		stopsSpinner.setSelection(stopsPosition);
		stopsSpinner.setOnItemSelectedListener(OISListener);
		recordSpinner=(Spinner) view.findViewById(R.id.history_limit);
		recordSpinner.setSelection(limitPosition);
		recordSpinner.setOnItemSelectedListener(OISListener);
		
		ringSet=(ImageView) view.findViewById(R.id.ring_set);
		ringSet.setBackgroundDrawable(getResources().getDrawable(isRing?R.drawable.set_on:R.drawable.set_off));
		vibrateSet=(ImageView) view.findViewById(R.id.vibrate_set);
		vibrateSet.setBackgroundDrawable(getResources().getDrawable(isVibrate?R.drawable.set_on:R.drawable.set_off));
	}
	@SuppressWarnings("deprecation")
	@Override
	public void onClick(View v) {//���ý���ĵ�������¼�
		switch (v.getId()) {
		case R.id.refresh_layout://ˢ��Ƶ��
			
			break;
		case R.id.early_stops_layout://��ǰ��վ����
			
			break;
		case R.id.history_record_layout://������¼
			
			break;
		case R.id.ring_layout://�Ƿ�����
			isRing=!isRing;
			ringSet.setBackgroundDrawable(getResources().getDrawable(isRing?R.drawable.set_on:R.drawable.set_off));
			sp_setting.edit().putBoolean(Constant.IS_RING, isRing).commit();
			break;
		case R.id.vibrate_layout://�Ƿ���
			isVibrate=!isVibrate;
			vibrateSet.setBackgroundDrawable(getResources().getDrawable(isVibrate?R.drawable.set_on:R.drawable.set_off));
			sp_setting.edit().putBoolean(Constant.IS_VIBRATE, isVibrate).commit();
			break;
		case R.id.clear_layout://�������
			View clearView=getLayoutInflater().inflate(R.layout.clear_cache_content, null);
			AlertDialog.Builder clear_builder=new Builder(MainDrawerActivity.this);
			clear_builder.setTitle("��ʾ");
			clear_builder.setView(clearView);
			//clear_builder.setMessage("������潫���ʼ����������ã��Ƿ�ȷ�������");
			clear_builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					sp_setting.edit().clear().commit();
					sp_history.edit().clear().commit();
					DBHelper.getInstance(MainDrawerActivity.this).clear();
					dialog.dismiss();
					Toast.makeText(MainDrawerActivity.this, "����ɹ�!", Toast.LENGTH_SHORT).show();
				}
			});
			clear_builder.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
			});
			clear_builder.create().show();
			break;
		case R.id.update_layout://������
			progressDialog=new ProgressDialog(this);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progressDialog.setMessage("��������...");
			LoadingDialog.getInstance(MainDrawerActivity.this).show();
			updateCheck();
			break;
		case R.id.feedback_layout://�������
			LayoutInflater inflater=getLayoutInflater();
			View view=inflater.inflate(R.layout.feedback_content, null);
			AlertDialog.Builder feedback_builder=new Builder(MainDrawerActivity.this);
			feedback_builder.setTitle("�������");
			feedback_builder.setView(view);
			feedback_builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			feedback_builder.create().show();
			break;
		case R.id.about_layout://����
			Intent intent=new Intent();
			intent.setClass(MainDrawerActivity.this, GuidePager.class);
			startActivity(intent);
			closedDrawer();
			break;
		case R.id.quit_layout://�˳�
			RemindDialog.getInstance(MainDrawerActivity.this).show(
					"����", "��ȷ��Ҫ�˳���?", true, true);
			break;
		default:
			break;
		}
	}
	
	/**
	 * ������ѡ���¼�
	 */
	private OnItemSelectedListener OISListener=new OnItemSelectedListener() {
		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			switch (parent.getId()) {
				case R.id.refresh_time:
					String times=(String) parent.getItemAtPosition(position);
					long refresh_time=Long.valueOf(times.replace("��", "").trim())*1000;
					sp_setting.edit().putLong(Constant.REFRESH_TIME, refresh_time).commit();
					break;
				case R.id.remind_stops:
					String stop=(String) parent.getItemAtPosition(position);
					int stops=Integer.valueOf(stop.replace("վ", "").trim());
					sp_setting.edit().putInt(Constant.REMIND_STOPS, stops).commit();
					break;
				case R.id.history_limit:
					String limitStr=(String) parent.getItemAtPosition(position);
					int limit=Integer.valueOf(limitStr.replace("��", "").trim());
					sp_history.edit().putInt(Constant.HISTORY_SHOW_LIMIT, limit).commit();
					break;
				default:
					break;
			}
			
		}
		@Override
		public void onNothingSelected(AdapterView<?> parent) {
		}
	};
	
	/**
	 * ���¼��
	 */
	private void updateCheck(){
		updateThread=new Thread(){
			@Override
			public void run() {
				while(!Thread.currentThread().isInterrupted()){
					HttpDownloader downloader=new HttpDownloader();
					VersionInfo info=null;
					try {
						InputStream is=downloader.getInputStreamFromUrl(Constant.DOWNLOAD_URL);
						info=XMLSaxParser.getVersionInfo(is);//sax����
					} catch (Exception e) {
						e.printStackTrace();
						Thread.currentThread().interrupt();
					}
					Message msg=Message.obtain();
					msg.obj=info;
					msg.arg1=1;
					updateHandler.sendMessage(msg);
					Thread.currentThread().interrupt();
				}
			}
		};
		updateThread.start();
	}
	/**
	 * �������ѶԻ���
	 */
	private void showUpdateDialog(){
		AlertDialog.Builder dialog=new Builder(this);
		dialog.setIcon(android.R.drawable.ic_dialog_info);
		dialog.setTitle("��������");
		dialog.setMessage("���°汾���Ƿ����?");
		dialog.setCancelable(false);
		
		dialog.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
					DownUtil.createFile(Constant.FILE_PATH);
					String apkPath=Environment.getExternalStorageDirectory() + Constant.APK_PATH;
					progressDialog.show();
					UpdateTask updateTask=new UpdateTask(apkPath,versionInfo.getUrl());
					new Thread(updateTask).start();
				}else{
					Toast.makeText(MainDrawerActivity.this, "SD�������ã������SD����", Toast.LENGTH_SHORT).show();
				}
				dialog.dismiss();
			}
		});
		
		dialog.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		dialog.create().show();
	}
	/**
	 * �����߳�
	 * @author Administrator
	 *
	 */
	class UpdateTask implements Runnable{
		private String filePath;
		private String url;
		
		public UpdateTask(String filePath, String url) {
			this.filePath = filePath;
			this.url = url;
		}
		@Override
		public void run() {
			while(!Thread.currentThread().isInterrupted()){
				try {
					File file=DownloadTask.getFile(url, filePath, progressDialog);
					progressDialog.dismiss();
					Intent intent=new Intent();
					intent.setAction(Intent.ACTION_VIEW);
					intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
					Thread.currentThread().interrupt();
					finish();
					startActivity(intent);
				} catch (Exception e) {
					e.printStackTrace();
					Message msg=Message.obtain();
					msg.arg1=2;
					updateHandler.sendMessage(msg);
					Thread.currentThread().interrupt();
				}
			}
		}
	}
	/**
	 * ������Ϣ����
	 */
	@SuppressLint("HandlerLeak")
	private Handler updateHandler=new Handler(){
		public void handleMessage(Message msg) {
			if(msg.arg1==1){
				LoadingDialog.getInstance(MainDrawerActivity.this).dismiss();
				versionInfo=(VersionInfo) msg.obj;
				if(versionInfo!=null){
					if(!versionInfo.getVersion().equals(DownUtil.getVersion(MainDrawerActivity.this))){
						showUpdateDialog();
					}else{
						Toast.makeText(MainDrawerActivity.this, "�������°汾", Toast.LENGTH_SHORT).show();
					}
				}else{
					Toast.makeText(MainDrawerActivity.this, "��ȡ������Ϣ�쳣�����Ժ�����", Toast.LENGTH_SHORT).show();
				}
			}else if(msg.arg1==2){
				progressDialog.dismiss();
				Toast.makeText(MainDrawerActivity.this, "�����쳣", Toast.LENGTH_SHORT).show();
			}
		};
	};
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		if (keyCode == KeyEvent.KEYCODE_BACK) {//˫���˳�
			if (drawerLayout.isDrawerOpen(leftLayout)) {
				drawerLayout.closeDrawer(leftLayout);
				return true;
			}
			if(System.currentTimeMillis()-currTime>2000){
				Toast.makeText(this, "�ٰ�һ���˳�����", Toast.LENGTH_SHORT).show();
				currTime=System.currentTimeMillis();
			}else{
				finish();
			}
			
		}else if(keyCode==KeyEvent.KEYCODE_MENU){
			if (drawerLayout.isDrawerOpen(leftLayout)) {
				drawerLayout.closeDrawer(leftLayout);
			} else {
				drawerLayout.openDrawer(leftLayout);
			}
		}
		return false;
	}
	@Override
	protected void onDestroy() {
		if (App.mBMapManager != null) {
			App.mBMapManager.destroy();
			App.mBMapManager = null;
		}
		ComponentName thisWidget=new ComponentName(this, RemindWidget.class);
		Intent intent=new Intent(this,MainDrawerActivity.class);
		PendingIntent pIntent=PendingIntent.getActivity(this, 0, intent, 0);
		RemoteViews views=new RemoteViews(this.getPackageName(), R.layout.widget_main);
		views.setOnClickPendingIntent(R.id.widget_layout, pIntent);
		AppWidgetManager appWidgetManager=AppWidgetManager.getInstance(this);
		appWidgetManager.updateAppWidget(thisWidget, views);
		sp_setting.edit().putBoolean(Constant.IS_REMIND, false).commit();
		super.onDestroy();
	}
	
	private void closedDrawer(){
		if (drawerLayout.isDrawerOpen(leftLayout)) {
			drawerLayout.closeDrawer(leftLayout);
		}
	}
	
}