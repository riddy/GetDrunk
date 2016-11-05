package com.hackathon.getdrunk;

import java.util.ArrayList;

public class Users {
	
	static ArrayList<User> users = new ArrayList<User>();
	
	private Users() {
		
	}

	public static void addUser(User user) {
		users.add(new User("YWJiOWViZjIyYTNmNGVlYyMxNDc4MzU4NzU2MDAw"));
		//users.add(new User(""));
	}

	public static User getUserById(String userId) {
		for(User u : users){
			if(u.getDeviceId().equals(userId)){
				return u;
			}
		}
		return null;
	}
}
