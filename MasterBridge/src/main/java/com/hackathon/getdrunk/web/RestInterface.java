package com.hackathon.getdrunk.web;

import java.util.ArrayList;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.hackathon.getdrunk.LabelPrinter;
import com.hackathon.getdrunk.Main;
import com.hackathon.getdrunk.MasterBridge.State;
import com.hackathon.getdrunk.model.BluetoothConnection;
import com.hackathon.getdrunk.model.GoalStatus;
import com.hackathon.getdrunk.model.User;
import com.hackathon.getdrunk.model.Users;

@RestController
@EnableWebMvc
public class RestInterface {

	private static final Integer CLOSE_THRESHOLD = -70;

	@ResponseBody
	@RequestMapping(consumes = "application/json", method = RequestMethod.PUT, value = "/api/{deviceID}/closeby")
	public boolean updateCloseby(
			@PathVariable(name = "deviceID") String deviceID,
			@RequestBody BluetoothConnection closeby) {
		

		System.out.print("d "+closeby.getRssi()+",");
		
		User user = Users.getUserById(deviceID);
		

		float rssi = closeby.getRssi()*1.0f + 80;
		if(rssi <= 0) rssi = 1;
		if(rssi >= 30) rssi = 30;
		double distance = rssi / 30;
		
		Main.getMasterBridge().hue.setDistance(distance);
		
		if (closeby.getRssi() < -75 || closeby.getRssi() == 0) {
			System.out.println("User " + deviceID + " is gone.");
			Main.getMasterBridge().currentCloseUser = null;
						
			Main.getMasterBridge().ChangeState(State.IDLE, user);
			
			return false;
		} else {

		Main.getMasterBridge().currentCloseUser = user;
			if(user.isDehydrated()){
				Main.getMasterBridge().ChangeState(State.CLOSE_DEHYDRATED, user);
			} else {
				Main.getMasterBridge().ChangeState(State.CLOSE_NOT_THIRSTY, user);
			}
		}
		

		System.out.print("c");
//		System.out.println("Received something! " + deviceID
//				+ closeby.isIs_close_by()+ closeby.getRssi());
		return true;
	}


	/**
		 * Called to retrieve the current goals and if the 
		 */
		@ResponseBody
		@RequestMapping(produces = "application/json", method = RequestMethod.GET, value = "/api/{deviceID}/goals")
		public GoalStatus getGoalStatus(
				@PathVariable(name = "deviceID") String deviceID) {
			
			ArrayList<User> users = Users.getUsers();
			
			User user = Users.getUserById(deviceID);
	
			GoalStatus status = new GoalStatus();
			status.setGoal(user.getGlassesPercent());
			status.setHydration_alert(user.isDehydrated());
	
			System.out.print("g");
	//		System.out.println("Return goal! " + status.getGoal()
	//				+ status.isHydration_alert());
			return status;
		}

	@ResponseBody
	@RequestMapping(method = RequestMethod.GET, value = "/api")
	public boolean test() {

		System.out.println("API TEST!");
		return true;
	}
	


	@ResponseBody
	@RequestMapping(method = RequestMethod.GET, value = "/api/test/idle")
	public boolean start() {

		Main.getMasterBridge().setState(State.IDLE);
		return true;
	}
	
	@ResponseBody
	@RequestMapping(method = RequestMethod.GET, value = "/api/test/thirsty")
	public boolean thirsty() {
		User user;
		if(Main.getMasterBridge().currentCloseUser == null) user = Users.getUsers().get(0);
		else user = Main.getMasterBridge().currentCloseUser;
		
		Main.getMasterBridge().ChangeState(State.CLOSE_DEHYDRATED, user);
		return true;
	}
	
	@ResponseBody
	@RequestMapping(method = RequestMethod.GET, value = "/api/test/notthirsty")
	public boolean notthirsty() {
		User user;
		if(Main.getMasterBridge().currentCloseUser == null) user = Users.getUsers().get(0);
		else user = Main.getMasterBridge().currentCloseUser;
		
		Main.getMasterBridge().ChangeState(State.CLOSE_NOT_THIRSTY, user);
		return true;
	}
	
	@ResponseBody
	@RequestMapping(method = RequestMethod.GET, value = "/api/test/pour")
	public boolean pour() {
		User user;
		if(Main.getMasterBridge().currentCloseUser == null) user = Users.getUsers().get(0);
		else user = Main.getMasterBridge().currentCloseUser;
		
		Main.getMasterBridge().tcu.pourGlassAmbientWater(user);
		return true;
	}
	
	@ResponseBody
	@RequestMapping(method = RequestMethod.GET, value = "/api/test/partyend")
	public boolean partyend() {
		User user;
		if(Main.getMasterBridge().currentCloseUser == null) user = Users.getUsers().get(0);
		else user = Main.getMasterBridge().currentCloseUser;
		
		Main.getMasterBridge().ChangeState(State.PARTY_END, user);
		return true;
	}
	
	
	
	
	//############################# TEST endpoints
	
	@ResponseBody
	@RequestMapping(method = RequestMethod.GET, value = "/api/test/print1")
	public boolean print1() {
		LabelPrinter.printAward("award1");
		return true;
	}
	
	@ResponseBody
	@RequestMapping(method = RequestMethod.GET, value = "/api/test/print2")
	public boolean print2() {
		LabelPrinter.printAward("award2");
		return true;
	}
	
	@ResponseBody
	@RequestMapping(method = RequestMethod.GET, value = "/api/test/sim")
	public boolean testSim() {
		return Main.getMasterBridge().tcu.getSimState().equals("READY");
	}
	
	
	
//	@ResponseBody
//	@RequestMapping(produces = "application/json",
//					consumes = "application/json",
//					method = RequestMethod.PUT,
//					value = "/api/{deviceID}/closeby")
//	public GoalStatus updateCloseby(
//			@PathVariable(name = "deviceID") String deviceID,
//			@RequestBody BluetoothConnection closeby) {
//		
//		//Fetch user
//		User user = Users.getUserById(deviceID);
//		
//		//Calculate a distance
//		float rssi = closeby.getRssi()*1.0f + 80;
//		if(rssi <= 0) rssi = 1;
//		if(rssi >= 30) rssi = 30;
//		double distance = rssi / 30;
//		Main.getMasterBridge().hue.setDistance(distance);
//		
//		//User is not close
//		if (!closeby.isIs_close_by()) {
//			System.out.println("User " + user.getName() + " is not close.");
//			Main.getMasterBridge().currentCloseUser = null;
//						
//			Main.getMasterBridge().ChangeState(State.IDLE, user);
//		}
//		//User is close
//		else {
//			Main.getMasterBridge().currentCloseUser = user;
//			
//			if(user.isDehydrated()){
//				Main.getMasterBridge().ChangeState(State.CLOSE_DEHYDRATED, user);
//			} else {
//				Main.getMasterBridge().ChangeState(State.CLOSE_NOT_THIRSTY, user);
//			}
//		}
//
//		System.out.print(".");
//		
//		
//		//Deliver the user goal
//		GoalStatus status = new GoalStatus();
//		status.setGoal(user.getGlassesPercent());
//		status.setHydration_alert(user.isDehydrated());
//
////		System.out.println("Return goal! " + status.getGoal()
////				+ status.isHydration_alert());
//		return status;
//		
//
//		
//		/*if (closeby.getRssi()> CLOSE_THRESHOLD) {
//			// FIXME: call light bright
//		} else {
//			// FIXME: call light dark
//		}*/
//	}
}
