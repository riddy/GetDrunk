package com.hackathon.getdrunk.model;

public class BluetoothConnection {
	private int rssi;
	private boolean is_close_by;

	public int getRssi() {
		return rssi;
	}

	public void setRssi(int rssi) {
		this.rssi = rssi;
	}

	public boolean isIs_close_by() {
		return is_close_by;
	}

	public void setIs_close_by(boolean is_close_by) {
		this.is_close_by = is_close_by;
	}

}
