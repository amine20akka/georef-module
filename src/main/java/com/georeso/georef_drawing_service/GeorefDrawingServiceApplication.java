package com.georeso.georef_drawing_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.georeso.georef_drawing_service.georef")
public class GeorefDrawingServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(GeorefDrawingServiceApplication.class, args);
	}

}
