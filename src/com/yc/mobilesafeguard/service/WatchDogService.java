package com.yc.mobilesafeguard.service;

import java.util.List;

import com.yc.mobilesafeguard.activity.AppLockPwdActivity;
import com.yc.mobilesafeguard.db.dao.AppLockDao;
import android.app.ActivityManager;
import android.app.Service;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

public class WatchDogService extends Service {
	private ActivityManager am;
	private boolean flag; 
	private AppLockDao dao;
	private InnerReceiver innerReceiver;
	private String tempStopPackageName;
	private ScreenOffReceiver offReceiver;
	private Intent intent;
	private List<String> packageNames;
	private appLockChangeReceiver appLockChangeReceiver;
	
	private class ScreenOffReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			tempStopPackageName = null;
		}
	}
	
	private class InnerReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			tempStopPackageName = intent.getStringExtra("packagename");
		}
		
	}
	
	private class appLockChangeReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			packageNames = dao.queryAll();
		}	
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		appLockChangeReceiver = new appLockChangeReceiver();
		registerReceiver(appLockChangeReceiver, new IntentFilter("com.yc.mobilesafeguard.applockchange"));
		innerReceiver = new InnerReceiver();
		registerReceiver(innerReceiver, new IntentFilter("com.yc.mobilesafeguard.temstop"));
		offReceiver = new ScreenOffReceiver();
		registerReceiver(offReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
		dao = new AppLockDao(this);
		packageNames = dao.queryAll();
		am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);		
		flag = true;
		intent = new Intent(getApplicationContext(), AppLockPwdActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		new Thread(){
			public void run() {
				while(flag){
					List<RunningTaskInfo> infos = am.getRunningTasks(1);
					String packageName = infos.get(0).topActivity.getPackageName(); //当前操作的程序的包名
				//	if(dao.query(packageName)){
					if(packageNames.contains(packageName) ){ //query from memory is faster than from database
						if(packageName.equals(tempStopPackageName)){
							
						}else {						
							intent.putExtra("packagename", packageName); 
							startActivity(intent);							
						}
					}
					try {
						Thread.sleep(20);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			};
		}.start();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		flag = false; 
		unregisterReceiver(innerReceiver);
		innerReceiver = null;
		unregisterReceiver(offReceiver);
		offReceiver = null;
		unregisterReceiver(appLockChangeReceiver);
		appLockChangeReceiver = null;
	}
}
