package com.hackathon.getdrunk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHBridgeResourcesCache;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;

public class CycleLightsThread extends Thread {
	
	List<Integer> colorList;
	PHHueSDK hueInstance;
	Random rand = new Random();
	
	int HUE_VALUE = 45281;
	int SAT_VALUE = 245;
	int BRI_MIN = 5;
	int BRI_MAX = 254;
	
	boolean isPaused = false;
	boolean isParty = false;
	
	double currentDistance = -1;
	boolean lastColorMin = true;
	List<Integer> lightIndices = Arrays.asList(1,2);
	
	public CycleLightsThread(PHHueSDK hueInstance) {
		this.hueInstance = hueInstance;
	}
	
	
	public void setDistance(double currentDistance) {
		this.currentDistance = currentDistance;
	}
	
	public void startParty() {
		System.out.println("start partyyyy");
		isParty = true;
	}
	
	@Override
	public void run() {
		while (true) {
			int colorValue;
			if (isPaused == false) {
				if (currentDistance < 0) {
					if (lastColorMin) {
						colorValue = BRI_MIN + 50;
						lastColorMin = false;
					} else {
						colorValue = BRI_MIN;
						lastColorMin = true;
					}
				} else if (currentDistance == 0) {
					if (lastColorMin) {
						colorValue = BRI_MAX;
						lastColorMin = false;
					} else {
						colorValue = BRI_MAX - 70;
						lastColorMin = true;
					}
				} else {
					if (lastColorMin) {
						colorValue = BRI_MIN + 100 + ((int) (currentDistance * 80)) - 20;
						lastColorMin = false;
					} else {
						colorValue = BRI_MIN + 100 + ((int) (currentDistance * 80)) + 20;
						lastColorMin = true;
					}
				}
				setLights(colorValue);
				
				try {
					int sleepTime = 2000;
					Thread.sleep(sleepTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else if (isParty) {
				partyyyyy();
			}
			
		}
	}
	
	private void partyyyyy() {
		PHBridge bridge = hueInstance.getSelectedBridge();
		
		PHBridgeResourcesCache cache = bridge.getResourceCache();
		List<PHLight> lightsList = cache.getAllLights();
		
		PHLightState waterLightState = new PHLightState();
		
		for (int i = 0; i < 100; i++) {
			waterLightState.setHue(rand.nextInt(65530) + 2);
			waterLightState.setSaturation(250);
			waterLightState.setBrightness(200);
			waterLightState.setTransitionTime(0);
			bridge.updateLightState(lightsList.get(0), waterLightState);
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		isParty = false;
	}
	
	private void setLights(int briValue) {
		PHBridge bridge = hueInstance.getSelectedBridge();
		
		PHBridgeResourcesCache cache = bridge.getResourceCache();
		List<PHLight> lightsList = cache.getAllLights();
		
		PHLightState ambiLightState = new PHLightState();
		ambiLightState.setHue(HUE_VALUE);
		ambiLightState.setSaturation(250);
		ambiLightState.setBrightness(briValue);
		ambiLightState.setTransitionTime(20);
		lightIndices.forEach(lightIndex -> {
			bridge.updateLightState(lightsList.get(lightIndex), ambiLightState);	
		});
	}
	
}
