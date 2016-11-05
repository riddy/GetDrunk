package com.hackathon.getdrunk.model;

public class BluetoothConnection {
	private int RSSI;
	private boolean inrange;

	public int getRSSI() {
		return RSSI;
	}

	public void setRSSI(int rSSI) {
		RSSI = rSSI;
	}

	public boolean isInrange() {
		return inrange;
	}

	public void setInrange(boolean inrange) {
		this.inrange = inrange;
	}

}
