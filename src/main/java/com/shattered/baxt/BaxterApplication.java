package com.shattered.baxt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.shattered.baxt"})
public class BaxterApplication {

	public static void main(String[] args) {
		SpringApplication.run(BaxterApplication.class, args);
	}

}