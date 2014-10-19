package com.xm.bus.common.ui;

import android.app.Activity;

import com.xm.bus.common.base.HtmlChangeParse;
import com.xm.bus.common.base.HtmlLineParse;
import com.xm.bus.common.base.HtmlStopParse;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExitApplication {
	private static ExitApplication instance;
	private List<Activity> activities=new ArrayList<Activity>();

	public static ExitApplication getInstance() {
		if (instance == null)
			instance = new ExitApplication();
		return instance;
	}

	public void Exit() {
		
		for(Activity activity:activities){
			activity.finish();
		}
		//System.exit(0);
		activities.clear();
	}

	public void addActivity(Activity activity){
		activities.add(activity);
	}
}