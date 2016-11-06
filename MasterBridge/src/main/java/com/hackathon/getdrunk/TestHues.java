package com.hackathon.getdrunk;

public class TestHues {
	
	public static void main(String[] args) {
		
		HueHue myHueHue = new HueHue();	
		myHueHue.initHueHue();
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		myHueHue.setLightsIdle();
	}

}
