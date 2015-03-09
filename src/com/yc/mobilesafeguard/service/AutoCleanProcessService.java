package com.yc.mobilesafeguard.service;

import java.util.List;

import android.app.ActivityManager;
import android.app.Service;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

public class AutoCleanProcessService extends Service {

	private ScreenOffReceiver receiver;
	private ActivityManager am;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		super.onCreate();
		receiver = new ScreenOffReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		registerReceiver(receiver, filter);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
		receiver = null;
	}
	
	private class ScreenOffReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			List<RunningAppProcessInfo> infos = am.getRunningAppProcesses();
			for(RunningAppProcessInfo info:infos){
				am.killBackgroundProcesses(info.processName);
			}
		}
		
	}
}
