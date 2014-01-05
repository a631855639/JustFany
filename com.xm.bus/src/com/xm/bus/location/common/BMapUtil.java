package com.xm.bus.location.common;

import android.graphics.Bitmap;
import android.view.View;
import android.view.View.MeasureSpec;

public class BMapUtil {
	public static Bitmap getBitmapFromView(View paramView) {
		paramView.destroyDrawingCache();
		paramView.measure(View.MeasureSpec.makeMeasureSpec(0, 0),
				View.MeasureSpec.makeMeasureSpec(0, 0));
		paramView.layout(0, 0, paramView.getMeasuredWidth(),
				paramView.getMeasuredHeight());
		paramView.setDrawingCacheEnabled(true);
		return paramView.getDrawingCache(true);
	}
}