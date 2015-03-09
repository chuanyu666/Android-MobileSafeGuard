package com.yc.mobilesafeguard.receiver;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.TextUtils;

import com.yc.mobilesafeguard.R;
import com.yc.mobilesafeguard.service.GPSService;

public class SmsReceiver extends BroadcastReceiver {
	
	private SharedPreferences sp;
	private DevicePolicyManager dpm;
	@Override
	public void onReceive(Context context, Intent intent) {
		Object[] objs = (Object[]) intent.getExtras().get("pdus");
		sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
		String safeNumber = sp.getString("phone", "");
		
		for(Object obj:objs){
			SmsMessage sms = SmsMessage.createFromPdu((byte[]) obj);
			String sender = sms.getOriginatingAddress();
			String body = sms.getMessageBody();
			
			if(sender.contains(safeNumber)){
				if("#*location*#".equals(body)){
					Intent intent2 = new Intent(context,GPSService.class);
					context.startService(intent2);
					String lastLocation = sp.getString("lastLocation", null);
					if(TextUtils.isEmpty(lastLocation)){
						SmsManager.getDefault().sendTextMessage(sender, null,"locating now...", null, null);
					}else{
						SmsManager.getDefault().sendTextMessage(sender, null,lastLocation, null, null);
					}
					abortBroadcast();
				}else if("#*alarm*#".equals(body)){
					MediaPlayer mp = MediaPlayer.create(context, R.raw.ylzs);
					mp.setLooping(false);
					mp.setVolume(1.0f, 1.0f);
					mp.start();
					abortBroadcast();
				}else if("#*delete*#".equals(body)){
					dpm.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);
					//chu chang she zhi
					dpm.wipeData(0);
					abortBroadcast();
				}else if("#*lock*#".equals(body)){
					ComponentName lock = new ComponentName(context, LockSceenReceiver.class);
					if(dpm.isAdminActive(lock)){
						dpm.lockNow();
						dpm.resetPassword("1234", 0);
						abortBroadcast();						
					}
				}				
			}
		}
	}
	
	
}
