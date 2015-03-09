package com.yc.mobilesafeguard.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.yc.mobilesafeguard.R;


public class Setup4Activity extends BaseSetupActivity {
	private SharedPreferences sp;
	private CheckBox cb_protection;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup4);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		cb_protection = (CheckBox) findViewById(R.id.cb_protection);
		
		boolean protection = sp.getBoolean("protection", false);
		if(protection){
			cb_protection.setChecked(true);
			cb_protection.setText("You have setup safety protection");
		}else{
			cb_protection.setChecked(false);
			cb_protection.setText("You have not setup safety protection");
		}
		
		cb_protection.setOnCheckedChangeListener(new OnCheckedChangeListener() {			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					cb_protection.setText("You have setup safety protection");
				}else{
					cb_protection.setText("You have not setup safety protection");
				}
				Editor editor = sp.edit();
				editor.putBoolean("protection", isChecked);
				editor.commit();
			}
		});
	}


	@Override
	public void showNext() {
		Editor editor = sp.edit();
		editor.putBoolean("configured", true);
		editor.commit();
		Intent intent = new Intent(this, SafetyActivity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.trans_in, R.anim.trans_out);
		
	}

	@Override
	public void showPrev() {
		Intent intent = new Intent(this, Setup3Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.trans_pre_in, R.anim.trans_pre_out);
	}
}
