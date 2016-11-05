package com.hackathon.getdrunk.rest;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.hackathon.getdrunk.model.BluetoothConnection;

@RestController
@EnableWebMvc
public class RestInterface {

	private static final Integer CLOSE_THRESHOLD = -60;

	@ResponseBody
	@RequestMapping(consumes = "application/json", method = RequestMethod.PUT, value = "/api/{deviceID}/closeby")
	public boolean updateCloseby(
			@PathVariable(name = "deviceID") String deviceID,
			@RequestBody BluetoothConnection closeby) {
		if (!closeby.isInrange()) {
			System.out.println("User " + deviceID + " is gone.");
			// FIXME: call light
			return false;
		}
		if (closeby.getRSSI() > CLOSE_THRESHOLD) {
			// FIXME: call light bright
		} else {
			// FIXME: call light dark
		}

		System.out.println("Received something! " + deviceID
				+ closeby.isInrange() + closeby.getRSSI());
		return true;
	}

	@ResponseBody
	@RequestMapping(method = RequestMethod.GET, value = "/api")
	public boolean test() {

		System.out.println("Received something!");
		return true;
	}
}
