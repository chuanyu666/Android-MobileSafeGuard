package com.yc.mobilesafeguard.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;
import android.widget.Toast;

import com.yc.mobilesafeguard.R;
import com.yc.mobilesafeguard.utils.StreamTool;

public class SplashActivity extends Activity {

	protected static final String TAG = "SplashActivity ";
	protected static final int ENTER_HOME = 0;
	protected static final int SHOW_UPDATE_DIALOG = 1;
	protected static final int URL_ERROR = 2;
	protected static final int NETWORK_ERROR = 3;
	protected static final int JSON_ERROR = 4;
	private TextView tv_splash_version;
	private String description;
	private String apkurl;
	private TextView tv_update_progres;
	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		sp = getSharedPreferences("config",MODE_PRIVATE); 
		tv_splash_version = (TextView) findViewById(R.id.tv_splash_version);
		tv_splash_version.setText("Version: " + getVersion());
		copyDB("address.db");
		copyDB("antivirus.db");
		boolean update = sp.getBoolean("update", false);
		
		installShortcut();
		
		if(update){
			checkUpdate();			
		}else{
			handler.postDelayed(new Runnable() {				
				@Override
				public void run() {
					enterHome();
				}
			}, 3000);
		}
		AlphaAnimation aa = new AlphaAnimation(0.2f, 1.0f);
		aa.setDuration(1000);
		findViewById(R.id.rl_splash).startAnimation(aa);
		
		tv_update_progres = (TextView) findViewById(R.id.tv_update_progress);
	}

	private void installShortcut() {
		if(sp.getBoolean("shortcut", false)){
			return;
		}
		Editor editor = sp.edit();	
		Intent intent = new Intent();
		intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
		intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "safe guard");
		intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));
		Intent shortcutIntent = new Intent();
		shortcutIntent.setAction("android.intent.action.MAIN");
		shortcutIntent.addCategory("android.intent.category.LAUNCHER");
		shortcutIntent.setClassName(getPackageName(), "com.yc.mobilesafeguard.activity.SplashActivity");
		intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
		sendBroadcast(intent);
		editor.putBoolean("shortcut", true);
		editor.commit();
	}

	private void copyDB(String filename) {
	
			File file = new File(getFilesDir(),filename);
			if(file.exists() && file.length()>0){
				
			}else{
				try {
					InputStream is = getAssets().open(filename);
					FileOutputStream fos = new FileOutputStream(file);
					byte[] buffer = new byte[1024];
					int len = 0;
					while((len = is.read(buffer))!=-1){
						fos.write(buffer, 0, len);
					}
					is.close();
					fos.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}				
			}
		
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case ENTER_HOME:
				enterHome();
				break;
			case SHOW_UPDATE_DIALOG:
				showUpdateDialog();
				break;
			case URL_ERROR:
				Toast.makeText(SplashActivity.this, "Network Error",
						Toast.LENGTH_SHORT).show();
				enterHome();
				break;
			case NETWORK_ERROR:
				enterHome();
				break;
			case JSON_ERROR:
				enterHome();
				break;
			default:
				break;
			}
		}
	};

	private void showUpdateDialog() {
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle("Update Info");
		builder.setMessage(description);
	//	builder.setCancelable(false); 
		builder.setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				enterHome();
				dialog.dismiss();
			}
		});
		builder.setPositiveButton("Update Now", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					FinalHttp http = new FinalHttp();
					http.download(apkurl, Environment
							.getExternalStorageDirectory().getAbsolutePath()
							+ "/mobilesg2.0.apk", 
							new AjaxCallBack<File>() {

								@Override
								public void onFailure(Throwable t, int errorNo,
										String strMsg) {
									t.printStackTrace();
									Toast.makeText(SplashActivity.this, "download Error",
											Toast.LENGTH_SHORT).show();
									super.onFailure(t, errorNo, strMsg);
								}

								@Override
								public void onLoading(long count, long current) {
									super.onLoading(count, current);
									int progress = (int) (current *100/count);
									tv_update_progres.setText("Downloading"+progress+"%");
								}

								@Override
								public void onSuccess(File t) {
									super.onSuccess(t);
									installApk(t);
								}

								private void installApk(File t) {
									Intent intent = new Intent();
									intent.setAction("android.intent.action.VIEW");
									intent.addCategory("android.intent.category.DEFAULT");
									intent.setDataAndType(Uri.fromFile(t), "application/vnd.android.package-archive");
									startActivity(intent);
								}
						 
					});
				} else {
					Toast.makeText(SplashActivity.this, "No Sdcard",
							Toast.LENGTH_SHORT).show();
					return;
				}
			}
		});
		builder.setNegativeButton("Cancel", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				enterHome();
			}
		});
		builder.show();
	}

	private void enterHome() {
		Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);
		finish();
	}

	/**
	 * check apk new version
	 */
	private void checkUpdate() {
		new Thread() {
			public void run() {
				Message msg = Message.obtain();
				long startTime = System.currentTimeMillis();
				try {
					URL url = new URL(getString(R.string.serverurl));
					HttpURLConnection conn = (HttpURLConnection) url
							.openConnection();
					conn.setRequestMethod("GET");
					conn.setConnectTimeout(5000);
					int code = conn.getResponseCode();
					if (code == 200) {
						InputStream is = conn.getInputStream();
						String result = StreamTool.readInputStream(is);
						Log.i(TAG, result);
						JSONObject object = new JSONObject(result);
						String version = (String) object.get("version");
						description = (String) object.get("description");
						apkurl = (String) object.get("apkurl");
						if (getVersion().equals(version)) {
							msg.what = ENTER_HOME;
						} else {
							msg.what = SHOW_UPDATE_DIALOG;
						}
					}
				} catch (MalformedURLException e) {
					msg.what = URL_ERROR;
					e.printStackTrace();
				} catch (IOException e) {
					msg.what = NETWORK_ERROR;
					e.printStackTrace();
				} catch (JSONException e) {
					msg.what = JSON_ERROR;
					e.printStackTrace();
				} finally {
					long endTime = System.currentTimeMillis();
					long activateTime = endTime - startTime;
					if (activateTime < 2000) {
						try {
							Thread.sleep(2000 - activateTime);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					handler.sendMessage(msg);
				}
			};
		}.start();
	}

	private String getVersion() {
		PackageManager pManager = getPackageManager();
		try {
			PackageInfo info = pManager.getPackageInfo(getPackageName(), 0);
			return info.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return "";
		}
	}

}
