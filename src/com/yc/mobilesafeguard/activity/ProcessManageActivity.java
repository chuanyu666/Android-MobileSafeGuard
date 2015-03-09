package com.yc.mobilesafeguard.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yc.mobilesafeguard.R;
import com.yc.mobilesafeguard.domain.ProcessInfo;
import com.yc.mobilesafeguard.engine.ProcessInfoProvider;
import com.yc.mobilesafeguard.utils.SystemInfoUtils;

public class ProcessManageActivity extends Activity {
	private TextView tv_process;
	private TextView tv_memory;
	private LinearLayout ll_loading;
	private ListView lv_process_list;
	private List<ProcessInfo> processInfos;
	private List<ProcessInfo> userProcess;
	private List<ProcessInfo> systemProcess;
	private TextView tv_status;
	private ProcessAdapter adapter;
	private int processCount;
	private long availableMem;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_process_manage);

		tv_process = (TextView) findViewById(R.id.tv_process);
		tv_memory = (TextView) findViewById(R.id.tv_memory);
		tv_status = (TextView) findViewById(R.id.tv_status);
		ll_loading = (LinearLayout) findViewById(R.id.ll_process_load);
		lv_process_list = (ListView) findViewById(R.id.lv_process_list);
		fillData();
		lv_process_list.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (userProcess != null && systemProcess != null) {
					if (firstVisibleItem > userProcess.size()) {
						tv_status.setText("System process: "
								+ systemProcess.size());
					} else {
						tv_status.setText("User process: " + userProcess.size());
					}
				}
			}
		});
		lv_process_list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ProcessInfo processInfo;
				if (position == 0) {
					return;
				} else if (position == userProcess.size() + 1) {
					return;
				} else if (position <= userProcess.size()) {
					processInfo = userProcess.get(position - 1);
				} else {
					processInfo = systemProcess.get(position - 1
							- userProcess.size() - 1);
				}
				if(getPackageName().equals(processInfo.getPackageName())){
					return;
				}
				ViewHolder holder = (ViewHolder) view.getTag();
				if (processInfo.isChecked()) {
					processInfo.setChecked(false);
					holder.cb_checked.setChecked(false);
				} else {
					processInfo.setChecked(true);
					holder.cb_checked.setChecked(true);
				}
			}
		});
	}

	private void setTitle() {
		processCount = SystemInfoUtils.getRunningProcessCount(this);
		availableMem = SystemInfoUtils.getAvailableMemory(this);
		tv_process.setText("Running process: "
				+ processCount);
		tv_memory.setText("Memory Available: "
				+ Formatter.formatFileSize(this,
						availableMem)
				+ "/"
				+ Formatter.formatFileSize(this,
						SystemInfoUtils.getTotalMemory(this)));
	}

	private void fillData() {
		ll_loading.setVisibility(View.VISIBLE);
		new Thread() {
			public void run() {
				processInfos = ProcessInfoProvider
						.getProcessInfo(ProcessManageActivity.this);
				userProcess = new ArrayList<ProcessInfo>();
				systemProcess = new ArrayList<ProcessInfo>();
				for (ProcessInfo info : processInfos) {
					if (info.isUserProcess()) {
						userProcess.add(info);
					} else {
						systemProcess.add(info);
					}
				}
				runOnUiThread(new Runnable() {
					public void run() {
						ll_loading.setVisibility(View.INVISIBLE);
						if(adapter==null){
							adapter = new ProcessAdapter();
							lv_process_list.setAdapter(adapter);							
						}else{
							adapter.notifyDataSetChanged();
						}
						setTitle();
					}
				});
			};
		}.start();
	}

	private class ProcessAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
			boolean show = sp.getBoolean("displaySysPro", false);
			if(show){
				return userProcess.size() + 1 + systemProcess.size() + 1;				
			}else {
				return userProcess.size() + 1;
			}
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ProcessInfo processInfo;
			if (position == 0) {
				TextView tv = new TextView(ProcessManageActivity.this);
				tv.setTextColor(Color.WHITE);
				tv.setBackgroundColor(Color.GRAY);
				tv.setText("User process: " + userProcess.size());
				return tv;
			} else if (position == userProcess.size() + 1) {
				TextView tv = new TextView(ProcessManageActivity.this);
				tv.setTextColor(Color.WHITE);
				tv.setBackgroundColor(Color.GRAY);
				tv.setText("System process: " + systemProcess.size());
				return tv;
			} else if (position <= userProcess.size()) {
				processInfo = userProcess.get(position - 1);
			} else {
				processInfo = systemProcess.get(position - 1
						- userProcess.size() - 1);
			}
			View view;
			ViewHolder holder;
			if (convertView != null && convertView instanceof RelativeLayout) {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			} else {
				view = View.inflate(ProcessManageActivity.this,
						R.layout.list_item_process, null);
				holder = new ViewHolder();
				holder.iv_process_icon = (ImageView) view
						.findViewById(R.id.iv_process_icon);
				holder.tv_process_name = (TextView) view
						.findViewById(R.id.tv_process_name);
				holder.tv_process_memory = (TextView) view
						.findViewById(R.id.tv_process_memory);
				holder.cb_checked = (CheckBox) view
						.findViewById(R.id.cb_checked);
				view.setTag(holder);
			}
			holder.iv_process_icon.setImageDrawable(processInfo.getIcon());
			holder.tv_process_name.setText(processInfo.getName());
			holder.tv_process_memory.setText("Memory used: "
					+ Formatter.formatFileSize(ProcessManageActivity.this,
							processInfo.getMomeryUsed()));
			holder.cb_checked.setChecked(processInfo.isChecked());
			if(getPackageName().equals(processInfo.getPackageName())){
				holder.cb_checked.setVisibility(View.INVISIBLE);
			}else {
				holder.cb_checked.setVisibility(View.VISIBLE);
			}
			return view;
		}
	}

	class ViewHolder {
		ImageView iv_process_icon;
		TextView tv_process_name;
		TextView tv_process_memory;
		CheckBox cb_checked;
	}

	public void selectAll(View view) {
		for(ProcessInfo info:processInfos){
			if(getPackageName().equals(info.getPackageName())){
				continue;
			}
			info.setChecked(true);
		}
		adapter.notifyDataSetChanged();
	}

	public void selectInverse(View view) {
		for(ProcessInfo info:processInfos){
			if(getPackageName().equals(info.getPackageName())){
				continue;
			}
			info.setChecked(!info.isChecked() );
		}
		adapter.notifyDataSetChanged();
	}

	public void clean(View view) {
		ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		int count = 0;
		long savedMem = 0;
		List<ProcessInfo> killedInfos = new ArrayList<ProcessInfo>();
		for(ProcessInfo info:processInfos){
			if(info.isChecked()){
				am.killBackgroundProcesses(info.getPackageName());
				if(info.isUserProcess()){
					userProcess.remove(info);
				}else{
					systemProcess.remove(info);
				}
				killedInfos.add(info);
				count++;
				savedMem += info.getMomeryUsed();
			}
		}
		processInfos.removeAll(killedInfos);
		adapter.notifyDataSetChanged();
		Toast.makeText(this, "kill "+count+" process, release "+Formatter.formatFileSize(this, savedMem)+" memory" , Toast.LENGTH_LONG).show();
		processCount -= count;
		availableMem += savedMem;
		tv_process.setText("Running process: "
				+ processCount);
		tv_memory.setText("Memory Available: "
				+ Formatter.formatFileSize(this,
						availableMem)
				+ "/"
				+ Formatter.formatFileSize(this,
						SystemInfoUtils.getTotalMemory(this)));
	}

	public void setting(View view) {
		Intent intent = new Intent(this,ProcessSettingActivity.class);
		startActivityForResult(intent, 0);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		adapter.notifyDataSetChanged();
		super.onActivityResult(requestCode, resultCode, data);
	}
}
