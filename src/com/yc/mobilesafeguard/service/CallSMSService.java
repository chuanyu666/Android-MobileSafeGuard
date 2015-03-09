package com.yc.mobilesafeguard.service;

import java.lang.reflect.Method;

import com.android.internal.telephony.ITelephony;
import com.yc.mobilesafeguard.db.dao.BlackNumberDao;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

public class CallSMSService extends Service {
	
	private SmsReceiver receiver;
	private BlackNumberDao dao;
	private TelephonyManager tm;
	private PhoneListener listener;
	@Override
	public IBinder onBind(Intent intent) {
		return null; 
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		dao = new BlackNumberDao(this);
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		listener = new PhoneListener();
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE );
		receiver = new SmsReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.provider.Telephony.SMS_RECEIVED");
		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		registerReceiver(receiver, filter);
	}
	
	private class PhoneListener extends PhoneStateListener{
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:
				String mode = dao.findMode(incomingNumber);
				if("1".equals(mode)|| "3".equals(mode)){
					//deletCallLog(incomingNumber);
					getContentResolver().registerContentObserver(Uri.parse("content://call_log/calls"), true, new CallLogObserver(new Handler(),incomingNumber));
					endCall(); //另一个进程运行的远程服务的方法，所以需要contentObserver来观察通话记录
				}
				break;
			default:
				break;
			}
		}
	}
	
	private class CallLogObserver extends ContentObserver{

		private String incomingNumber;
		
		public CallLogObserver(Handler handler,String incomingNumber) {
			super(handler);
			this.incomingNumber = incomingNumber;
		}
		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			getContentResolver().unregisterContentObserver(this);
			deletCallLog(incomingNumber);
		}
	}  
	
	private void endCall() {
		try {
			Class clazz = CallSMSService.class.getClassLoader().loadClass("android.os.ServiceManager");
			Method method = clazz.getDeclaredMethod("getService", String.class);
			IBinder binder = (IBinder) method.invoke( null, TELEPHONY_SERVICE); 
			ITelephony.Stub.asInterface(binder).endCall();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void deletCallLog(String incomingNumber) {
		ContentResolver resolver = getContentResolver();
		resolver.delete(Uri.parse("content://call_log/calls"), "number=?", new String[]{incomingNumber});
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
		tm.listen(listener, PhoneStateListener.LISTEN_NONE);
		receiver = null;
		listener = null; 
	}
	
	private class SmsReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			Object[] objs = (Object[]) intent.getExtras().get("pdus");
			for(Object obj: objs){
				SmsMessage message = SmsMessage.createFromPdu((byte[]) obj);
				String number = message.getOriginatingAddress();
				String mode = dao.findMode(number);
				if("2".equals(mode)||"3".equals(mode)){
					abortBroadcast();
				}
			}
		}
		
	}
}
