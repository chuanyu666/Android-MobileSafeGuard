package com.yc.mobilesafeguard.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yc.mobilesafeguard.R;
import com.yc.mobilesafeguard.utils.MD5Utils;

public class HomeActivity extends Activity {
	private GridView gv_home;
	private SharedPreferences sp;
	
	private static String[] names = {
		"Safety","Call&SMS","App",
		"Process","Data","Virus",
		"Cache","Advance","Setting" 
		};
	
	private static int[] imageIds = {
		 R.drawable.safe,R.drawable.callmsgsafe,R.drawable.app,
		 R.drawable.taskmanager,R.drawable.netmanager,R.drawable.trojan,
		 R.drawable.sysoptimize,R.drawable.atools,R.drawable.settings 
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		gv_home = (GridView) findViewById(R.id.gv_home);
		gv_home.setAdapter(new MyAdapter() );
		gv_home.setOnItemClickListener(new OnItemClickListener() {
			Intent intent;
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				switch (position) {
				case 8:
					intent = new Intent(HomeActivity.this,SettingActivity.class);
					startActivity(intent);
					break;
				case 0:
					showLostFoundDialog();
					break;
				case 1:
					intent = new Intent(HomeActivity.this,CallSMSActivity.class);
					startActivity(intent); 
					break;
				case 2:
					intent = new Intent(HomeActivity.this,AppManageActivity.class);
					startActivity(intent); 
					break;
				case 3:
					intent = new Intent(HomeActivity.this,ProcessManageActivity.class);
					startActivity(intent); 
					break;
				case 4:
					intent = new Intent(HomeActivity.this,DataTrafficActivity.class);
					startActivity(intent); 
					break;
				case 5:
					intent = new Intent(HomeActivity.this,AntiVirusActivity.class);
					startActivity(intent); 
					break;
				case 6:
					intent = new Intent(HomeActivity.this,CleanCacheActivity.class);
					startActivity(intent); 
					break;
				case 7:
					intent = new Intent(HomeActivity.this,AdvanceToolActivity.class);
					startActivity(intent);
					break;
				default:
					break;
				}
			}		
		});
	}
	private void showLostFoundDialog() {
		if(isSetPwd()){
			showEnterPwdDialog();
		}else{
			showSetPwdDialog();
		}
	} 
	
	private EditText et_set_pwd;
	private EditText et_pwd_confirm;
	private Button btn_confirm;
	private Button btn_cancel ;
	private AlertDialog dialog;
	
	private void showSetPwdDialog() {
		AlertDialog.Builder builder = new Builder(this);
		View view = View.inflate(this, R.layout.dialog_set_password, null); 
		et_set_pwd = (EditText) view.findViewById(R.id.et_set_pwd);
		et_pwd_confirm = (EditText) view.findViewById(R.id.et_pwd_confirm);
		btn_confirm = (Button) view.findViewById(R.id.btn_confirm);
		btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
		btn_cancel.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		btn_confirm.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				String pwd = et_set_pwd.getText().toString().trim();
				String confirmPwd = et_pwd_confirm.getText().toString().trim();
				if(TextUtils.isEmpty(pwd) || TextUtils.isEmpty(confirmPwd)){
					Toast.makeText(HomeActivity.this, "password null", Toast.LENGTH_SHORT).show();
					return;
				}
				if(pwd.equals(confirmPwd)){
					Editor editor = sp.edit();
					editor.putString("password", MD5Utils.md5Password(pwd));
					editor.commit();
					dialog.dismiss();
					Intent intent = new Intent(HomeActivity.this, SafetyActivity.class);
					startActivity(intent);
				}else{
					Toast.makeText(HomeActivity.this, "password not identical", Toast.LENGTH_SHORT).show();
					return;
				}
			}
		});
		builder.setView(view);
		dialog = builder.show(); 
	}
	
	private void showEnterPwdDialog() {
		AlertDialog.Builder builder = new Builder(this);
		View view = View.inflate(this, R.layout.dialog_enter_password, null); 
		et_set_pwd = (EditText) view.findViewById(R.id.et_set_pwd); 
		btn_confirm = (Button) view.findViewById(R.id.btn_confirm);
		btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
		btn_cancel.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		btn_confirm.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				String pwd = et_set_pwd.getText().toString().trim();
				if(TextUtils.isEmpty(pwd)){
					Toast.makeText(HomeActivity.this, "password null", Toast.LENGTH_SHORT).show();
					return;
				}
				if(MD5Utils.md5Password(pwd).equals(sp.getString("password", ""))){
					dialog.dismiss();
					Intent intent = new Intent(HomeActivity.this, SafetyActivity.class);
					startActivity(intent);
				}else{
					Toast.makeText(HomeActivity.this, "password wrong", Toast.LENGTH_SHORT).show();
					return;
				}
			}
		});
		builder.setView(view);
		dialog = builder.show(); 
	}
	
	private boolean isSetPwd(){
		String password = sp.getString("password", null);
		if(TextUtils.isEmpty(password)){
			return false;
		}else{
			return true;			
		}
		//return !TextUtils.isEmpty(password);
	}
	
	private class MyAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return names.length;
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			View view = View.inflate(HomeActivity.this, R.layout.items_home, null);
			ImageView iv = (ImageView) view.findViewById(R.id.iv_items);
			TextView tv = (TextView) view.findViewById(R.id.tv_items);
			tv.setText(names[arg0]);
			iv.setImageResource(imageIds [arg0]);
			return view;
		}
		
	}
}
