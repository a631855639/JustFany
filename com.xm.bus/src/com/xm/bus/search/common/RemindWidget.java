package com.xm.bus.search.common;

import com.xm.bus.MainDrawerActivity;
import com.xm.bus.R;
import com.xm.bus.common.Constant;
import com.xm.bus.common.MyApplication;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.RemoteViews;

public class RemindWidget extends AppWidgetProvider{
	//private MyApplication myApp;
	private SharedPreferences sp;
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		Log.v("RemindWidget", "onUpdate");
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		Intent intent=new Intent(context,MainDrawerActivity.class);
		PendingIntent pIntent=PendingIntent.getActivity(context, 0, intent, 0);
		RemoteViews views=new RemoteViews(context.getPackageName(), R.layout.widget_main);
		views.setOnClickPendingIntent(R.id.widget_layout, pIntent);
		appWidgetManager.updateAppWidget(appWidgetIds, views);
	}
	
	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		Log.v("RemindWidget", "onDeleted");
		super.onDeleted(context, appWidgetIds);
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.v("RemindWidget", "onReceive");
		super.onReceive(context, intent);
	}
	
	@Override
	public void onEnabled(Context context) {
		Log.v("RemindWidget", "onEnabled");
		sp=context.getSharedPreferences(Constant.SETTING, 0);
		sp.edit().putBoolean(Constant.HAS_WIDGET, true).commit();
		//myApp=(MyApplication) context.getApplicationContext();
		//myApp.setHasWidget(true);
		super.onEnabled(context);
	}
	
	@Override
	public void onDisabled(Context context) {
		Log.v("RemindWidget", "onDisabled");
		sp=context.getSharedPreferences(Constant.SETTING, 0);
		sp.edit().putBoolean(Constant.HAS_WIDGET, false).commit();
		//myApp=(MyApplication) context.getApplicationContext();
		//myApp.setHasWidget(false);
		super.onDisabled(context);
	}
}
