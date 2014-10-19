package com.xm.bus.common.utils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.Matrix;

public class WebService {
	/**
	 * 从网络上获取加载图片并显示
	 * @param path
	 * @return
	 */
	public static byte[] getImage(String path) {
		URL url = null;
		byte[] b = null;
		try {
			url = new URL(path);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setReadTimeout(3000);
			InputStream is = conn.getInputStream();
			b = readInputStream(is);
			if(is!=null){
				is.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return b;
	}
	
	/**
	 * 通过输入流得到字节数组
	 * @param is
	 * @return
	 * @throws Exception
	 */
	public static byte[] readInputStream(InputStream is) throws Exception {
		int temp = 0;
		byte[] buffer = new byte[1024];
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		while ((temp = is.read(buffer)) != -1) {
			out.write(buffer, 0, temp);
		}
		out.close();
		is.close();
		return out.toByteArray();
	}
	/**
	 * 
	 * @param b
	 * @param newWidth
	 * @param newHeight
	 * @return 图片缩放
	 */
	public static Bitmap zoomImage(Bitmap b,int newWidth,int newHeight){
		int h=b.getHeight();
		int w=b.getWidth();
		float scaleWidth = newWidth/w;
		float scaleHeight=newHeight/h;
		Matrix matrix=new Matrix();
		matrix.postScale(scaleWidth, scaleWidth);//高度缩放的比例与宽度缩放比例一样
		Bitmap bitmap=Bitmap.createBitmap(b, 0, 0, w, h, matrix, true);
		return bitmap;
	}
}