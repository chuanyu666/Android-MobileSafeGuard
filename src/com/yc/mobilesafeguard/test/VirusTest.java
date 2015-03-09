package com.yc.mobilesafeguard.test;

import com.yc.mobilesafeguard.db.dao.AntiVirusDao;

import android.test.AndroidTestCase;

public class VirusTest extends AndroidTestCase {
	//02-04 17:16:46.786: I/System.out(1985): LockScreen:7523da777516d39ffcad8674ae878e45

	public void testVirus(){
		AntiVirusDao.addVirus("7523da777516d39ffcad8674ae878e45");
	
	}
	
	public void testExist(){
		boolean result = AntiVirusDao.isVirus("8a2aa2096301b0a89f8f04929a07e4f4");
		System.out.println(result);
	}
}
