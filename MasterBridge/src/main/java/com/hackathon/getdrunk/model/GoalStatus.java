package com.hackathon.getdrunk.model;

public class GoalStatus {
	private int goal;
	private boolean hydration_alert;

	public int getGoal() {
		return goal;
	}

	public void setGoal(int goal) {
		this.goal = goal;
	}

	public boolean isHydration_alert() {
		return hydration_alert;
	}

	public void setHydration_alert(boolean hydration_alert) {
		this.hydration_alert = hydration_alert;
	}
}
