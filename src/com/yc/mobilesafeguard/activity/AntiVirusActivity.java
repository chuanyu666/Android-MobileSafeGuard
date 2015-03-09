package com.yc.mobilesafeguard.activity;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yc.mobilesafeguard.R;
import com.yc.mobilesafeguard.db.dao.AntiVirusDao;
import com.yc.mobilesafeguard.domain.ScanVirusInfo;
import com.yc.mobilesafeguard.utils.MD5Utils;

public class AntiVirusActivity extends Activity {
	
	protected static final int SCANNING = 0;
	protected static final int FINISH = 1;
	private ImageView iv_scan_rotate;
	private ProgressBar pb_virus_scan;
	private PackageManager pm;
	private TextView tv_scan_status;
	private LinearLayout ll_scan_status;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_anti_virus);
		
		pm = getPackageManager();
		ll_scan_status = (LinearLayout) findViewById(R.id.ll_scan_status);
		tv_scan_status = (TextView) findViewById(R.id.tv_scan_status);
		 iv_scan_rotate = (ImageView) findViewById(R.id.iv_scan_rotate);
		 RotateAnimation ra = new RotateAnimation(0,360,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
		 ra.setDuration(1000);
		 ra.setRepeatCount(RotateAnimation.INFINITE);
		 iv_scan_rotate.startAnimation(ra);
		 
		 pb_virus_scan = (ProgressBar) findViewById(R.id.pb_virus_scan);
		 scanVirus();
	}
	
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SCANNING:
				final ScanVirusInfo scanInfo = (ScanVirusInfo) msg.obj;
				tv_scan_status.setText("scanning: "+scanInfo.getName());
				TextView tv = new TextView(AntiVirusActivity.this);
				if(scanInfo.isVirus()){
					tv.setTextColor(Color.RED);
					tv.setText("Find Virus: "+scanInfo.getName());
					tv.setOnClickListener(new OnClickListener() {					
						@Override
						public void onClick(View v) {
							Intent intent = new Intent();
							intent.setAction("android.intent.action.VIEW");
							intent.setAction("android.intent.action.DELETE"); 
							intent.addCategory("android.intent.category.DEFAULT");
							intent.setData(Uri.parse("package:"+scanInfo.getPackname()));
							startActivity(intent);	
						}
					});
				}else {
					tv.setTextColor(Color.BLACK);
					tv.setText("Secure app: "+scanInfo.getName());
				}		
				ll_scan_status.addView(tv,0);
				break;
			case FINISH:
				tv_scan_status.setText("scan complete");
				iv_scan_rotate.clearAnimation();
				break;
			}
		};
	};
	
	private void scanVirus() {
		tv_scan_status.setText("initialize virus data engine...");
		new Thread(){
			public void run() { 
				List<PackageInfo> infos = pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES | PackageManager.GET_SIGNATURES);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				pb_virus_scan.setMax(infos.size());
				int progress = 0;
				for(PackageInfo info : infos){
				//	String sourceDir = info.applicationInfo.sourceDir;
					String signature = info.signatures[0].toCharsString();
					String md5 = MD5Utils.md5Password(signature);
			//		System.out.println(info.applicationInfo.loadLabel(pm).toString()+":"+md5);
					ScanVirusInfo scanInfo = new ScanVirusInfo();
					scanInfo.setName(info.applicationInfo.loadLabel(pm).toString());
					scanInfo.setPackname(info.packageName);
					if(AntiVirusDao.isVirus(md5)){
						scanInfo.setVirus(true);
					}else {
						scanInfo.setVirus(false);
					}
					Message msg = Message.obtain();
					msg.obj = scanInfo;
					msg.what = SCANNING ;
					handler.sendMessage(msg);
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					progress++;
					pb_virus_scan.setProgress(progress);
				}	
				Message msg = Message.obtain();
				msg.what = FINISH ;
				handler.sendMessage(msg);
			};
		}.start();
	}
	
	
	/**
	 * 获取文件的md5值
	 * @param path
	 * @return
	 */
//	private String getFileMD5(String path){
//		 try {
//			File file = new File(path);
//			 FileInputStream fis = new FileInputStream(file);
//			 MessageDigest digest = MessageDigest.getInstance("md5");
//			 byte[] buffer = new byte[1024];
//			 int len = -1;
//			 while((len=fis.read(buffer))!=-1){
//				 digest.update(buffer, 0, len);
//			 } 
//			 byte[] result = digest.digest();
//			 StringBuffer sb = new StringBuffer();
//			 for(byte b:result){
//				int number = b & 0xff;
//				String str = Integer.toHexString(number);
//				if(str.length()==1){
//					sb.append("0");
//				}
//				sb.append(str);
//			}
//			fis.close();
//			return sb.toString();
//		} catch (Exception e) {
//			e.printStackTrace();
//			return ""; 
//		}
//	}
	
}
