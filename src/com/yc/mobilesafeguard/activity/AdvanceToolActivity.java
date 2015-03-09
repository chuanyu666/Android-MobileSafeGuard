package com.yc.mobilesafeguard.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.yc.mobilesafeguard.R;
import com.yc.mobilesafeguard.utils.SmsUtils;
import com.yc.mobilesafeguard.utils.SmsUtils.restoreSmsCallBack;

public class AdvanceToolActivity extends Activity {
	
	private ProgressDialog pd;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_advance_tools);
	}

	public void numberQuery(View view) {
		Intent intent = new Intent(this, NumberLocationActivity.class);
		startActivity(intent);
	}

	public void smsBackup(View view) {
		pd = new ProgressDialog(this);
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.setMessage("backup now, please wait for a moment");
		pd.show();
		new Thread(){
			public void run() {
				try {
					SmsUtils.backupSms(AdvanceToolActivity.this,pd);
					runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(AdvanceToolActivity.this, "backup success", Toast.LENGTH_SHORT).show();							
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
					runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(AdvanceToolActivity.this, "backup fail", Toast.LENGTH_SHORT).show();													
						}
					});
				}finally{
					pd.dismiss();
				}
			};
		}.start();		
	}

	public void smsRestore(View view) {
		pd = new ProgressDialog(this);
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.setMessage("restore now, please wait for a moment");
		pd.show();
		new Thread(){
			public void run() {
				try {
					SmsUtils.restoreSms(AdvanceToolActivity.this, true, new restoreSmsCallBack() {					
						@Override
						public void onRestore(int progress) {
							pd.setProgress(progress);
						}
						
						@Override
						public void beforeRestore(int max) {
							pd.setMax(max);
						}
					});
					runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(AdvanceToolActivity.this, "restore success", Toast.LENGTH_SHORT).show();							
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
					runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(AdvanceToolActivity.this, "restore fail", Toast.LENGTH_SHORT).show();													
						}
					});
				}finally{
					pd.dismiss();
				}
			};
		}.start();	
	}

}
