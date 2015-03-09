package com.yc.mobilesafeguard.db.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class AntiVirusDao {
	
	public static boolean isVirus(String md5){
		boolean result = false;
		String path = "/data/data/com.yc.mobilesafeguard/files/antivirus.db";
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
		Cursor cursor = db.rawQuery("select* from datable where md5=?", new String[]{md5});
		if(cursor.moveToNext()){
			result = true;
		}
		cursor.close();
		db.close();
		return result;
	}
	
	public static void addVirus(String md5){
		String path = "/data/data/com.yc.mobilesafeguard/files/antivirus.db";
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READWRITE);
		ContentValues values = new ContentValues();
//		values.put("_id", 9999);
		values.put("md5", md5);
//		values.put("type", 6);
//		values.put("name", "test virus");
//		values.put("desc", "adasdasdasd");
	//	db.insert("datable", null, values);	
		db.update("datable", values, "_id=?", new String[]{"9999"});
		db.close();
	}
}
