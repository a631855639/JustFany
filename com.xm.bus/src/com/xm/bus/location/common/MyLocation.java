package com.xm.bus.location.common;

import android.content.Context;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import javax.security.auth.Destroyable;

public class MyLocation implements Destroyable {
	private Context context;
	private DoAfterListener doAfterListener;
	private LocationClient mLocClient = null;

	public MyLocation(Context context) {
		this.context = context;
		init();
	}

	private void init() {
		this.mLocClient = new LocationClient(
				this.context.getApplicationContext());
		this.mLocClient.setAK("pGMEW4fzUg34mQkKVegKcEY0");
		this.mLocClient.registerLocationListener(new MyLocationListener());
		LocationClientOption localLocationClientOption = new LocationClientOption();
		localLocationClientOption.setOpenGps(true);
		localLocationClientOption.setPriority(1);
		localLocationClientOption.setCoorType("bd09ll");
		localLocationClientOption.setAddrType("all");
		localLocationClientOption.setProdName("厦门实时公交查询");
		this.mLocClient.setLocOption(localLocationClientOption);
		this.mLocClient.start();
	}

	private void onDoAfter(BDLocation paramBDLocation) {
		if (this.doAfterListener != null)
			this.doAfterListener.onDoAfter(paramBDLocation);
	}

	public void destroy() {
		if (this.mLocClient != null) {
			this.mLocClient.stop();
			this.mLocClient = null;
		}
	}

	public boolean isDestroyed() {
		return false;
	}

	public void setDoAfterListener(DoAfterListener paramDoAfterListener) {
		this.doAfterListener = paramDoAfterListener;
		this.mLocClient.requestLocation();
	}

	public static abstract interface DoAfterListener {
		public abstract void onDoAfter(BDLocation paramBDLocation);
	}

	class MyLocationListener implements BDLocationListener {
		MyLocationListener() {
		}

		public void onReceiveLocation(BDLocation paramBDLocation) {
			MyLocation.this.onDoAfter(paramBDLocation);
		}

		public void onReceivePoi(BDLocation paramBDLocation) {
		}
	}
}