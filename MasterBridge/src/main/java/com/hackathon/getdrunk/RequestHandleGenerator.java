package com.hackathon.getdrunk;

public class RequestHandleGenerator {
	private int lastHandle;
	private int start;
	private int end;
	
	public RequestHandleGenerator(int start, int end) {
		this.start = start;
		lastHandle = start;
		this.end = end;
	}
	
	public synchronized int getNextRequestHandle() {
		if (lastHandle >= end) {
			lastHandle = start;
		} else {
			lastHandle++;
		}
		return lastHandle;
	}
}
