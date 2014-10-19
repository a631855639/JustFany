package com.xm.bus.common.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpDownloader {
	
	public String download(String urlStr){
		StringBuffer sb=new StringBuffer();
		String line="";
		BufferedReader br=null;
		try {
			URL url=new URL(urlStr);
			HttpURLConnection urlConn=(HttpURLConnection) url.openConnection();
			urlConn.setConnectTimeout(5000);
			urlConn.setRequestMethod("GET");
			urlConn.setDoInput(true);
			urlConn.connect();
			br = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			br.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
	
	public InputStream getInputStreamFromUrl(String urlStr) throws Exception{
		URL url = new URL(urlStr);
		HttpURLConnection urlConn=(HttpURLConnection) url.openConnection();
		urlConn.setConnectTimeout(5000);
		urlConn.setRequestMethod("GET");
		urlConn.setDoInput(true);
		urlConn.connect();
		InputStream inputStream=urlConn.getInputStream();
		return inputStream;
	}
}
