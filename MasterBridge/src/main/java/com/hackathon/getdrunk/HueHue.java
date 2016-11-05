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
	final int WATERLIGHT_APROACHING_NOTTHRISTY = 43137;
	final int WATERLIGHT_APROACHING_DEHYD = 7137;
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
	
	private void startAmbilight() {
		cycleAmbiLightsThread = new CycleLightsThread();
		cycleAmbiLightsThread.start();
	}
	
	private void setWaterLight(int hueValue) {
		if(!MasterBridge.ENABLE_HUE) return;
		
		PHBridge bridge = hueInstance.getSelectedBridge();
		
		PHBridgeResourcesCache cache = bridge.getResourceCache();
		List<PHLight> lightsList = cache.getAllLights();
		
		PHLightState waterLightState = new PHLightState();
		waterLightState.setHue(hueValue);
		waterLightState.setSaturation(250);
		bridge.updateLightState(lightsList.get(0), waterLightState);
	}
	
	private void startParty() {
		if(!MasterBridge.ENABLE_HUE) return;
		
	}

	public void setDistance(double distance) {
		cycleAmbiLightsThread.setDistance(distance);
	}
	
	public void setLightsIdle() {
		System.out.println("setLightsIdle()");
		setWaterLight(WATERLIGHT_IDLE);
	}
	
	public void setLightsCloseNotThirsty() {
		System.out.println("setLightsApproachingGood()");
		setWaterLight(WATERLIGHT_APROACHING_NOTTHRISTY);
	}
	
	public void setLightsCloseDehydrated() {
		System.out.println("setLightsApproachingBad()");
		setWaterLight(WATERLIGHT_APROACHING_DEHYD);
	}
	
	public void setLightsWaterRunning() {
		System.out.println("setLightsWaterRunning()");
		setWaterLight(WATERLIGHT_RUNNING);
	}

	public void setLightsParty() {
		System.out.println("setLightsParty()");
		startParty();
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
				startAmbilight();
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
