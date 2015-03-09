package com.yc.mobilesafeguard.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.yc.mobilesafeguard.R;
import com.yc.mobilesafeguard.service.AutoCleanProcessService;
import com.yc.mobilesafeguard.utils.ServiceUtils;

public class ProcessSettingActivity extends Activity {
	private CheckBox cb_display_syspro;
	private CheckBox cb_auto_clean;
	private SharedPreferences sp;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_process_setting);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		cb_display_syspro = (CheckBox) findViewById(R.id.cb_display_syspro);
		cb_auto_clean = (CheckBox) findViewById(R.id.cd_auto_clean);
		cb_display_syspro.setChecked(sp.getBoolean("displaySysPro", false));
		cb_display_syspro.setOnCheckedChangeListener(new OnCheckedChangeListener() {			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Editor editor = sp.edit();
				editor.putBoolean("displaySysPro", isChecked);
				editor.commit();
			}
		});
		
		cb_auto_clean.setOnCheckedChangeListener(new OnCheckedChangeListener() {		
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Intent intent = new Intent(ProcessSettingActivity.this, AutoCleanProcessService.class);
				if(isChecked){
					startService(intent);
				}else {
					stopService(intent);
				}
			}
		});
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		boolean running = ServiceUtils.serviceIsAlive(this, "com.yc.mobilesafeguard.service.AutoCleanProcessService");
		cb_auto_clean.setChecked(running);
	}
}	
