package com.xm.bus.search.utils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class StreamTool {
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
}