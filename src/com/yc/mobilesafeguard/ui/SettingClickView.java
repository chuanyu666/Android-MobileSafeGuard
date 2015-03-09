package com.yc.mobilesafeguard.ui;

import com.yc.mobilesafeguard.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SettingClickView extends RelativeLayout {
	
	private TextView tv_desc_update;
	private TextView tv_title_update;
	private String desc_on;
	private String desc_off;
	
	public SettingClickView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	public SettingClickView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
		String title = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.yc.mobilesafeguard", "title");
		desc_on = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.yc.mobilesafeguard", "desc_on");
		desc_off = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.yc.mobilesafeguard", "desc_off");
		tv_title_update.setText(title);
		tv_desc_update.setText(desc_off);
	}

	public SettingClickView(Context context) {
		super(context);
		initView(context );
	}

	private void initView(Context context) {
		View.inflate(context, R.layout.setting_click_view,SettingClickView.this);
		tv_desc_update = (TextView) findViewById(R.id.tv_desc_update);
		tv_title_update = (TextView) findViewById(R.id.tv_title_update);
	}

	
	public void setChecked(boolean checked){
		if(checked){
			setDescription(desc_on);
		}else{
			setDescription(desc_off);
		}
	}
	
	public void setDescription(String text){
		tv_desc_update.setText(text);
	}
}
