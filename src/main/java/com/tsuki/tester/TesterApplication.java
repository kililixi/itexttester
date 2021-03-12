package com.tsuki.tester;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"com.demo.starter.config", "com.tsuki.tester"})
public class TesterApplication {
	public static void main(String[] args) {
		SpringApplication.run(TesterApplication.class, args);
	}

}
