package com.xm.bus.search.utils;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class WebService {

	public static byte[] getImage(String path) {
		URL url = null;
		byte[] b = null;
		try {
			url = new URL(path);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setReadTimeout(3000);
			InputStream is = conn.getInputStream();
			b = StreamTool.readInputStream(is);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return b;
	}
}