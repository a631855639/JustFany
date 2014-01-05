package com.xm.bus.location.self;

import android.app.Activity;
import android.view.View;
import android.widget.EditText;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.PoiOverlay;
import com.baidu.mapapi.search.MKPoiInfo;

public class MyPoiOverlay extends PoiOverlay {
	EditText editText;

	public MyPoiOverlay(Activity paramActivity, MapView paramMapView,
			View paramView) {
		super(paramActivity, paramMapView);
		this.editText = ((EditText) paramView);
	}

	protected boolean onTap(int paramInt) {
		MKPoiInfo localMKPoiInfo = getPoi(paramInt);
		String str = localMKPoiInfo.name;
		if (str.contains("("))
			str = localMKPoiInfo.name.replace("(", "£¨").replace(")", "£©");
		this.editText.setText(str);
		return true;
	}
}