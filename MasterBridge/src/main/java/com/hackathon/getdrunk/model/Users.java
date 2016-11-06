package com.hackathon.getdrunk.model;

import java.util.ArrayList;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class Users {
	
	static ArrayList<User> users = new ArrayList<User>();
	
	static {
		//Add users
		User benni = new User("Benjamin", "01725820865");
		User julia = new User("Julia", "2");
		User marita = new User("Marita", "default_marita");
		User marius = new User("Marius", "4");

		benni.setGlasses(2);
		//benni.setLastGlassTime(System.currentTimeMillis());
		julia.setGlasses(0);
		marita.setGlasses(3);
		marius.setGlasses(3);

		addUser(benni);
		addUser(julia);
		addUser(marita);
		addUser(marius);
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
