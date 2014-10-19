package com.xm.bus.common.service;



import com.xm.bus.R;
import com.xm.bus.common.Constant;
import com.xm.bus.common.MyApplication;
import com.xm.bus.common.base.HtmlBaseParse;
import com.xm.bus.common.base.HtmlBaseParse.STATE;
import com.xm.bus.search.common.ArrivalActivity;
import com.xm.bus.search.common.RemindWidget;
import com.xm.bus.search.model.ArrivalInfo;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

public class UpdateWidgetServie extends Service{
	private MyApplication myApp;
	private String remindText="";
	private int stops=-1;
	private boolean isRemind=false;
	private boolean hasWidget;
	private boolean isRing;
	private boolean isVibrate;
	private SharedPreferences sp;
	private NotificationManager manager;
	private  int REMIND_STOPS;
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		myApp=(MyApplication) getApplication();
		sp=getSharedPreferences("Setting", 0);
		REMIND_STOPS=sp.getInt(Constant.REMIND_STOPS, 2);
		isRemind=sp.getBoolean(Constant.IS_REMIND, false);
		hasWidget=sp.getBoolean(Constant.HAS_WIDGET, false);
		new AsyncTask<Void, STATE, STATE>() {
			@Override
			protected STATE doInBackground(Void... params) {
				STATE result=HtmlBaseParse.getInstacne(UpdateWidgetServie.this).getArrivalInfo(myApp.getUrl());
				if(result==STATE.Success){
					try{
						ArrivalInfo infos=HtmlBaseParse.getInstacne(UpdateWidgetServie.this).getArrivalInfo();
						if(infos.getStopsNum().equals("")){
							remindText=infos.getNextTime();
						}else{
							remindText="距离'"+infos.getCurrentStation()+"'站还差"+infos.getStopsNum()+"站";
						}
						stops=Integer.parseInt(infos.getStopsNum());
						
					}catch(Exception e){
						result=STATE.NetworkError;
					}
				}
				return result;
			}
			protected void onPostExecute(STATE result) {
				reminding();
				if(result==STATE.Success){
					if(hasWidget){
						ComponentName thisWidget=new ComponentName(UpdateWidgetServie.this, RemindWidget.class);
						RemoteViews views=new RemoteViews(UpdateWidgetServie.this.getPackageName(), R.layout.remind_content);
						Intent intent1=new Intent(UpdateWidgetServie.this,ArrivalActivity.class);
						PendingIntent pIntent=PendingIntent.getActivity(UpdateWidgetServie.this, 0, intent1,0);
						views.setTextViewText(R.id.remind_text,remindText);
						views.setOnClickPendingIntent(R.id.remind_layout, pIntent);
						AppWidgetManager appWidgetManager=AppWidgetManager.getInstance(UpdateWidgetServie.this);
						appWidgetManager.updateAppWidget(thisWidget, views);
						Log.v("UpdateWidgetServie", "界面更新了！！！！！");
					}
				}
				stopSelf();
				}
		}.execute();
		return super.onStartCommand(intent, flags, startId);
	}
	/**
	 * 到站提醒
	 */
	@SuppressWarnings("deprecation")
	private void reminding(){
		if(isRemind){
			if(stops>=0&&stops<=REMIND_STOPS){
				manager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
				Notification notification=new Notification();
				AudioManager audioManager=(AudioManager) getSystemService(Context.AUDIO_SERVICE);
				switch (audioManager.getRingerMode()) {//优先处理系统设置，在处理软件设置
				case AudioManager.RINGER_MODE_SILENT://静音模式，值为0，这时候不震动，不响铃
					notification.sound=null;
					notification.vibrate=null;
					break;
				case AudioManager.RINGER_MODE_VIBRATE://震动模式，值为1，这时候震动，不响铃
					notification.sound=null;
					notification.defaults|=Notification.DEFAULT_VIBRATE;
				case AudioManager.RINGER_MODE_NORMAL://常规模式，值为2，
					isRing=sp.getBoolean(Constant.IS_RING, false);
					isVibrate=sp.getBoolean(Constant.IS_VIBRATE, false);
					if(isVibrate){
						notification.defaults |= Notification.DEFAULT_VIBRATE;
					}else{
						notification.vibrate=null;
					}
					if(isRing){
						/*Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
						notification.sound=alert;*/
						notification.defaults |= Notification.DEFAULT_SOUND;
					}
					/*if(!sp.contains(Constant.IS_RING)&&!sp.contains(Constant.IS_VIBRATE)){
						notification.defaults |= Notification.DEFAULT_VIBRATE;
						notification.defaults |= Notification.DEFAULT_SOUND;
					}else{
						isRing=sp.getBoolean(Constant.IS_RING, false);
						isVibrate=sp.getBoolean(Constant.IS_VIBRATE, false);
						if(isVibrate){
							if(audioManager.getVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER) == AudioManager.VIBRATE_SETTING_OFF){
								notification.vibrate = null;
							}else if(audioManager.getVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER) == AudioManager.VIBRATE_SETTING_ONLY_SILENT){
								notification.vibrate = null;
							}else{
								notification.defaults |= Notification.DEFAULT_VIBRATE;
							}
						}else{
							notification.vibrate=null;
						}
						if(isRing){
							Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
							notification.sound=alert;
							notification.defaults |= Notification.DEFAULT_SOUND;
						}
					}*/
				default:
					break;
				}
				Toast.makeText(UpdateWidgetServie.this, "车快到站了，请注意!!", Toast.LENGTH_LONG).show();
				manager.notify("到站提醒", 888, notification);
				/*if(audioManager.getRingerMode()!=AudioManager.RINGER_MODE_SILENT){
					notification.defaults=Notification.DEFAULT_VIBRATE;
					manager.notify("到站提醒", 888, notification);
				}*/
				
			}
		}else{
			if(manager!=null){
				manager.cancel("到站提醒",888);
			}
		}
	}
	
}
