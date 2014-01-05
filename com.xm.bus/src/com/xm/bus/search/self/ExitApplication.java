package com.xm.bus.search.self;

import android.app.Activity;
import com.xm.bus.search.utils.HtmlChangeParse;
import com.xm.bus.search.utils.HtmlLineParse;
import com.xm.bus.search.utils.HtmlStopParse;
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
		if (HtmlChangeParse.sourceStopList != null) {
			HtmlChangeParse.sourceStopList.clear();
			HtmlChangeParse.sourceStopList = null;
		}
		if (HtmlChangeParse.destinationStopList != null) {
			HtmlChangeParse.destinationStopList.clear();
			HtmlChangeParse.destinationStopList = null;
		}
		if (HtmlChangeParse.planLineList != null) {
			HtmlChangeParse.planLineList.clear();
			HtmlChangeParse.planLineList = null;
		}
		if (HtmlChangeParse.changeDetailList != null) {
			HtmlChangeParse.changeDetailList.clear();
			HtmlChangeParse.changeDetailList = null;
		}
		if (HtmlLineParse.lineList != null) {
			HtmlLineParse.lineList.clear();
			HtmlLineParse.lineList = null;
		}
		if (HtmlLineParse.stopList != null) {
			HtmlLineParse.stopList.clear();
			HtmlLineParse.stopList = null;
		}
		if (HtmlStopParse.relationLineList != null) {
			HtmlStopParse.relationLineList.clear();
			HtmlStopParse.relationLineList = null;
		}
		if (HtmlStopParse.relationStopList != null) {
			HtmlStopParse.relationStopList.clear();
			HtmlStopParse.relationStopList = null;
		}
		
		for(Activity activity:activities){
			activity.finish();
		}
		System.exit(0);
	}

	public void addActivity(Activity activity){
		activities.add(activity);
	}
}