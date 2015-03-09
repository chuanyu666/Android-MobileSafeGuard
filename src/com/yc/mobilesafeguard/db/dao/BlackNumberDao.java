package com.yc.mobilesafeguard.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yc.mobilesafeguard.db.BlackNumberDBOpenHelper;
import com.yc.mobilesafeguard.domain.BlackNumberInfo;

public class BlackNumberDao {

	private BlackNumberDBOpenHelper helper;

	public BlackNumberDao(Context context) {
		helper = new BlackNumberDBOpenHelper(context);
	}

	public boolean find(String number) {
		boolean result = false;
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select* from blacknumber where number=?",
				new String[] { number });
		while (cursor.moveToNext()) {
			result = true;
		}
		cursor.close();
		db.close();
		return result;
	}

	public String findMode(String number) {
		String result = null;
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery(
				"select mode from blacknumber where number=?",
				new String[] { number });
		while (cursor.moveToNext()) {
			result = cursor.getString(0);
		}
		cursor.close();
		db.close();
		return result;
	}

	public List<BlackNumberInfo> findAll() {
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		List<BlackNumberInfo> result = new ArrayList<BlackNumberInfo>();
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery(
				"select* from blacknumber order by id desc", null);
		while (cursor.moveToNext()) {
			String number = cursor.getString(cursor.getColumnIndex("number"));
			String mode = cursor.getString(cursor.getColumnIndex("mode"));
			BlackNumberInfo info = new BlackNumberInfo();
			info.setNumber(number);
			info.setMode(mode);
			result.add(info);
		}
		cursor.close();
		db.close();
		return result;
	}

	public List<BlackNumberInfo> findPart(int offset, int maxnumber) {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		List<BlackNumberInfo> result = new ArrayList<BlackNumberInfo>();
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery(
				"select* from blacknumber order by id desc limit ? offset ?",
				new String[] { String.valueOf(maxnumber),
						String.valueOf(offset) });
		while (cursor.moveToNext()) {
			String number = cursor.getString(cursor.getColumnIndex("number"));
			String mode = cursor.getString(cursor.getColumnIndex("mode"));
			BlackNumberInfo info = new BlackNumberInfo();
			info.setNumber(number);
			info.setMode(mode);
			result.add(info);
		}
		cursor.close();
		db.close();
		return result;
	}

	public void insert(String number, String mode) {
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("number", number);
		values.put("mode", mode);
		db.insert("blacknumber", null, values);
		db.close();
	}

	public void modify(String number, String newMode) {
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("mode", newMode);
		db.update("blacknumber", values, "number=?", new String[] { number });
		db.close();
	}

	public void delete(String number) {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.delete("blacknumber", "number=?", new String[] { number });
		db.close();
	}
}
