package com.hackathon.getdrunk.model;

public class User {
	private String deviceId;
	private int glasses = 0;
	private Boolean isClose = false;
	
	private double lastGlassTime = 0;
	private String name;
	
	public User(String name, String deviceId) {
		this.setName(name);
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
		//return 60;
		int percent = glasses * 25;
		if(percent < 0) percent = 0;
		if(percent > 100) percent = 100;
		return percent;
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
		if(System.currentTimeMillis() - getLastGlassTime() > 60*60 * 1000){
			return true;
		}
		return false;
	}
	
	public void addGlass(){
		glasses++;
		setLastGlassTime(System.currentTimeMillis());
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getLastGlassTime() {
		return lastGlassTime;
	}
	public void setLastGlassTime(double lastGlassTime) {
		this.lastGlassTime = lastGlassTime;
	}
}
