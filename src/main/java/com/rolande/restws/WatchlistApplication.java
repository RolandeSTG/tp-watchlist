package com.rolande.restws;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Our Watchlist Spring Boot application's main entry point
 * @author Rolande St-Gelais
 */

@SpringBootApplication
public class WatchlistApplication {

	public static void main(String[] args) {
		
		// {RSTG}: If, for some reasons, embedded Tomcat engine cannot get its path properly from 
		//         'application.properties', defining it as a System Property is a decent (and
		//         reliable) temporary workaround...
		
		// System.setProperty("server.servlet.context-path", "/demos");	
		// System.setProperty("cxf.jaxrs.classes-scan", "true");	 
		// System.setProperty("cxf.jaxrs.classes-scan-packages", "com.fasterxml.jackson.jakarta.rs,com.rolande.restws");
			
		SpringApplication.run(WatchlistApplication.class, args);
	}

}
