package com.yc.mobilesafeguard.service;

import java.util.Timer;
import java.util.TimerTask;

import com.yc.mobilesafeguard.R;
import com.yc.mobilesafeguard.receiver.MyWidget;
import com.yc.mobilesafeguard.utils.SystemInfoUtils;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.text.format.Formatter;
import android.widget.RemoteViews;

public class UpdateWidgetService extends Service {

	private Timer timer;
	private TimerTask timerTask;
	private AppWidgetManager awm;
	private ScreenOffReceiver offReceiver;
	private ScreenOnReceiver onReceiver;
	
	private class ScreenOffReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			System.out.println("stop timer");
			stopTimer();
		}
	}
	
	private class ScreenOnReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			System.out.println("start timer");
			startTimer();
		}
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		awm = AppWidgetManager.getInstance(this);
		onReceiver = new ScreenOnReceiver();
		offReceiver = new ScreenOffReceiver();
		registerReceiver(onReceiver, new IntentFilter(Intent.ACTION_SCREEN_ON));
		registerReceiver(offReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
		startTimer();
	}

	private void startTimer() {
		if(timer==null&&timerTask==null){
			
			timer = new Timer();
			timerTask = new TimerTask() {
				@Override
				public void run() {
					System.out.println("widget update");
					ComponentName provider = new ComponentName(
							UpdateWidgetService.this, MyWidget.class);
					RemoteViews views = new RemoteViews(getPackageName(),
							R.layout.process_widget);
					views.setTextViewText(
							R.id.process_count,
							"Running process:"
									+ SystemInfoUtils
									.getRunningProcessCount(getApplicationContext()));
					views.setTextViewText(
							R.id.process_memory,
							"Available memory:"
									+ Formatter
									.formatFileSize(
											getApplicationContext(),
											SystemInfoUtils
											.getAvailableMemory(getApplicationContext())));
					
					Intent intent = new Intent();
					intent.setAction("com.yc.mobilesafeguard.clearall");
					
					PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
					views.setOnClickPendingIntent(R.id.btn_clear, pendingIntent);
					awm.updateAppWidget(provider, views);
				}
			};
			timer.schedule(timerTask, 0, 3000);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(offReceiver);
		unregisterReceiver(onReceiver);
		offReceiver = null;
		onReceiver = null;
		stopTimer();
	}

	private void stopTimer() {
		if(timer!=null&&timerTask!=null){
			timer.cancel();
			timerTask.cancel();
			timer = null;
			timerTask = null;			
		}
	}
}
