package com.cardio.cardiotrack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class CardiotrackApplication {

	public static void main(String[] args) {
		SpringApplication.run(CardiotrackApplication.class, args);
	}

}
