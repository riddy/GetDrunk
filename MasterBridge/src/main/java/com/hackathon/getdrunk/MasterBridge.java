package com.hackathon.getdrunk;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MasterBridge implements GlassTriggerListener{
	
	// Constants
	final static Logger logger = LogManager.getLogger(MasterBridge.class.getName());
	public static final String PLATFORM = "EDGEROUTER";
	public static final String CONFIG_FILE_NAME = "config.json";
	public static final String HARDWARE_INFO_FILE_NAME = "hardwareinfo.json";
	public static final String KEYSTORE_FILE_NAME = "keystore.jks";
	public static String ENROLLMENT_FILE_NAME = "enrollment.json";
	
	public Webserver webserver;
	
	public Tcu tcu = new Tcu();
	public HueHue hue = new HueHue();
	public DistanceTrigger distanceTrigger = new DistanceTrigger();
	
	
	//Global variable if any user is close
	public Boolean userIsClose = false;
	

	public static final Boolean ENABLE_TCU = false;
	public static final Boolean ENABLE_HUE = false;

	
	public MasterBridge() {
		
	}
	
	//TODO: synchronize all lists that are modified to prevent threading errors
	
	public void start(){
		
		//Register IR sensor
		distanceTrigger.initDistanceTrigger(this);
		
		//Connect to TCU
		//connectToTcu();
		
		//Initialize Hue control
		hue.initHueHue();
		
		
//		webserver = new Webserver();
//		webserver.start();
	}
	

	private void connectToTcu() {
		try{
			tcu.connect();
			
		} catch(Throwable t){
			System.out.println(t);
		}
		
		System.out.println("Connected to TCU");
	}
	
	
	//########################################## GLASS FILLING
	
	private Boolean glassWasOnStand = false;
	private double lastGlassFillTime = 0;

	/**
	 * Checks if a new glass should be filled
	 */
	@Override
	public void glassState(Boolean glassAvailable) {
		
		if(glassAvailable && !glassWasOnStand){
			glassWasOnStand = true;
			if(System.currentTimeMillis() - lastGlassFillTime > 10 * 1000){
				if(userIsClose){
					System.out.println("Filling glass");
					tcu.pourGlassAmbientWater();
					lastGlassFillTime = System.currentTimeMillis();
				}
			}
		}
		
		if(!glassAvailable){
			glassWasOnStand = false;
		}
	}
}
