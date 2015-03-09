package com.yc.mobilesafeguard.domain;

public class ScanVirusInfo {
	
	private String name;
	private String packname;
	private boolean isVirus;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPackname() {
		return packname;
	}
	public void setPackname(String packname) {
		this.packname = packname;
	}
	public boolean isVirus() {
		return isVirus;
	}
	public void setVirus(boolean isVirus) {
		this.isVirus = isVirus;
	}
	
	
}
