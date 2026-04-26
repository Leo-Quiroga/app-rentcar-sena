package com.autoreserve.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ServletComponentScan
@EnableScheduling
public class AutoreserveBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(AutoreserveBackendApplication.class, args);
	}

}
