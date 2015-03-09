package com.yc.mobilesafeguard.engine;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.yc.mobilesafeguard.domain.AppInfo;

public class AppInfoProvider {

	public static List<AppInfo> getAppInfos(Context context) {
		PackageManager pm = context.getPackageManager();
		List<PackageInfo> packageInfos = pm.getInstalledPackages(0);
		List<AppInfo> appList = new ArrayList<AppInfo>();
		for (PackageInfo info : packageInfos) {
			AppInfo appInfo = new AppInfo();
			String packageName = info.packageName;
			Drawable icon = info.applicationInfo.loadIcon(pm);
			String name = info.applicationInfo.loadLabel(pm).toString();
			int uid = info.applicationInfo.uid;
			appInfo.setUid(uid);
//			File rcvFile = new File("/proc/uid_stat/"+uid+"/tcp_rcv");
//			File sndFile = new File("/proc/uid_stat/"+uid+"/tcp_snd");
			int flags = info.applicationInfo.flags;  //应用程序信息的标记
			if ((flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
				appInfo.setUserApp(true);
			} else {
				appInfo.setUserApp(false);
			}
			if ((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == 0) {
				appInfo.setInRom(true);
			} else {
				appInfo.setInRom(false);
			}
			
			appInfo.setName(name);
			appInfo.setPackageName(packageName);
			appInfo.setIcon(icon);
			appList.add(appInfo);
		}
		return appList;
	}

}
