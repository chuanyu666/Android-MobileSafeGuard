package com.yc.mobilesafeguard.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

public class ServiceUtils {
	
	public static boolean serviceIsAlive(Context context,String serviceName){
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> infos = am.getRunningServices(100);
		for(RunningServiceInfo info:infos){
			String name = info.service.getClassName();
			if(serviceName.equals(name)){
				return true;
			}
		}
		return false;
	}
	
}
