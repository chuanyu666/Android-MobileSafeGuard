package com.yc.mobilesafeguard.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class PhoneLocationQueryUtils {
	
	private static String path="/data/data/com.yc.mobilesafeguard/files/address.db";
	
	public static String queryNumber(String number){
		String location = "";
		if(number.matches("^1[34568]\\d{9}$")){
			number = number.substring(0, 7);
		}else{
			
		}
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY); 
		Cursor cursor = db.rawQuery("select location from data2 where id=(select outkey from data1 where id=?)", new String[]{number});
		while(cursor.moveToNext()){
			location = cursor.getString(0);
		}
		cursor.close();
		return location;	
	}
	
}
