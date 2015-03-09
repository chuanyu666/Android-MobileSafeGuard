package com.yc.mobilesafeguard.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

public class BootReceiver extends BroadcastReceiver {
	
	private SharedPreferences sp;
	private TelephonyManager tm;
	@Override
	public void onReceive(Context context, Intent intent) {
		tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		String sim = sp.getString("sim", null);
		String simNow = tm.getSimSerialNumber();
		if(sim.equals(simNow)){
			
		}else{
			System.out.println("sim卡变更了，需要偷偷发短信；");
			String safenumber = sp.getString("phone", "");
			SmsManager smsManager = SmsManager.getDefault();
			smsManager.sendTextMessage(safenumber, null, "sim card change !", null, null);

		}
	}

	
}
