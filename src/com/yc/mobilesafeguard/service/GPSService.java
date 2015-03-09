package com.yc.mobilesafeguard.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

public class GPSService extends Service {
	private LocationManager lm;
	private MyLocation listener;
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		lm = (LocationManager) getSystemService(LOCATION_SERVICE);
		listener = new MyLocation();
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		String provider = lm.getBestProvider(criteria, true);
		lm.requestLocationUpdates(provider, 60000, 50, listener);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		lm.removeUpdates(listener);
		listener = null;
	}
	
	class MyLocation implements LocationListener{

		@Override
		public void onLocationChanged(Location location) {
			String longitude = "latitude: "+location.getLongitude()+"\n";
			String latitude = "longitude: "+location.getLatitude() +"\n";
			String accuracy = "accuracy: "+location.getAccuracy();
//			try {
//				InputStream is = getAssets().open("axisoffset.dat");
//				ModifyOffset offset = ModifyOffset.getInstance(is);
//				PointDouble pd = offset.c2s(new PointDouble(location.getLongitude(), location.getLatitude()));
//				longitude = "latitude: "+pd.x+"\n";
//				latitude= "longitude: "+pd.y +"\n";
//				
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
			
			SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
			Editor editor = sp.edit();
			editor.putString("lastLocation", latitude+longitude+accuracy);
			editor.commit();
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			
		}
		
	}
}
