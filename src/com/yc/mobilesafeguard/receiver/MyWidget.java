package com.yc.mobilesafeguard.receiver;

import com.yc.mobilesafeguard.service.UpdateWidgetService;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

public class MyWidget extends AppWidgetProvider {
	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		Intent intent2 = new Intent(context, UpdateWidgetService.class);
		context.startService(intent2);
	}
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		System.out.println("update");
	}
	
	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
		Intent intent = new Intent(context, UpdateWidgetService.class);
		context.startService(intent);
	}
	
	@Override
	public void onDisabled(Context context) {
		super.onDisabled(context);
		Intent intent = new Intent(context, UpdateWidgetService.class);
		context.stopService(intent);
	}
	
}
