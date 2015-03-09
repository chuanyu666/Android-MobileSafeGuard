package com.yc.mobilesafeguard.activity;

import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.yc.mobilesafeguard.R;

public class Setup3Activity extends BaseSetupActivity {
	private EditText et_phone;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup3);
		et_phone = (EditText) findViewById(R.id.et_phone);
		
		String phone = sp.getString("phone", "");
		et_phone.setText(phone);
	}
	
	public void select(View view){
		Intent intent = new Intent(this, SelectContactActivity.class);
		startActivityForResult(intent, 0);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(data == null){
			return;
		}
		String phone = data.getStringExtra("phone");
		et_phone.setText(phone); 
	}

	@Override
	public void showNext() {
		String phone = et_phone.getText().toString().trim();
		if(TextUtils.isEmpty(phone)){
			Toast.makeText(this, "please setup secure phone", Toast.LENGTH_SHORT).show();
			return;
		}
		Editor editor = sp.edit();
		editor.putString("phone", phone);
		editor.commit(); 
		Intent intent = new Intent(this, Setup4Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.trans_in, R.anim.trans_out);
	}


	@Override
	public void showPrev() {
		Intent intent = new Intent(this, Setup2Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.trans_pre_in, R.anim.trans_pre_out);
		
	}
}
