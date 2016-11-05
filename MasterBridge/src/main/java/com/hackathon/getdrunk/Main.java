package com.hackathon.getdrunk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableWebMvc
public class Main {
	
	public static Bridge edgeRouterInstance;
	
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
		edgeRouterInstance = new Bridge();
		edgeRouterInstance.start();
	}
	
	public static Bridge getEdgeRouter(){
		return edgeRouterInstance;
	}
}
