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
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);//打开GPS
		option.setPriority(LocationClientOption.GpsFirst);//优先使用GPS定位,提高准确度
		option.setCoorType("bd09ll");
		option.setAddrType("all");//设置获取地址的详细信息
		option.setProdName("厦门实时公交查询");
		mLocClient.setLocOption(option);
		mLocClient.start();
	}
	/**
	 * 定位监听函数
	 */
	class MyLocationListener implements BDLocationListener {
		MyLocationListener() {
		}

		public void onReceiveLocation(BDLocation bDLocation) {
			onDoAfter(bDLocation);
		}

		public void onReceivePoi(BDLocation paramBDLocation) {
		}
	}
	/**
	 * 设置回调函数
	 */
	public void setDoAfterListener(DoAfterListener doAfterListener) {
		this.doAfterListener = doAfterListener;
		this.mLocClient.requestLocation();
	}
	public interface DoAfterListener {
		public abstract void onDoAfter(BDLocation bDLocation);
	}
	
	private void onDoAfter(BDLocation bDLocation) {
		if (this.doAfterListener != null){
			this.doAfterListener.onDoAfter(bDLocation);
		}
	}

	@Override
	public void destroy() {
		if (this.mLocClient != null) {
			this.mLocClient.stop();
			this.mLocClient = null;
		}
	}
	@Override
	public boolean isDestroyed() {
		return false;
	}
}