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
	Random random = new Random();
	
	int HUE_VALUE = 45281;
	int SAT_VALUE = 245;
	int BRI_MIN = 5;
	int BRI_MAX = 254;
	
	boolean isPaused = false;
	
	double currentDistance = -1;
	boolean lastColorMin = true;
	List<Integer> lightIndices = Arrays.asList(1,2);
	
	public CycleLightsThread() {
	}
	
	
	public void setDistance(double currentDistance) {
		this.currentDistance = currentDistance;
	}
	
	@Override
	public void run() {
		while (true) {
			int colorValue;
			if (isPaused == false) {
				if (currentDistance < 0) {
					if (lastColorMin) {
						colorValue = BRI_MIN + 100;
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
			}
			try {
				int sleepTime = 500;
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void setLights(int ambiLightHue) {
		PHBridge bridge = hueInstance.getSelectedBridge();
		
		PHBridgeResourcesCache cache = bridge.getResourceCache();
		List<PHLight> lightsList = cache.getAllLights();
		
		PHLightState ambiLightState = new PHLightState();
		ambiLightState.setHue(ambiLightHue);
		ambiLightState.setSaturation(250);
		ambiLightState.setTransitionTime(100);
		lightIndices.forEach(lightIndex -> {
			bridge.updateLightState(lightsList.get(lightIndex), ambiLightState);	
		});
	}
	
	public void pause() {
		isPaused = true;
	}
	
	public void resumeCycle() {
		isPaused = false;
	}
}
