package com.yc.mobilesafeguard.activity;

import com.yc.mobilesafeguard.R;
import com.yc.mobilesafeguard.db.dao.PhoneLocationQueryUtils;

import android.app.Activity;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class NumberLocationActivity extends Activity {
	
	private EditText et_number;
	private TextView tv_result;
	private Vibrator vibrator;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_number_location);
		
		vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		et_number = (EditText) findViewById(R.id.et_number);
		tv_result = (TextView) findViewById(R.id.tv_result);
		
		et_number.addTextChangedListener(new TextWatcher() {		
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(s!= null&&s.length()>=3){
					//查询数据库，并且显示结果
					String address = PhoneLocationQueryUtils.queryNumber(s.toString());
					tv_result.setText(address);
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				
			}
		});
	}
	
	public void query(View view){
		String phone = et_number.getText().toString().trim();
		if(TextUtils.isEmpty(phone)){
			Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
			et_number.startAnimation(shake);
		//	vibrator.vibrate(2000);
			long[] pattern = {200,200,300,300,1000,1000};
			//-1 no repeat 
			vibrator.vibrate(pattern, -1);
			Toast.makeText(this, "phone number null", Toast.LENGTH_SHORT).show();
			return;
		}else {
			String location = PhoneLocationQueryUtils.queryNumber(phone);
			tv_result.setText(location);
		}
	}
}
