package com.hackathon.getdrunk;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

import com.hackathon.getdrunk.MasterBridge.State;
import com.hackathon.getdrunk.model.User;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class Tcu {
	private JSch jsch;
	private Session session;

	public final static String READ_VAAR = "/usr/local/bin/tcuclient -c \"var read ";
	public final static String READ_PARAM = "/usr/local/bin/tcuclient -c \"var read ksip.kopf.parameter.";
	public final static String WRITE_OPTION = "/usr/local/bin/tcuclient -c \"var write ksip.kopf.output.";
	public final static String EOL = "\"";

	public static final int V_IN = 0;
	public static final int V_OUT = 1;
	public static final int V_AMBIENT = 2;
	public static final int V_COLD = 3;
	public static final int V_HYG1 = 16;

	public Tcu() {
		// TODO Auto-generated constructor stub
	}
	
	public void connect() throws JSchException, IOException{
		jsch = new JSch();
		java.util.Properties config = new java.util.Properties(); 
		config.put("StrictHostKeyChecking", "no");
		session = jsch.getSession("root", "192.168.100.99", 22);
		session.setConfig(config);
		session.setPassword("KyQETdMx8xTHAS{R");
		session.setTimeout(5000);
		session.connect();
		
		System.out.println("Connected to TCU");

	}
	
	/**
	 * Sends a command to the TCU
	 * @param message
	 * @return
	 */
	public String send(String message){
		System.out.println("TCU message "+message);
		
		if(!MasterBridge.ENABLE_TCU) return "1";
		
		try {
			ChannelExec channel=(ChannelExec) session.openChannel("exec");
			BufferedReader in = new BufferedReader(new InputStreamReader(channel.getInputStream()));
			BufferedReader error = new BufferedReader(new InputStreamReader(channel.getErrStream()));
			channel.setCommand(message);
			channel.connect();
	
			String result = "";
			String msg = null;
			while((msg=in.readLine())!=null){
				result += msg;
			}
			while((msg=error.readLine())!=null){
				result += msg;
			}
			System.out.println("TCU result "+result);
			
			in.close();
			error.close();
			channel.disconnect();
			
			return result;
		} catch (Throwable t){
			return "error";
		}
	}
	
	public String getSimState(){
		return send(READ_VAAR+"tcu.networkmanager.simstate"+EOL);
	}
	
	public String getTotalWater(){
		return send(READ_PARAM+"19"+EOL);
	}
	
	public Boolean activateSoftwareControl(){
		Boolean result = false;
		int failCounter = 0;
				
		while(!result){
			if(failCounter > 10) return false;
			String sendResult = send(WRITE_OPTION+"29 1"+EOL);
			result = sendResult.equals("1");
			failCounter++;
			if(!MasterBridge.ENABLE_TCU) break;
		}
		return true;
	}
	
	public Boolean disableSoftwareControl(){
		Boolean result = false;
		int failCounter = 0;
				
		while(!result){
			if(failCounter > 10) return false;
			String sendResult = send(WRITE_OPTION+"29 0"+EOL);
			result = sendResult.equals("0");
			failCounter++;
			if(!MasterBridge.ENABLE_TCU) break;
		}
		return true;
	}

	/**
	 * Pours a glass of ambient water
	 */
	public Boolean pourGlassAmbientWater(User user){
		Boolean result = false;
		result = activateSoftwareControl();
		result = triggerValve(V_IN, true);
		result = triggerValve(V_OUT, true);
		result = triggerValve(V_AMBIENT, true);
		
		Main.getMasterBridge().ChangeState(State.WATER_RUNNING, user);
		
		try {
			Thread.sleep(3000);
		} catch (Exception e){
			
		}
		
		user.addGlass();
		
		//This enables us to leave to water running state

		//Stop Water
		if(user.getGlasses() == 2){
			LabelPrinter.printAward("award1");
			Main.getMasterBridge().ChangeState(State.PARTY, user);
		} else if (user.getGlasses() == 4){
			LabelPrinter.printAward("award2");
			Main.getMasterBridge().ChangeState(State.PARTY, user);
		} else {
			Main.getMasterBridge().ChangeState(State.WATER_RUNNING_END, user);
			Main.getMasterBridge().ChangeState(State.CLOSE_NOT_THIRSTY, user);
		}

		result = triggerValve(V_AMBIENT, false);
		result = triggerValve(V_OUT, false);
		result = triggerValve(V_IN, false);
		result = disableSoftwareControl();
		
		return true;
	}
	
	/**
	 * Will open or close a valve
	 */
	public Boolean triggerValve(int port, Boolean open){
		Boolean result = false;
		int openInt = open ? 1 : 0;
		int failCounter = 0;
		
		if(!Arrays.asList(V_IN,V_OUT,V_AMBIENT,V_COLD,V_HYG1).contains(port)){
			System.out.println("Wrong valve");
			return false;
		}
				
		while(!result){
			if(failCounter > 10) return false;
			String sendResult = send(WRITE_OPTION+port+" "+openInt+EOL);
			result = sendResult.equals(""+openInt);
			failCounter++;
			if(!MasterBridge.ENABLE_TCU) break;
		}
		return true;
	}
}
