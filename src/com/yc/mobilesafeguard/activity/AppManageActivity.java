package com.yc.mobilesafeguard.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yc.mobilesafeguard.R;
import com.yc.mobilesafeguard.db.dao.AppLockDao;
import com.yc.mobilesafeguard.domain.AppInfo;
import com.yc.mobilesafeguard.engine.AppInfoProvider;
import com.yc.mobilesafeguard.utils.DensityUtil;

public class AppManageActivity extends Activity implements OnClickListener {
	private TextView tv_rom;
	private TextView tv_sdcard;
	private LinearLayout ll_app_load;
	private ListView lv_app_manage;
	private List<AppInfo> appList;
	private List<AppInfo> userApp;
	private List<AppInfo> systemApp;
	private TextView tv_status;
	private PopupWindow popupWindow;
	private LinearLayout ll_uninstall;
	private LinearLayout ll_start;
	private LinearLayout ll_share;
	private AppInfo info;
	private AppAdapter adapter;
	private AppLockDao dao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_manage);

		dao = new AppLockDao(this);
		tv_rom = (TextView) findViewById(R.id.tv_rom);
		tv_sdcard = (TextView) findViewById(R.id.tv_sdcard);
		ll_app_load = (LinearLayout) findViewById(R.id.ll_app_load);
		lv_app_manage = (ListView) findViewById(R.id.lv_app_list);
		tv_status = (TextView) findViewById(R.id.tv_status);
		tv_sdcard.setText("SdCard:"
				+ getAvailableSpace(Environment.getExternalStorageDirectory()
						.getAbsolutePath()));
		tv_rom.setText("Rom:"
				+ getAvailableSpace(Environment.getDataDirectory()
						.getAbsolutePath()));

		ll_app_load.setVisibility(View.VISIBLE);
		fillData();

		lv_app_manage.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				dismissPopupWindow();
				if (userApp != null && systemApp != null) {
					if (firstVisibleItem > userApp.size()) {
						tv_status.setText("System App: " + systemApp.size());
					} else {
						tv_status.setText("User App: " + userApp.size());
					}
				}
			}
		});

		lv_app_manage.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				if (position == 0) {
					return;
				} else if (position == userApp.size() + 1) {
					return;
				} else if (position <= userApp.size()) {
					info = userApp.get(position - 1);
				} else {
					info = systemApp.get(position - 1 - userApp.size() - 1);
				}
				dismissPopupWindow();
				View contentView = View.inflate(AppManageActivity.this,
						R.layout.popup_app_item, null);
				ll_uninstall = (LinearLayout) contentView
						.findViewById(R.id.ll_uninstall);
				ll_start = (LinearLayout) contentView
						.findViewById(R.id.ll_start);
				ll_share = (LinearLayout) contentView
						.findViewById(R.id.ll_share);

				ll_uninstall.setOnClickListener(AppManageActivity.this);
				ll_start.setOnClickListener(AppManageActivity.this);
				ll_share.setOnClickListener(AppManageActivity.this);

				popupWindow = new PopupWindow(contentView, -2, -2);//-2 = wrap_content
				// popupwindow 加动画必须设置背景颜色
				popupWindow.setBackgroundDrawable(new ColorDrawable(
						Color.TRANSPARENT));
				int[] location = new int[2];
				view.getLocationInWindow(location);
				int dip = 60;
				int px = DensityUtil.dip2px(AppManageActivity.this, dip);
				// 代码中的都是像素，要转成dip
				popupWindow.showAtLocation(parent, Gravity.LEFT | Gravity.TOP,
						px, location[1]);
				ScaleAnimation sa = new ScaleAnimation(0.3f, 1.0f, 0.3f, 1.0f,
						Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF, 0.5f);
				sa.setDuration(1000);
				AlphaAnimation aa = new AlphaAnimation(0.5f, 1.0f);
				aa.setDuration(1000);
				AnimationSet as = new AnimationSet(false);
				as.addAnimation(sa);
				as.addAnimation(aa);
				contentView.startAnimation(as);
			}
		});
		
		lv_app_manage.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent,
					View view, int position, long id) {
				if (position == 0) {
					return true;
				} else if (position == userApp.size() + 1) {
					return true;
				} else if (position <= userApp.size()) {
					info = userApp.get(position - 1);
				} else {
					info = systemApp.get(position - 1 - userApp.size() - 1);
				}
				ViewHolder holder = (ViewHolder) view.getTag();
				if(dao.query(info.getPackageName())){
					dao.delete(info.getPackageName());
					holder.iv_app_status.setImageResource(R.drawable.unlock);
				}else {
					dao.add(info.getPackageName());
					holder.iv_app_status.setImageResource(R.drawable.lock);
				}
				return true;
			}
		});
	}

	private void fillData() {
		new Thread() {
			public void run() {
				appList = AppInfoProvider.getAppInfos(AppManageActivity.this);
				userApp = new ArrayList<AppInfo>();
				systemApp = new ArrayList<AppInfo>();
				for (AppInfo info : appList) {
					if (info.isUserApp()) {
						userApp.add(info);
					} else {
						systemApp.add(info);
					}
				}
				runOnUiThread(new Runnable() {
					public void run() {
						if(adapter==null){
							adapter = new AppAdapter();
							lv_app_manage.setAdapter(adapter);
						}else{
							adapter.notifyDataSetChanged();
						}
						ll_app_load.setVisibility(View.INVISIBLE);							
					}
				});
			};
		}.start();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		dismissPopupWindow();
	}

	private void dismissPopupWindow() {
		if (popupWindow != null && popupWindow.isShowing()) {
			popupWindow.dismiss();
			popupWindow = null;
		}
	}

	private class AppAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// return appList.size();
			return userApp.size() + 1 + systemApp.size() + 1;
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
			AppInfo appInfo;
			if (position == 0) {
				TextView tv = new TextView(AppManageActivity.this);
				tv.setTextColor(Color.WHITE);
				tv.setBackgroundColor(Color.GRAY);
				tv.setText("User App: " + userApp.size());
				return tv;
			} else if (position == userApp.size() + 1) {
				TextView tv = new TextView(AppManageActivity.this);
				tv.setTextColor(Color.WHITE);
				tv.setBackgroundColor(Color.GRAY);
				tv.setText("System App: " + systemApp.size());
				return tv;
			} else if (position <= userApp.size()) {
				appInfo = userApp.get(position - 1);
			} else {
				appInfo = systemApp.get(position - 1 - userApp.size() - 1);
			}
			View view;
			ViewHolder holder;
			if (convertView != null && convertView instanceof RelativeLayout) {// 判断格式类型
				view = convertView;
				holder = (ViewHolder) view.getTag();
			} else {
				view = View.inflate(AppManageActivity.this,
						R.layout.list_item_appinfo, null);
				holder = new ViewHolder();
				holder.iv_app_icon = (ImageView) view
						.findViewById(R.id.iv_app_icon);
				holder.tv_app_name = (TextView) view
						.findViewById(R.id.tv_app_name);
				holder.tv_app_location = (TextView) view
						.findViewById(R.id.tv_app_location);
				holder.iv_app_status = (ImageView) view.findViewById(R.id.iv_app_status);
				view.setTag(holder);
			}
			holder.iv_app_icon.setImageDrawable(appInfo.getIcon());
			holder.tv_app_name.setText(appInfo.getName());
			if (appInfo.isInRom()) {
				holder.tv_app_location.setText("Rom");
			} else {
				holder.tv_app_location.setText("External");
			}
			if(dao.query(appInfo.getPackageName())){
				holder.iv_app_status.setImageResource(R.drawable.lock);
			}else {
				holder.iv_app_status.setImageResource(R.drawable.unlock);
			}
			return view;
		}
	}

	class ViewHolder {
		TextView tv_app_name;
		TextView tv_app_location;
		ImageView iv_app_icon;
		ImageView iv_app_status;
	}

	private String getAvailableSpace(String path) {
		StatFs statFs = new StatFs(path);
		long count = statFs.getBlockCount();//  分区个数
		long size = statFs.getBlockSize();// 分区大小
		long avaiCount = statFs.getAvailableBlocks();
		return Formatter.formatFileSize(this, size * avaiCount) + "/"
				+ Formatter.formatFileSize(this, size * count);
	}

	@Override
	public void onClick(View v) {
		dismissPopupWindow();
		Intent intent;
		switch (v.getId()) {
		case R.id.ll_uninstall:
			if(info.isUserApp()){
				intent = new Intent();
				intent.setAction("android.intent.action.VIEW");
				intent.setAction("android.intent.action.DELETE"); 
				intent.addCategory("android.intent.category.DEFAULT");
				intent.setData(Uri.parse("package:"+info.getPackageName()));
				startActivityForResult(intent, 0);				
			}else{
				Toast.makeText(this, "can not uninstall system app", Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.ll_start:
			PackageManager pm = this.getPackageManager();
//			Intent intent = new Intent();
//			intent.setAction("android.intent.action.MAIN");
//			intent.addCategory("android.intent.category.LAUNCHER");
//			pm.queryIntentActivities(intent, PackageManager.GET_INTENT_FILTERS);
			intent = pm.getLaunchIntentForPackage(info.getPackageName());
			if(intent!=null){
				startActivity(intent);				
			}else {
				Toast.makeText(this, "can not start", Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.ll_share:
			intent = new Intent();
			intent.setAction("android.intent.action.SEND"); 
			intent.addCategory("android.intent.category.DEFAULT");
			intent.setType("text/plain");
			intent.putExtra(Intent.EXTRA_TEXT, "Recommend an app to you, name is "+info.getName());
			startActivity(intent);
			break;
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		fillData();
	}
}
