package com.hackathon.getdrunk.model;

import java.util.ArrayList;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class Users {
	
	static ArrayList<User> users = new ArrayList<User>();
	
	static {
		//Add users
		addUser(new User("Julia", "3"));
		addUser(new User("Marita", "2"));
		addUser(new User("Bennjamin", "01725820865"));
		addUser(new User("Marius", "4"));

		users.get(0).setGlasses(0);
		users.get(1).setGlasses(0);
		users.get(2).setGlasses(3);
		users.get(3).setGlasses(3);
	}

	public static void addUser(User user) {
		users.add(user);
	}

	public static User getUserById(String userId) {
		for(User u : users){
			if(u.getDeviceId().equals(userId)){
				return u;
			}
		}
		return null;
	}
	
	public static ArrayList<User> getUsers(){
		return users;
	}
	
	public static void sendAllusers(){
		/*for(User user : users){
			try {
				String result = Unirest.put(
						"https://drink-4-fit.apps.bosch-iot-cloud.com/api/highscore/{user}"
						).asString().getBody();
			} catch (UnirestException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}*/
	}
}
