package com.yc.mobilesafeguard.activity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PackageStats;
import android.os.Bundle;
import android.os.Process;
import android.os.RemoteException;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.yc.mobilesafeguard.R;

public class CleanCacheActivity extends Activity {

	private ProgressBar pb_clean_cache;
	private TextView tv_status;
	private PackageManager pm;
	private LinearLayout ll_cache;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clean_cache);
		pm = getPackageManager();
		ll_cache = (LinearLayout) findViewById(R.id.ll_cache);
		pb_clean_cache = (ProgressBar) findViewById(R.id.pb_clean_cache);
		tv_status = (TextView) findViewById(R.id.tv_status);

		scanCache();
	}

	private void scanCache() {
		new Thread() {
			public void run() {
				Method getSizeInfoMethod = null;
				Method[] methods = PackageManager.class.getMethods();
				for (Method method : methods) {
					if ("getPackageSizeInfo".equals(method.getName())) {
						getSizeInfoMethod = method;
					}
				}
				List<PackageInfo> infos = pm.getInstalledPackages(0);
				pb_clean_cache.setMax(infos.size());
				int progress = 0;
				System.out.println(Process.myUid()/100000);
				for (PackageInfo info : infos) {
					try {
						getSizeInfoMethod.invoke(pm, info.packageName,Process.myUid()/100000, new MyDataObserver());
						Thread.sleep(100);
					} catch (Exception e) {
						e.printStackTrace();
					}
					progress++;
					pb_clean_cache.setProgress(progress);
				}
				runOnUiThread(new Runnable() {
					public void run() {
						tv_status.setText("complete ");
					}
				});
			};
		}.start();
	}

	private class MyDataObserver extends IPackageStatsObserver.Stub {

		@Override
		public void onGetStatsCompleted(PackageStats pStats, boolean succeeded)
				throws RemoteException {
			final long cache = pStats.cacheSize;
			final String packname = pStats.packageName;
			try {
				final ApplicationInfo info = pm.getApplicationInfo(packname, 0);
				runOnUiThread(new Runnable() {
					public void run() {
						tv_status.setText("scanning: " + info.loadLabel(pm));
						if (cache > 0) {
							View view = View.inflate(getApplicationContext(),
									R.layout.list_item_appcache, null);
							TextView tv_app_name = (TextView) view
									.findViewById(R.id.tv_app_name);
							tv_app_name.setText(info.loadLabel(pm));
							TextView tv_app_cache = (TextView) view
									.findViewById(R.id.tv_app_cache);
							tv_app_cache.setText("Cache: "
									+ Formatter.formatFileSize(
											getApplicationContext(), cache));
							ImageView iv_app_icon = (ImageView) view
									.findViewById(R.id.iv_app_icon);
							iv_app_icon.setImageDrawable(info.loadIcon(pm));
//							ImageView iv_delete = (ImageView) findViewById(R.id.iv_app_clean);
//							iv_delete.setOnClickListener(new OnClickListener() {
//								@Override
//								public void onClick(View v) {
//									try {
//										Method method = PackageManager.class
//												.getMethod(
//														"deleteApplicationCacheFiles",
//														String.class,
//														IPackageDataObserver.class);
//										method.invoke(pm, packname, new MyPackDataObserver());
//									} catch ( Exception e) {
//										e.printStackTrace();
//									}
//								} //android.permission.DELETE_CACHE_FILES is only granted to system apps
//							}); 
							ll_cache.addView(view, 0);
						}
					}
				});
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	 private class MyPackDataObserver extends IPackageDataObserver.Stub{

		@Override
		public void onRemoveCompleted(String packageName, boolean succeeded)
				throws RemoteException {
			runOnUiThread(new Runnable() {
				public void run() {
					ll_cache.removeAllViews();
				}
			});
		}
		 
	 }
	 
	 public void clearAll(View view){
		Method[] methods =  PackageManager.class.getMethods();
		for(Method method:methods){
			if("freeStorageAndNotify".equals(method.getName())){
				try {
					method.invoke(pm, Integer.MAX_VALUE,new MyPackDataObserver());
					Toast.makeText(this, "clear complete", Toast.LENGTH_LONG).show();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}
	 }
}
