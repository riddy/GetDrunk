package com.hackathon.getdrunk.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@RestController
@EnableWebMvc
public class RestInterface {

	private static Map<String, List<Date>> highscore = new HashMap<String, List<Date>>();

	@ResponseBody
	@RequestMapping(method = RequestMethod.POST, value = "/api/highscore/{user}")
	public boolean addGlass(@PathVariable(name = "user") String userName) {
		List<Date> glasses = highscore.get(userName);
		if (glasses == null) {
			glasses = new ArrayList<>();
		}
		glasses.add(new Date());
		highscore.put(userName, glasses);
		return true;
	}

	@ResponseBody
	@RequestMapping(produces = "application/json", method = RequestMethod.GET, value = "/api/highscore")
	public Map getHighscore() {

		return highscore;
	}


	@ResponseBody
	@RequestMapping(method = RequestMethod.GET, value = "/api")
	public boolean test() {

		System.out.println("Received something!");
		return true;
	}

	@ResponseBody
	@RequestMapping(method = RequestMethod.GET, value = "/api/highscore/result")
	public String fetchWinner() {
		String winnerName = "";
		int mostGlasses = 0;
		for (Entry<String, List<Date>> glasses : highscore.entrySet()) {
			int size = glasses.getValue().size();
			if (size > mostGlasses) {
				mostGlasses = size;
				winnerName = glasses.getKey();
			}
		}
		System.out.println("Winner is: " + winnerName);
		return winnerName;
	}

}
