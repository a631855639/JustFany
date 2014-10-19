package com.xm.bus.common.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.ProgressDialog;

public class DownloadTask {
	
	public static File getFile(String urlPath,String filePath,ProgressDialog progressDialog) throws Exception{
		URL url=new URL(urlPath);
		HttpURLConnection httpURLConnection=(HttpURLConnection) url.openConnection();
		httpURLConnection.setConnectTimeout(2000);
		httpURLConnection.setRequestMethod("GET");
		httpURLConnection.setDoInput(true);
		if(httpURLConnection.getResponseCode()==200){
			int total=httpURLConnection.getContentLength();
			progressDialog.setMax(total);
			
			InputStream is=httpURLConnection.getInputStream();
			File file=new File(filePath);
			if(!file.exists()){
				file.createNewFile();
			}
			FileOutputStream fos=new FileOutputStream(file);
			byte[] buffer=new byte[1024];
			int len=0;
			int process=0;
			while((len=is.read(buffer))!=-1){
				fos.write(buffer,0,len);
				process+=len;
				progressDialog.setProgress(process);
			}
			fos.flush();
			fos.close();
			is.close();
			return file;
		}
		return null;
	}
}
