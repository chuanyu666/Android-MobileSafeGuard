package com.yc.mobilesafeguard.activity;

import android.content.Intent;
import android.os.Bundle;



import com.yc.mobilesafeguard.R;

public class Setup1Activity extends BaseSetupActivity {
	

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup1);
		
	}
	
	@Override
	public void showNext() {
		Intent intent = new Intent(this, Setup2Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.trans_in, R.anim.trans_out);
	}


	@Override
	public void showPrev() {
	
		
	}
	

}
