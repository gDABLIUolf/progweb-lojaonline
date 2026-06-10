package com.vesteBem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.util.TimeZone;

@SpringBootApplication
public class VesteBemApplication {

	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone("GMT-4"));
		SpringApplication.run(VesteBemApplication.class, args);
	}

}
