package com.xm.bus.location.self;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.View;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.xm.bus.R;
import com.xm.bus.location.common.BMapUtil;
/**
 * ÅÝÅÝ×Ô¶¨ÒåÍ¼²ã
 *
 */
public class MyItemizedOverlay extends ItemizedOverlay<OverlayItem> {
	PopupOverlay pop;
	private View popupInfo = null;
	private View popupLeft = null;
	private View popupRight = null;
	
	public MyItemizedOverlay(Drawable drawable, MapView mapView,
			View view, PopupOverlay pop) {
		super(drawable, mapView);
		this.popupInfo = view.findViewById(R.id.popinfo);
		this.popupLeft = view.findViewById(R.id.popleft);
		this.popupRight = view.findViewById(R.id.popright);
		this.pop = pop;
	}
	@Override
	protected boolean onTap(int index) {
		OverlayItem item = getItem(index);
		Bitmap[] bitmaps = new Bitmap[]{
			BMapUtil.getBitmapFromView(this.popupLeft),
			BMapUtil.getBitmapFromView(this.popupInfo),
			BMapUtil.getBitmapFromView(this.popupRight)
		};
		this.pop.showPopup(bitmaps, item.getPoint(), 32);
		return true;
	}
	@Override
	public boolean onTap(GeoPoint paramGeoPoint, MapView paramMapView) {
		if (this.pop != null)
			this.pop.hidePop();
		return false;
	}
}