package com.hackathon.getdrunk.model;

import java.util.ArrayList;

public class Users {
	
	static ArrayList<User> users = new ArrayList<User>();
	
	static {
		//Add users
		addUser(new User("YWJiOWViZjIyYTNmNGVlYyMxNDc4MzU4NzU2MDAw"));
		addUser(new User("sdf"));
		addUser(new User("sdggds"));
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
}
