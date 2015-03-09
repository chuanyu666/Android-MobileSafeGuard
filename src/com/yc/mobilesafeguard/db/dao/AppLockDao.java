package com.yc.mobilesafeguard.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yc.mobilesafeguard.db.AppLockDBOpenHelper;

public class AppLockDao {
	
	private AppLockDBOpenHelper helper;
	private Context context;
	
	public AppLockDao(Context context){
		helper = new AppLockDBOpenHelper(context);
		this.context = context;
	}
	
	public void add(String packageName){
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("packagename", packageName);
		db.insert("applock", null, values);
		db.close();
		Intent intent = new Intent();
		intent.setAction("com.yc.mobilesafeguard.applockchange");
		context.sendBroadcast(intent);
	}
	
	public void delete(String packageName){
		SQLiteDatabase db = helper.getWritableDatabase();
		db.delete("applock", "packagename=?", new String[]{packageName});
		db.close();
		Intent intent = new Intent();
		intent.setAction("com.yc.mobilesafeguard.applockchange");
		context.sendBroadcast(intent);
	}
	
	public boolean query(String packageName){
		boolean result = false;
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query("applock", null, "packagename=?", new String[]{packageName}, null, null, null);
		if(cursor.moveToNext()){
			result = true;
		}
		cursor.close();
		db.close();
		return result;
	}
	
	public List<String> queryAll(){
		List<String> result = new ArrayList<String>();
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query("applock", new String[]{"packagename"}, null, null, null, null, null);
		while(cursor.moveToNext()){
			String name = cursor.getString(0);
			result.add(name);
		}
		cursor.close();
		db.close();
		return result;
	}
	
}
