package com.xm.bus.location.common;

import android.content.Context;
import android.widget.Toast;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.MKEvent;
import com.xm.bus.search.common.SearchApp;

public class LocationApplication extends SearchApp {
	private static LocationApplication mInstance = null;
    public boolean m_bKeyRight = true;
    public BMapManager mBMapManager = null;

    public static final String strKey = "pGMEW4fzUg34mQkKVegKcEY0";
	
	@Override
    public void onCreate() {
	    super.onCreate();
		mInstance = this;
		initEngineManager(this);
	}
	
	public void initEngineManager(Context context) {
		/**
         * 使用地图sdk前需先初始化BMapManager.
         * BMapManager是全局的，可为多个MapView共用，它需要地图模块创建前创建，
         * 并在地图地图模块销毁后销毁，只要还有地图模块在使用，BMapManager就不应该销毁
         */
        if (mBMapManager == null) {
        	/**
             * 如果BMapManager没有初始化则初始化BMapManager
             */
            mBMapManager = new BMapManager(context);
        }

        if (!mBMapManager.init(strKey,new MyGeneralListener())) {
            Toast.makeText(LocationApplication.getInstance().getApplicationContext(),"初始化错误!", Toast.LENGTH_LONG).show();
        }
	}
	
	public static LocationApplication getInstance() {
		return mInstance;
	}
	
	
	// 常用事件监听，用来处理通常的网络错误，授权验证错误等
    public static class MyGeneralListener implements MKGeneralListener {
        
        @Override
        public void onGetNetworkState(int iError) {
            if (iError == MKEvent.ERROR_NETWORK_CONNECT) {
                /*Toast.makeText(LocationApplication.getInstance().getApplicationContext(), "您的网络信号不太好！",
                    Toast.LENGTH_LONG).show();*/
            }
            else if (iError == MKEvent.ERROR_NETWORK_DATA) {
                Toast.makeText(LocationApplication.getInstance().getApplicationContext(), "输入正确的检索条件！",
                        Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onGetPermissionState(int iError) {
        	//非零值表示key验证未通过
            if (iError != 0) {
                //授权Key错误：
                LocationApplication.getInstance().m_bKeyRight = false;
            }
            else{
            	LocationApplication.getInstance().m_bKeyRight = true;
            }
        }
    }
	
}