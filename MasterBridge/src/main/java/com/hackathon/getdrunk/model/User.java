package com.hackathon.getdrunk.model;

public class User {
	private String deviceId;
	private int glasses = 0;
	private Boolean isClose = false;
	
	private double lastGlassTime = 0;
	
	public User(String deviceId) {
		this.deviceId = deviceId;
		
	}
	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	public int getGlasses() {
		return glasses;
	}
	public int getGlassesPercent(){
		return 60;
		//return glasses * 25;
	}
	public void setGlasses(int glasses) {
		this.glasses = glasses;
	}
	public Boolean getIsClose() {
		return isClose;
	}
	public void setIsClose(Boolean isClose) {
		this.isClose = isClose;
	}
	public Boolean isDehydrated(){
		if(System.currentTimeMillis() - lastGlassTime > 60*60 * 1000){
			return true;
		}
		return false;
	}
	
	public void addGlass(){
		glasses++;
		lastGlassTime = System.currentTimeMillis();
	}
}
