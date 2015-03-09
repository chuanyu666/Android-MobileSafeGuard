package com.yc.mobilesafeguard.activity;

import com.yc.mobilesafeguard.R;
import com.yc.mobilesafeguard.activity.AppManageActivity.ViewHolder;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class AppLockPwdActivity extends Activity {
	private EditText et_app_password;
	private String packageName; 
	private TextView tv_app_name;
	private ImageView iv_app_icon;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_password);
		et_app_password = (EditText) findViewById(R.id.et_app_password);
		Intent intent = getIntent();
		packageName = intent.getStringExtra("packagename");	
		
		tv_app_name = (TextView) findViewById(R.id.tv_app_name);
		iv_app_icon = (ImageView) findViewById(R.id.iv_app_icon);
		PackageManager pm = getPackageManager();
		try {
			ApplicationInfo info = pm.getApplicationInfo(packageName, 0);
			tv_app_name.setText(info.loadLabel(pm));
			iv_app_icon.setImageDrawable(info.loadIcon(pm));
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onBackPressed() {
		Intent intent = new Intent();
		intent.setAction("android.intent.action.MAIN");
		intent.addCategory("android.intent.category.HOME");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addCategory("android.intent.category.MONKEY");
		startActivity(intent);
		
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		finish();
	}
	
	public void click(View view){
		String password = et_app_password.getText().toString().trim();
		if(TextUtils.isEmpty(password)){
			Toast.makeText(this, "password null", Toast.LENGTH_SHORT).show();
			return;
		}
		if("123".equals(password)){
			Intent intent = new Intent();
			intent.setAction("com.yc.mobilesafeguard.temstop");
			intent.putExtra("packagename", packageName);
			sendBroadcast(intent);
			finish();
		}else {
			Toast.makeText(this, "password wrong", Toast.LENGTH_SHORT).show();
		}
	}
}
