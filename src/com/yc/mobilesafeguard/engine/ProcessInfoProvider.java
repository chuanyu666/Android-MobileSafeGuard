package com.yc.mobilesafeguard.engine;

import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Debug.MemoryInfo;

import com.yc.mobilesafeguard.R;
import com.yc.mobilesafeguard.domain.ProcessInfo;


public class ProcessInfoProvider {
		
	public static List<ProcessInfo> getProcessInfo(Context context){
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		PackageManager pm = context.getPackageManager();
		List<RunningAppProcessInfo> infos = am.getRunningAppProcesses();
		List<ProcessInfo> processInfos = new ArrayList<ProcessInfo>();
		for(RunningAppProcessInfo info:infos){ 
			ProcessInfo processInfo = new ProcessInfo();
			String packageName = info.processName;
			processInfo.setPackageName(packageName);
			MemoryInfo[] memoryInfos = am.getProcessMemoryInfo(new int[]{info.pid});
			long memorySize = memoryInfos[0].getTotalPrivateDirty()*1024;
			processInfo.setMomeryUsed(memorySize);
			try {
				ApplicationInfo applicationInfo = pm.getApplicationInfo(packageName, 0);
				Drawable icon = applicationInfo.loadIcon(pm);
				processInfo.setIcon(icon);
				String name = applicationInfo.loadLabel(pm).toString();
				processInfo.setName(name);
				if((applicationInfo.flags&ApplicationInfo.FLAG_SYSTEM)==0){
					processInfo.setUserProcess(true);
				}else {
					processInfo.setUserProcess(false);
				}
				
			} catch (NameNotFoundException e) {
				processInfo.setIcon(context.getResources().getDrawable(R.drawable.ic_default));
				processInfo.setName(packageName);
				e.printStackTrace();
			}
			processInfos.add(processInfo);
		}		
		return processInfos;
	}
	
}
