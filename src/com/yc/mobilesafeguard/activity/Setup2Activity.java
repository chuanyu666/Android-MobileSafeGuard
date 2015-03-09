 package com.yc.mobilesafeguard.activity;

import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.yc.mobilesafeguard.R;
import com.yc.mobilesafeguard.ui.SettingItemView;

public class Setup2Activity extends BaseSetupActivity {
	private SettingItemView siv_sim;
	private TelephonyManager tm;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup2);
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
				
		siv_sim = (SettingItemView) findViewById(R.id.siv_sim);
		
		String sim = sp.getString("sim", null);
		if(TextUtils.isEmpty(sim)){
			siv_sim.setChecked(false);
		}else{
			siv_sim.setChecked(true);
		}
		
		siv_sim.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				Editor editor = sp.edit();
				if(siv_sim.isChecked()){
					siv_sim.setChecked(false);
					editor.putString("sim", null);
				}else{
					siv_sim.setChecked(true);
					String sim = tm.getSimSerialNumber(); 
					editor.putString("sim", sim);
				}
				editor.commit();
			}
		});
	}
	

	@Override
	public void showNext() {
		String sim = sp.getString("sim", null);
		if(TextUtils.isEmpty(sim)){
			Toast.makeText(this, "You have to bind your sim card", Toast.LENGTH_SHORT).show();
			return;
		}
		Intent intent = new Intent(this, Setup3Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.trans_in, R.anim.trans_out);		
	}

	@Override
	public void showPrev() {
		Intent intent = new Intent(this, Setup1Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.trans_pre_in, R.anim.trans_pre_out);	
	}
}
