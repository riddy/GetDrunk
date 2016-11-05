package com.hackathon.getdrunk;

import java.util.ArrayList;
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
	List<Integer> lightIndices;
	Random random = new Random();
	
	boolean isPaused = false;
	
	public CycleLightsThread(List<Integer> colorList, PHHueSDK hueInstance, List<Integer> lightIndices) {
		this.colorList = colorList;
		this.hueInstance = hueInstance;
		this.lightIndices = lightIndices;
	}
	
	public void updateParams(List<Integer> colorList, PHHueSDK hueInstance, List<Integer> lightIndices) {
		this.colorList = colorList;
		this.hueInstance = hueInstance;
		this.lightIndices = lightIndices;
	}
	
	@Override
	public void run() {
		while (true) {
			if (isPaused == false) {
				setLights(colorList.get(random.nextInt(colorList.size())));
			}
			try {
				int sleepTime = isPaused == true ? 500 : 5000;
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
