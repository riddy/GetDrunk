package com.hackathon.getdrunk;

import java.util.Arrays;
import java.util.List;

import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHBridgeSearchManager;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.PHSDKListener;
import com.philips.lighting.hue.sdk.heartbeat.PHHeartbeatManager;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHBridgeResourcesCache;
import com.philips.lighting.model.PHGroup;
import com.philips.lighting.model.PHHueParsingError;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLight.PHLightAlertMode;
import com.philips.lighting.model.PHLightState;

public class HueHue {
	static PHAccessPoint accessPoint;
	static String username = "7f52cac884895324249d3f33fa06ce7";
	
	static String bridgeIp = "192.168.0.37";
	
	static PHHeartbeatManager heartbeatManager;
	
	static PHBridge bridge;
	
	static PHBridgeResourcesCache cache;
	
	static PHGroup ambilightGroup;
	static PHGroup waterlightGroup;
	
	PHHueSDK hueInstance;
	PHSDKListener hueListener;
	
	boolean cycleAmbiLights = false;
	
	CycleLightsThread cycleAmbiLightsThread;
	
	final int AMBI_WATER_HUE = 46945;
	
	final int WATERLIGHT_IDLE = 48225;
	final int WATERLIGHT_APROACHING_GOOD = 43137;
	final int WATERLIGHT_APROACHING_BAD = 7137;
	final int WATERLIGHT_RUNNING = 46593;

	public HueHue() {
		
		hueInstance = PHHueSDK.getInstance();
	}
	
	public void initHueHue() {
		System.out.println("initHueHue");
		hueListener = getHueListener();
		
		hueInstance.getNotificationManager().registerSDKListener(hueListener);
		
		PHBridgeSearchManager sm = (PHBridgeSearchManager) hueInstance.getSDKService(PHHueSDK.SEARCH_BRIDGE);
		sm.search(true, true);
	}
	
	private void setLights(int waterLightHue, int ambiLightHue) {
		if(!MasterBridge.ENABLE_HUE) return;
		
		PHBridge bridge = hueInstance.getSelectedBridge();
		
		PHBridgeResourcesCache cache = bridge.getResourceCache();
		List<PHLight> lightsList = cache.getAllLights();
		
		if (ambiLightHue >= 0) {
			
			if (cycleAmbiLights) {
				cycleAmbiLightsThread.pause();
				cycleAmbiLights = false;
			}
			
			if (ambiLightHue == 0) {
				ambiLightHue = lightsList.get(1).getLastKnownLightState().getHue();
			}
			
			PHLightState ambiLightState = new PHLightState();
			ambiLightState.setHue(ambiLightHue);
			ambiLightState.setSaturation(250);
			bridge.updateLightState(lightsList.get(1), ambiLightState);
			bridge.updateLightState(lightsList.get(2), ambiLightState);
		}
		
		if (waterLightHue >= 0) {
			if (waterLightHue == 0) {
				waterLightHue = lightsList.get(0).getLastKnownLightState().getHue();
			}
			PHLightState waterLightState = new PHLightState();
			waterLightState.setHue(waterLightHue);
			waterLightState.setSaturation(250);
			bridge.updateLightState(lightsList.get(0), waterLightState);
		}
		
	}
	
	private void cycleAmbilightsIdle() {
		List<Integer> colors = Arrays.asList(47920, 45920, 8265);
		List<Integer> lightIndices = Arrays.asList(1,2);
		if (cycleAmbiLights && cycleAmbiLightsThread != null) {
			cycleAmbiLightsThread.pause();
			cycleAmbiLights = false;
		}
		if (cycleAmbiLightsThread == null) {
			cycleAmbiLightsThread = new CycleLightsThread(colors, hueInstance, lightIndices);
		} else {
			cycleAmbiLightsThread.updateParams(colors, hueInstance, lightIndices);
		}
		cycleAmbiLights = true;
		cycleAmbiLightsThread.resumeCycle();
	}
	
	
	public void setLightsIdle() {
		System.out.println("setLightsIdle()");
		setLights(WATERLIGHT_IDLE, -1);
		cycleAmbilightsIdle();
	}
	
	public void setLightsCloseNotThirsty(double currentDistance) {
		System.out.println("setLightsApproachingGood()");
		setLights(WATERLIGHT_APROACHING_GOOD, 0);
	}
	
	public void setLightsCloseDehydrated(double currentDistance) {
		System.out.println("setLightsApproachingBad()");
		setLights(WATERLIGHT_APROACHING_BAD, 0);
	}
	
	public void setLightsWaterRunning() {
		System.out.println("setLightsWaterRunning()");
		setLights(WATERLIGHT_RUNNING, 0);
	}

	public void setLightsParty() {
		System.out.println("setLightsParty()");
	}
	
	private PHSDKListener getHueListener() {
		PHSDKListener listener = new PHSDKListener() {
			
			@Override
			public void onParsingErrors(List<PHHueParsingError> arg0) {
				
			}
			
			@Override
			public void onError(int arg0, String arg1) {
				
			}
			
			@Override
			public void onConnectionResumed(PHBridge arg0) {
				
			}
			
			@Override
			public void onConnectionLost(PHAccessPoint arg0) {
				
			}
			
			@Override
			public void onCacheUpdated(List<Integer> arg0, PHBridge arg1) {
				
			}
			
			@Override
			public void onBridgeConnected(PHBridge bridge, String usernameNew) {
				
				username = usernameNew;
				
				hueInstance.setSelectedBridge(bridge);
				System.out.println("bridge connected");
				
				hueInstance.enableHeartbeat(bridge, PHHueSDK.HB_INTERVAL);
				
				cache = hueInstance.getSelectedBridge().getResourceCache();
				
				
				List<PHLight> lightsList = cache.getAllLights();
				
				PHLightState lightState = new PHLightState();
				lightState.setAlertMode(PHLightAlertMode.ALERT_SELECT);
				lightsList.forEach(currentLight -> {
					bridge.updateLightState(currentLight, lightState);
				});
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				setLightsIdle();
			}
			
			@Override
			public void onAuthenticationRequired(PHAccessPoint arg0) {
				System.out.println("authentication requiered!");
				hueInstance.startPushlinkAuthentication(accessPoint);
			}
			
			@Override
			public void onAccessPointsFound(List<PHAccessPoint> arg0) {
				System.out.println("Access Point Found");
				if(arg0.size() > 0) {
					accessPoint = arg0.get(0);
				}
				hueInstance.connect(accessPoint);
			}
		};
		
		return listener;
	}
	
	
}
