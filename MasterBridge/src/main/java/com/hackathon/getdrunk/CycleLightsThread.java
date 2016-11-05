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
	
	ArrayList<Integer> colorList;
	PHHueSDK hueInstance;
	ArrayList<Integer> lightIndices;
	Random random = new Random();
	
	public CycleLightsThread(ArrayList<Integer> colorList, PHHueSDK hueInstance, ArrayList<Integer> lightIndices) {
		this.colorList = colorList;
		this.hueInstance = hueInstance;
		this.lightIndices = lightIndices;
	}
	
	@Override
	public void run() {
		while (true) {
			setLights(colorList.get(random.nextInt(colorList.size())));
			try {
				Thread.sleep(5000);
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
		lightIndices.forEach(lightIndex -> {
			bridge.updateLightState(lightsList.get(lightIndex), ambiLightState);	
		});
	}
}
