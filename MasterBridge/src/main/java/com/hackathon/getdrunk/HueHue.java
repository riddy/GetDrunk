package com.hackathon.getdrunk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.philips.lighting.hue.listener.PHLightListener;
import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHBridgeSearchManager;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.PHSDKListener;
import com.philips.lighting.hue.sdk.heartbeat.PHHeartbeatManager;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHBridgeResource;
import com.philips.lighting.model.PHBridgeResourcesCache;
import com.philips.lighting.model.PHGroup;
import com.philips.lighting.model.PHHueError;
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
	
	private void cycleLights() {
		Random random = new Random();
		ArrayList<Integer> colors = (ArrayList<Integer>) Arrays.asList(47920, 45920, 8265);
		while (cycleAmbiLights) {
			setLights(0, colors.get(random.nextInt(colors.size())));
		}
	}
	
	private void setLights(int waterLightHue, int ambiLightHue) {
		PHBridge bridge = hueInstance.getSelectedBridge();
		
		PHBridgeResourcesCache cache = bridge.getResourceCache();
		List<PHLight> lightsList = cache.getAllLights();
		
		if (ambiLightHue > 0) {
			PHLightState ambiLightState = new PHLightState();
			ambiLightState.setHue(ambiLightHue);
			ambiLightState.setSaturation(250);
			bridge.updateLightState(lightsList.get(1), ambiLightState, getLightListener());
			bridge.updateLightState(lightsList.get(2), ambiLightState, getLightListener());
		}
		
		if (waterLightHue > 0) {
			PHLightState waterLightState = new PHLightState();
			waterLightState.setHue(waterLightHue);
			waterLightState.setSaturation(250);
			bridge.updateLightState(lightsList.get(0), waterLightState, getLightListener());
		}
		
	}
	
	
	
	public void setLightsIdle() {
		System.out.println("setLightsIdle()");
		setLights(45920, 0);
		cycleAmbiLights = true;
		cycleLights();
//		PHBridge bridge = hueInstance.getSelectedBridge();
//		
//		PHBridgeResourcesCache cache = bridge.getResourceCache();
//		List<PHLight> lightsList = cache.getAllLights();
//		
//		PHLightState lightState = new PHLightState();
//		lightState.setEffectMode(PHLightEffectMode.EFFECT_COLORLOOP);
//		
//		bridge.updateLightState(lightsList.get(1), lightState);
//		bridge.updateLightState(lightsList.get(2), lightState);
	}
	
	public void setLightsApproaching() {
		System.out.println("setLightsApproaching()");
		setLights(25500, 42920);
	}
	
	public void setLightsWaterRunning() {
		System.out.println("setLightsWaterRunning()");
		setLights(5000, 42920);
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		setLightsIdle();
	}

	public void setLightsParty() {
		System.out.println("setLightsParty()");
	}
	
	private PHLightListener getLightListener() {
		PHLightListener listener = new PHLightListener() {
			
			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				System.out.println("onSuccess");
			}
			
			@Override
			public void onStateUpdate(Map<String, String> successAttribute, List<PHHueError> errorAttribute) {
				// TODO Auto-generated method stub
				System.out.println("onStateUpdate");
			}
			
			@Override
			public void onError(int code, String message) {
				System.out.println("error with light - " + message);
			}
			
			@Override
			public void onSearchComplete() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onReceivingLights(List<PHBridgeResource> lights) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onReceivingLightDetails(PHLight light) {
				// TODO Auto-generated method stub
				
			}
		};
		
		return listener;
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
