package com.hackathon.getdrunk.rest;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.hackathon.getdrunk.model.BluetoothConnection;
import com.hackathon.getdrunk.model.GoalStatus;

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
			// FIXME: call light
			return false;
		}
		if (closeby.getRssi()> CLOSE_THRESHOLD) {
			// FIXME: call light bright
		} else {
			// FIXME: call light dark
		}

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

	@ResponseBody
	@RequestMapping(produces = "application/json", method = RequestMethod.GET, value = "/api/{deviceID}/goals")
	public GoalStatus getGoalStatus(
			@PathVariable(name = "deviceID") String deviceID) {

		GoalStatus status = new GoalStatus();
		status.setGoal((int) (Math.random() * 100));
		status.setHydration_alert(Math.random() > 0.5 ? true : false);

		System.out.println("Return goal! " + status.getGoal()
				+ status.isHydration_alert());
		return status;
	}
}
