package com.hackathon.getdrunk;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Bridge implements GlassTriggerListener{
	
	// Constants
	final static Logger logger = LogManager.getLogger(Bridge.class.getName());
	public static final String PLATFORM = "EDGEROUTER";
	public static final String CONFIG_FILE_NAME = "config.json";
	public static final String HARDWARE_INFO_FILE_NAME = "hardwareinfo.json";
	public static final String KEYSTORE_FILE_NAME = "keystore.jks";
	public static String ENROLLMENT_FILE_NAME = "enrollment.json";
	
	public Webserver webserver;
	
	public Tcu tcu;
	
	public DistanceSensor sensor = new DistanceSensor();
	
	
	public Boolean userIsClose = false;

	
	public Bridge() {
	}
	
	//TODO: synchronize all lists that are modified to prevent threading errors
	
	public void start(){
		
		//Register IR sensor
		sensor.registerListener(this);
		
//		try{
//			tcu = new Tcu();
//			
//			tcu.connect();
//			
//			//tcu.send("pwd");
//		
//		
//		} catch(Throwable t){
//			System.out.println(t);
//		}
		
		webserver = new Webserver();
		webserver.start();
	}
	

	public Thread shutdown() {
		Thread shutdownThread = new Thread() {
			@Override
			public void run() {
				stopEdgeRouter();
			}
		};
		shutdownThread.start();
		return shutdownThread;
	}
	
	public void stopEdgeRouter() {
		logger.info("Shutting down...");
		webserver.shutdown();
	}
	
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
					//tcu.pourAmbientWater();
					lastGlassFillTime = System.currentTimeMillis();
				}
			}
		}
		
		if(!glassAvailable){
			glassWasOnStand = false;
		}
	}
}
