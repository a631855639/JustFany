package com.xm.bus.common.utils;

import java.io.File;
import java.io.InputStream;

import com.xm.bus.common.Constant;
import com.xm.bus.common.model.VersionInfo;
import com.xm.bus.common.service.HttpDownloader;


import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

public class DownUtil {
	/**
	 * 获取系统的版本号
	 * @param context
	 * @return
	 */
	public static String getVersion(Context context){
		try {
			PackageManager packageManager=context.getPackageManager();
			PackageInfo packageInfo=packageManager.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return "版本未知";
		}
	}
	/**
	 * 创建文件夹
	 * @param filePath
	 */
	public static void createFile(String filePath){
		File dir=new File(Environment.getExternalStorageDirectory(), filePath);
		if(!dir.exists()){
			dir.mkdirs();
		}
	}
	/**
	 * 检测更新
	 * @param handler 消息处理
	 */
	public static void updateCheck(final Handler handler){
		new Thread(){
			@Override
			public void run() {
				while(!Thread.currentThread().isInterrupted()){
					HttpDownloader downloader=new HttpDownloader();
					VersionInfo info=null;
					try {
						//String url=getResources().getString(R.string.server_url);
						InputStream is=downloader.getInputStreamFromUrl(Constant.DOWNLOAD_URL);
						//VersionInfo info=XMLPullParser.getVersionInfo(is);//pull解析
						info=XMLSaxParser.getVersionInfo(is);//sax解析
					} catch (Exception e) {
						e.printStackTrace();
					}
					Message msg=Message.obtain();
					msg.obj=info;
					msg.arg1=1;
					handler.sendMessage(msg);
					Thread.currentThread().interrupt();
				}
			}
		};
	}
}
