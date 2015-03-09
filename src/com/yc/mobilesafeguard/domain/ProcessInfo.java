package com.yc.mobilesafeguard.domain;

import android.graphics.drawable.Drawable;

public class ProcessInfo {
	
	private Drawable icon;
	private String name;
	private String packageName;
	private long momeryUsed;
	private boolean userProcess;
	private boolean checked;
	
	
	public boolean isChecked() {
		return checked;
	}
	public void setChecked(boolean checked) {
		this.checked = checked;
	}
	public Drawable getIcon() {
		return icon;
	}
	public void setIcon(Drawable icon) {
		this.icon = icon;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public long getMomeryUsed() {
		return momeryUsed;
	}
	public void setMomeryUsed(long momeryUsed) {
		this.momeryUsed = momeryUsed;
	}
	public boolean isUserProcess() {
		return userProcess;
	}
	public void setUserProcess(boolean userProcess) {
		this.userProcess = userProcess;
	}
	
	
	
}
