package com.yc.mobilesafeguard.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.TextView;

import com.yc.mobilesafeguard.R;
import com.yc.mobilesafeguard.db.dao.PhoneLocationQueryUtils;

public class NumberLocationService extends Service {
	private WindowManager wm;
	private TelephonyManager tm;
	private MyListener listener;
	private OutCallReceiver receiver;
	private View view;
	private SharedPreferences sp;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	class OutCallReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String phone = getResultData();
			String location = PhoneLocationQueryUtils.queryNumber(phone);
			// Toast.makeText(context, location, Toast.LENGTH_SHORT).show();
			MyToast(location);
		}

	}

	@Override
	public void onCreate() {
		super.onCreate();
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		listener = new MyListener();
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
		receiver = new OutCallReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.intent.action.NEW_OUTGOING_CALL");
		registerReceiver(receiver, filter);
		wm = (WindowManager) getSystemService(WINDOW_SERVICE);
		sp = getSharedPreferences("config", MODE_PRIVATE);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		tm.listen(listener, PhoneStateListener.LISTEN_NONE);
		listener = null;
		unregisterReceiver(receiver);
		receiver = null;
	}

	private class MyListener extends PhoneStateListener {
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:
				String location = PhoneLocationQueryUtils
						.queryNumber(incomingNumber);
				// Toast.makeText(getApplicationContext(), location,
				// Toast.LENGTH_SHORT).show();
				MyToast(location);
				break;
			case TelephonyManager.CALL_STATE_IDLE:
				if (view != null) {
					wm.removeView(view);
				}
				break;
			default:
				break;
			}
		}

	}
	
	WindowManager.LayoutParams params;
	private void MyToast(String location) {
		view = View.inflate(this, R.layout.phone_location_show, null);
		view.setOnTouchListener(new OnTouchListener() {
			int startX;
			int startY;
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_MOVE:
					int newX = (int) event.getRawX();
					int newY = (int) event.getRawY();
					int dx = newX - startX;
					int dy = newY - startY;
					params.x += dx;
					params.y += dy;
					if(params.x<0){
						params.x = 0;
					}
					if(params.y<0){
						params.y = 0;
					}
					if(params.x>(wm.getDefaultDisplay().getWidth()-view.getWidth() )){
						params.x = (wm.getDefaultDisplay().getWidth()-view.getWidth());
					}
					if(params.y>(wm.getDefaultDisplay().getHeight()-view.getHeight())){
						params.y = (wm.getDefaultDisplay().getHeight()-view.getHeight()); 
					}
					wm.updateViewLayout(view, params);
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_UP:
					Editor editor = sp.edit();
					editor.putInt("lastX", params.x);
					editor.putInt("lastY", params.y);
					editor.commit();
					break;
				}
				return true;
			}
		});
		TextView tv = (TextView) view.findViewById(R.id.tv_phone_location);
		tv.setText(location);
		int ids[] = { R.drawable.call_locate_white,
				R.drawable.call_locate_orange, R.drawable.call_locate_blue,
				R.drawable.call_locate_gray, R.drawable.call_locate_green };
		int select = sp.getInt("which", 0);
		view.setBackgroundResource(ids[select]);
		params = new WindowManager.LayoutParams();
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		params.gravity = Gravity.TOP+Gravity.LEFT;
		params.x = sp.getInt("lastX", 0);
		params.y = sp.getInt("lastY", 0);
		params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
			//	| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		params.format = PixelFormat.TRANSLUCENT;
	//	params.type = WindowManager.LayoutParams.TYPE_TOAST;
		//电话优先级，添加权限 android.permission.SYSTEM_ALERT_WINDOW
		params.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;
		wm.addView(view, params);
	}

}
