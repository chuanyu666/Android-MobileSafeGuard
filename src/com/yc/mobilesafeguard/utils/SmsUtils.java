package com.yc.mobilesafeguard.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Xml;

public class SmsUtils {

	public static void backupSms(Context context,ProgressDialog pd) throws Exception{
		File file = new File(Environment.getExternalStorageDirectory(), "backupsms.xml");
		FileOutputStream fos = new FileOutputStream(file);
		ContentResolver resolver = context.getContentResolver();
		XmlSerializer serializer = Xml.newSerializer();
		serializer.setOutput(fos, "UTF-8");
		serializer.startDocument("UTF-8", true);
		serializer.startTag(null, "smss");
		Cursor cursor = resolver.query(Uri.parse("content://sms/"),new String[]{"body","address","type","date"} , null, null, null);
		int max = cursor.getCount();
		pd.setMax(max);
		serializer.attribute(null, "max", max+"");
		int progress = 0;
		while(cursor.moveToNext()){
			String body = cursor.getString(0); 
			String address = cursor.getString(1); 
			String type = cursor.getString(2); 
			String date = cursor.getString(3); 
			serializer.startTag(null, "sms");
			serializer.startTag(null, "body");
			serializer.text(body);
			serializer.endTag(null, "body");
			
			serializer.startTag(null, "address");
			serializer.text(address);
			serializer.endTag(null, "address");
			
			serializer.startTag(null, "type");
			serializer.text(type);
			serializer.endTag(null, "type");
			
			serializer.startTag(null, "date");
			serializer.text(date);
			serializer.endTag(null, "date");
			serializer.endTag(null, "sms");
			progress++;
			pd.setProgress(progress);
		}
		serializer.endTag(null, "smss");  
		serializer.endDocument();
		fos.close();
	}
	
	public interface restoreSmsCallBack{
		public void beforeRestore(int max);
		public void onRestore(int progress);
	}
	
	public static void restoreSms(Context context,boolean flag,restoreSmsCallBack callBack) throws Exception {
		Uri uri = Uri.parse("content://sms/");	
		ContentResolver resolver = context.getContentResolver();
		if(flag){
			resolver.delete(uri, null, null);
		}
		File file  = new File(Environment.getExternalStorageDirectory(), "backupsms.xml");
		FileInputStream fis = new FileInputStream(file);
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(fis, "UTF-8");
		int max = 0;
		int type = parser.getEventType();
		int progress = 0;
		ContentValues values = null; 
		while(type!=XmlPullParser.END_DOCUMENT){			
			switch (type) {
			case XmlPullParser.START_TAG:
				if("smss".equals(parser.getName())){
					max = Integer.parseInt(parser.getAttributeValue(0));
					callBack.beforeRestore(max);
				}else if("sms".equals(parser.getName())){
					values = new ContentValues();
				}else if("address".equals(parser.getName())){
					String address = parser.nextText();
					values.put("address", address);
				}
				else if("type".equals(parser.getName())){
					String smsType = parser.nextText();
					values.put("type", smsType);
				}else if("date".equals(parser.getName())){
					String date = parser.nextText();
					values.put("date", date);
				}else if("body".equals(parser.getName())){
					String body = parser.nextText();
					values.put("body", body);
				}
				break;
			case XmlPullParser.END_TAG:
				if("sms".equals(parser.getName())){
					resolver.insert(uri, values);
					progress++;
					callBack.onRestore(progress);
				}
				break;
			}
			type = parser.next();
		}
	}
}
