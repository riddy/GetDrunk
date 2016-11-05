package com.hackathon.getdrunk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableWebMvc
public class Main {
	
	public static MasterBridge masterBridgeInstance;
	
	public static java.util.logging.Logger fileLogger = java.util.logging.Logger.getLogger("MyLog");  

	public static void main(final String args[]) throws Exception {
		Main main = new Main();
		//new JCommander(main, args);
		main.start();
		

		SpringApplication.run(Main.class, args);
	}
	
	private void start() throws Exception {
		System.out.println("Starting");
		startEdgeRouter();
	}
	
	private void startEdgeRouter() {
		masterBridgeInstance = new MasterBridge();
		masterBridgeInstance.start();
	}
	
	public static MasterBridge getMasterBridge(){
		return masterBridgeInstance;
	}
}
