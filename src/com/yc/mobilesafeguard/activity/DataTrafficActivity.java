package com.yc.mobilesafeguard.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.yc.mobilesafeguard.R;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.TrafficStats;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class DataTrafficActivity extends Activity {
	private TextView tv_data;
	private ListView lv_app_data;
	private List<ApplicationInfo> infos;
	private PackageManager pm;
	private DataAdapter adapter;
	private Timer timer;
	private TimerTask task;
	private List<ApplicationInfo> dataApps;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_data_traffic);
		
		tv_data = (TextView) findViewById(R.id.tv_data);
		lv_app_data = (ListView) findViewById(R.id.lv_app_data);
		pm = getPackageManager();
		infos = pm.getInstalledApplications(0);
		dataApps = new ArrayList<ApplicationInfo>();
		for(ApplicationInfo info : infos){
			int uid = info.uid;
			long tx = TrafficStats.getUidTxBytes(uid);  // upload
			long rx = TrafficStats.getUidRxBytes(uid); 
			if(tx!=0 || rx!=0){
				dataApps.add(info);
			}
		}
		
		long mobiletx = TrafficStats.getMobileTxBytes();
		long mobilerx = TrafficStats.getMobileRxBytes();
		
		long wifiData = TrafficStats.getTotalTxBytes()+TrafficStats.getTotalRxBytes()-mobilerx-mobiletx;
	
		tv_data.setText("4G data used: "+Formatter.formatFileSize(this, mobilerx+mobiletx)+"    wifi used: "
						+Formatter.formatFileSize(this, wifiData));
		adapter = new DataAdapter();
		lv_app_data.setAdapter(adapter);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		timer = new Timer();
		task = new TimerTask() {		
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					public void run() {
						adapter.notifyDataSetChanged();
					}
				});
			}
		};
		timer.schedule(task, 0, 3000);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		timer.cancel();
		task.cancel();
		timer = null;
		task = null;	
	}
	
	private class DataAdapter extends BaseAdapter{
		@Override
		public int getCount() {
			return dataApps.size();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			ViewHolder holder;
			ApplicationInfo info = dataApps.get(position);
			if(convertView!=null){
				view = convertView;
				holder = (ViewHolder) view.getTag();
			}else{
				view = View.inflate(DataTrafficActivity.this, R.layout.list_item_appdata, null);
				holder = new ViewHolder();
				holder.tv_app_name = (TextView) view.findViewById(R.id.tv_app_name);
				holder.tv_app_data = (TextView) view.findViewById(R.id.tv_app_data);
				holder.iv_app_icon = (ImageView) view.findViewById(R.id.iv_app_icon);
				view.setTag(holder);
			}
			int uid = info.uid;
			long tx = TrafficStats.getUidTxBytes(uid);  // upload
			long rx = TrafficStats.getUidRxBytes(uid);   // download
			holder.tv_app_name.setText(info.loadLabel(pm).toString());
			holder.tv_app_data.setText(Formatter.formatFileSize(DataTrafficActivity.this, tx+rx));
			holder.iv_app_icon.setImageDrawable(info.loadIcon(pm));;				
			
			return view;
		}
		
		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}		
	}
	
	class ViewHolder{
		TextView tv_app_name;
		TextView tv_app_data;
		ImageView iv_app_icon;
	}
}
