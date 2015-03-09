package com.yc.mobilesafeguard.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yc.mobilesafeguard.R;

public class SafetyActivity extends Activity {
	
	private SharedPreferences sp;
	private TextView tv_safe_phone;
	private ImageView iv_safe;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		sp = getSharedPreferences("config",MODE_PRIVATE);
		boolean configed = sp.getBoolean("configured", false);
		if(configed){
			setContentView(R.layout.activity_safety ); 
			tv_safe_phone = (TextView) findViewById(R.id.tv_safe_phone);
			iv_safe = (ImageView) findViewById(R.id.iv_safe);
			String safePhone = sp.getString("phone", "");
			tv_safe_phone.setText(safePhone);
			boolean protection = sp.getBoolean("protection", false);
			if(protection){
				iv_safe.setImageResource(R.drawable.lock);
			}else{
				iv_safe.setImageResource(R.drawable.unlock);
			}
		}else {
			Intent intent = new Intent(this, Setup1Activity.class);
			startActivity(intent);
			finish();
		}
	}
	
	public void reenter(View view){
		Intent intent = new Intent(this, Setup1Activity.class);
		startActivity(intent);
		finish();
	}
}
