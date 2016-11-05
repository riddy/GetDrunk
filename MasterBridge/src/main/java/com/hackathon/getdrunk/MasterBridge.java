package com.hackathon.getdrunk;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hackathon.getdrunk.model.User;

public class MasterBridge implements GlassTriggerListener{
	
	// Constants
	final static Logger logger = LogManager.getLogger(MasterBridge.class.getName());
	public static final String PLATFORM = "EDGEROUTER";
	public static final String CONFIG_FILE_NAME = "config.json";
	public static final String HARDWARE_INFO_FILE_NAME = "hardwareinfo.json";
	public static final String KEYSTORE_FILE_NAME = "keystore.jks";
	public static String ENROLLMENT_FILE_NAME = "enrollment.json";
	
	public Tcu tcu = new Tcu();
	public HueHue hue = new HueHue();
	public DistanceTrigger distanceTrigger = new DistanceTrigger();
	
	private State currentState = State.IDLE;
	public User currentCloseUser = null;
	
	public enum State{
		IDLE,
		CLOSE_DEHYDRATED,
		CLOSE_NOT_THIRSTY,
		WATER_RUNNING,
		PARTY
	}
	

	public static final Boolean ENABLE_TCU = false;
	public static final Boolean ENABLE_HUE = true;
	public static final Boolean ENABLE_PRINT = false;

	
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
		
		hue.setLightsIdle();
		
	}
	

	private void connectToTcu() {
		try{
			tcu.connect();
			
		} catch(Throwable t){
			System.out.println(t);
		}
		
		System.out.println("Connected to TCU");
	}
	
	public void ChangeState(State newState, User user){
		
		if(user == null) user = new User("dummy", "sdf");
		
		if(currentState == State.IDLE){
			if(newState == State.CLOSE_DEHYDRATED){
				hue.setLightsCloseDehydrated();
				user.setIsClose(true);
				currentState = newState;
			} else if(newState == State.CLOSE_NOT_THIRSTY){
				hue.setLightsCloseNotThirsty();
				user.setIsClose(true);
				currentState = newState;
			}
		} else if (currentState == State.CLOSE_DEHYDRATED || currentState == State.CLOSE_NOT_THIRSTY){
			if(newState == State.IDLE){
				hue.setLightsIdle();
				user.setIsClose(false);
				currentState = newState;
			} else if (newState == State.WATER_RUNNING){
				hue.setLightsWaterRunning();
				user.setIsClose(true);
				currentState = newState;
			}
		} else if (currentState == State.WATER_RUNNING){
			if(newState == State.CLOSE_NOT_THIRSTY){
				hue.setLightsCloseNotThirsty();
				user.setIsClose(true);
				currentState = newState;
			} else if(newState == State.IDLE){
				hue.setLightsIdle();
				user.setIsClose(false);
				currentState = newState;
			} else if (newState == State.PARTY){
				hue.setLightsParty();
				user.setIsClose(true);
				currentState = newState;
			}
		} else if (currentState == State.PARTY){
			if(newState == State.CLOSE_NOT_THIRSTY){
				hue.setLightsCloseNotThirsty();
				user.setIsClose(true);
				currentState = newState;
			}
		}
		
		
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
				if(currentCloseUser != null){
					System.out.println("Filling glass");
					tcu.pourGlassAmbientWater(currentCloseUser);
					lastGlassFillTime = System.currentTimeMillis();
				}
			}
		}
		
		if(!glassAvailable){
			glassWasOnStand = false;
		}
	}
}
