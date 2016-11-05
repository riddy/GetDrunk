package com.hackathon.getdrunk.web;

import java.util.ArrayList;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.hackathon.getdrunk.Main;
import com.hackathon.getdrunk.model.BluetoothConnection;
import com.hackathon.getdrunk.model.GoalStatus;
import com.hackathon.getdrunk.model.User;
import com.hackathon.getdrunk.model.Users;

@RestController
@EnableWebMvc
public class RestInterface {

	private static final Integer CLOSE_THRESHOLD = -60;

	@ResponseBody
	@RequestMapping(consumes = "application/json", method = RequestMethod.PUT, value = "/api/{deviceID}/closeby")
	public boolean updateCloseby(
			@PathVariable(name = "deviceID") String deviceID,
			@RequestBody BluetoothConnection closeby) {
		if (!closeby.isIs_close_by()) {
			System.out.println("User " + deviceID + " is gone.");
			Main.getEdgeRouter().userIsClose = false;
			// FIXME: call light
			return false;
		}
		if (closeby.getRssi()> CLOSE_THRESHOLD) {
			// FIXME: call light bright
		} else {
			// FIXME: call light dark
		}
		

		Main.getEdgeRouter().userIsClose = true;

		System.out.println("Received something! " + deviceID
				+ closeby.isIs_close_by()+ closeby.getRssi());
		return true;
	}

	@ResponseBody
	@RequestMapping(method = RequestMethod.GET, value = "/api")
	public boolean test() {

		System.out.println("Received something!");
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

		System.out.println("Return goal! " + status.getGoal()
				+ status.isHydration_alert());
		return status;
	}
}
