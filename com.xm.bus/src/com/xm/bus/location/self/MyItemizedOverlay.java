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

public class MyItemizedOverlay extends ItemizedOverlay<OverlayItem> {
	PopupOverlay pop;
	private View popupInfo = null;
	private View popupLeft = null;
	private View popupRight = null;

	public MyItemizedOverlay(Drawable paramDrawable, MapView paramMapView,
			View paramView, PopupOverlay paramPopupOverlay) {
		super(paramDrawable, paramMapView);
		this.popupInfo = paramView.findViewById(R.id.popinfo);
		this.popupLeft = paramView.findViewById(R.id.popleft);
		this.popupRight = paramView.findViewById(R.id.popright);
		this.pop = paramPopupOverlay;
	}

	protected boolean onTap(int paramInt) {
		OverlayItem localOverlayItem = getItem(paramInt);
		Bitmap[] arrayOfBitmap = new Bitmap[3];
		arrayOfBitmap[0] = BMapUtil.getBitmapFromView(this.popupLeft);
		arrayOfBitmap[1] = BMapUtil.getBitmapFromView(this.popupInfo);
		arrayOfBitmap[2] = BMapUtil.getBitmapFromView(this.popupRight);
		this.pop.showPopup(arrayOfBitmap, localOverlayItem.getPoint(), 32);
		return true;
	}

	public boolean onTap(GeoPoint paramGeoPoint, MapView paramMapView) {
		if (this.pop != null)
			this.pop.hidePop();
		return false;
	}
}