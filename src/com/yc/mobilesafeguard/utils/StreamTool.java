package com.yc.mobilesafeguard.utils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class StreamTool {
	public static String readInputStream(InputStream is) {
		try {

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			int length = 0;
			byte[] buffer = new byte[1024];
			if ((length = is.read(buffer)) != -1) {
				out.write(buffer, 0, length);
			}
			byte[] result = out.toByteArray();
			String temp = new String(result);
			if(temp.contains("utf-8")){
				return temp;
			}else if(temp.contains("gb2312")){
				return new String(result,"gb2312");
			}
			return temp; 
		} catch (Exception e) {
			// TODO: handle exception
			return "fail";
		}
	}

}
