package com.cokiming;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author wuyiming
 */
@EnableScheduling
@SpringBootApplication
public class SchedulemanagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SchedulemanagerApplication.class, args);
	}
}
