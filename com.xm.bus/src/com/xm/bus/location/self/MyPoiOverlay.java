package com.xm.bus.location.self;

import android.app.Activity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.PoiOverlay;
import com.baidu.mapapi.search.MKPlanNode;
import com.baidu.mapapi.search.MKPoiInfo;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.platform.comapi.basestruct.GeoPoint;
/**
 * 自定义poi图层
 *
 */
public class MyPoiOverlay extends PoiOverlay {
	EditText editText;
	MKSearch mkSearch;
	GeoPoint p;
	Activity activity;

	public MyPoiOverlay(Activity activity, MapView mapView,
			View view,MKSearch mkSearch,GeoPoint p) {
		super(activity, mapView);
		this.activity=activity;
		this.editText = ((EditText) view);
		this.mkSearch=mkSearch;
		this.p=p;
	}
	/**
	 * 搜索结果图层点击事件
	 */
	@Override
	protected boolean onTap(int index) {
		MKPoiInfo info = getPoi(index);
		String str = info.name;
		Toast.makeText(activity, str, Toast.LENGTH_SHORT).show();
		if (str.contains("("))
			str = info.name.replace("(", "（").replace(")", "）");
		this.editText.setText(str);
		MKPlanNode start=new MKPlanNode();
		MKPlanNode end=new MKPlanNode();
		start.pt=p;
		end.pt=info.pt;
		//步行路线考虑的优先策略,最短路径优先
		mkSearch.setDrivingPolicy(MKSearch.EBUS_WALK_FIRST);
		//mkSearch.drivingSearch(null, start, null, end);
		mkSearch.walkingSearch(null, start, null, end);
		return true;
	}
}