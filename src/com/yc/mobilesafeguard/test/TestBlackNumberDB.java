package com.yc.mobilesafeguard.test;

import java.util.List;
import java.util.Random;

import com.yc.mobilesafeguard.db.BlackNumberDBOpenHelper;
import com.yc.mobilesafeguard.db.dao.BlackNumberDao;
import com.yc.mobilesafeguard.domain.BlackNumberInfo;

import android.test.AndroidTestCase;

public class TestBlackNumberDB extends AndroidTestCase {
	
	public void createDB() throws Exception{
		BlackNumberDBOpenHelper helper = new BlackNumberDBOpenHelper(getContext());
		helper.getWritableDatabase();
	}
	
	
	public void insert() throws Exception{
		BlackNumberDao dao = new BlackNumberDao(getContext());
		
		Random random = new Random();
		for(int i=10;i<=50;i++){
			dao.insert("86237190"+i, random.nextInt(3)+1+"");			
		}
	}
	
	public void findAll() throws Exception{
		BlackNumberDao dao = new BlackNumberDao(getContext());
		List<BlackNumberInfo> infos = dao.findAll();
		for(BlackNumberInfo info : infos){
			System.out.println(info.getNumber());
		}
	}
	
	public void find() throws Exception{
		BlackNumberDao dao = new BlackNumberDao(getContext());
		boolean resulet = dao.find("86237190");
		assertEquals(true, resulet);
	}
}
