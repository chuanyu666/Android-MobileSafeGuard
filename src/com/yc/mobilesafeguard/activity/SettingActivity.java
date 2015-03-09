package com.yc.mobilesafeguard.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.yc.mobilesafeguard.R;
import com.yc.mobilesafeguard.service.CallSMSService;
import com.yc.mobilesafeguard.service.NumberLocationService;
import com.yc.mobilesafeguard.service.WatchDogService;
import com.yc.mobilesafeguard.ui.SettingClickView;
import com.yc.mobilesafeguard.ui.SettingItemView;
import com.yc.mobilesafeguard.utils.ServiceUtils;

public class SettingActivity extends Activity {
	private SettingItemView siv_update;
	private SettingItemView siv_location_service;
	private SharedPreferences sp;
	private Intent showLocation;
	private SettingClickView scv_select_view;
	private SettingItemView siv_black_number;
	private SettingItemView siv_watchdog;
	private Intent openBlackNumber;
	private Intent watchdogIntent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		siv_update = (SettingItemView) findViewById(R.id.siv_update);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		boolean update = sp.getBoolean("update", false);
		if(update){
			siv_update.setChecked(true);
	//		siv_update.setDescription("Automatically update on");
		}else {
			siv_update.setChecked(false);
	//		siv_update.setDescription("Automatically update off");
		}
		siv_update.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				Editor editor = sp.edit();
				if(siv_update.isChecked()){
					siv_update.setChecked(false);
	//				siv_update.setDescription("Automatically update off");
					editor.putBoolean("update", false);
				}else{
					siv_update.setChecked(true);
	//				siv_update.setDescription("Automatically update on");
					editor.putBoolean("update", true);
				}
				editor.commit();
			}
		});
		
		siv_location_service = (SettingItemView) findViewById(R.id.siv_location_service);
		showLocation = new Intent(this,NumberLocationService.class);
		boolean isAlive = ServiceUtils.serviceIsAlive(this, "com.yc.mobilesafeguard.service.NumberLocationService");
		if(isAlive){
			siv_location_service.setChecked(true);
		}else{
			siv_location_service.setChecked(false);
		}
		siv_location_service.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(siv_location_service.isChecked()){
					siv_location_service.setChecked(false);
					stopService(showLocation);
				}else{
					siv_location_service.setChecked(true);
					startService(showLocation);
				}
			}
		});
		scv_select_view = (SettingClickView) findViewById(R.id.scv_select_view);
		final String[] items = {"Transparent","Orange","Blue","Grey","Green"};
		int select = sp.getInt("which", 0);
		scv_select_view.setDescription(items[select]);
		scv_select_view.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				int s = sp.getInt("which", 0);
				AlertDialog.Builder builder = new Builder(SettingActivity.this);
				builder.setTitle("select number location display theme");
				builder.setSingleChoiceItems(items, s, new DialogInterface.OnClickListener() {					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Editor editor = sp.edit();
						editor.putInt("which", which);
						editor.commit();
						scv_select_view.setDescription(items[which]);
						dialog.dismiss();
					}
				});
				
				builder.show();
			}
		});
		
		openBlackNumber = new Intent(this, CallSMSService.class);
		siv_black_number = (SettingItemView) findViewById(R.id.siv_black_number);
		siv_black_number.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(siv_black_number.isChecked()){
					siv_black_number.setChecked(false);
					stopService(openBlackNumber);
				}else{
					siv_black_number.setChecked(true);
					startService(openBlackNumber);
				}
			}
		});
		
		watchdogIntent = new Intent(this, WatchDogService.class);
		siv_watchdog = (SettingItemView) findViewById(R.id.siv_watchdog);
		siv_watchdog.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				if(siv_watchdog.isChecked()){
					siv_watchdog.setChecked(false);
					stopService(watchdogIntent);
				}else{
					siv_watchdog .setChecked(true);
					startService(watchdogIntent);
				}
			}
		});
	} 
	
	@Override
	protected void onResume() {
		super.onResume();
		boolean isAlive = ServiceUtils.serviceIsAlive(this, "com.yc.mobilesafeguard.service.NumberLocationService");
		if(isAlive){
			siv_location_service.setChecked(true);
		}else{
			siv_location_service.setChecked(false);
		}
		
		boolean isCallSmsAlive = ServiceUtils.serviceIsAlive(this, "com.yc.mobilesafeguard.service.CallSMSService");
		if(isCallSmsAlive){
			siv_black_number.setChecked(true);
		}else{
			siv_black_number.setChecked(false);
		}
		
		boolean isWatchDogAlive = ServiceUtils.serviceIsAlive(this, "com.yc.mobilesafeguard.service.WatchDogService");
		if(isWatchDogAlive){
			siv_watchdog.setChecked(true);
		}else{
			siv_watchdog.setChecked(false);
		}
	}
}
