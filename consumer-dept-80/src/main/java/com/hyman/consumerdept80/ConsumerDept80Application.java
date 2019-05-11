package com.hyman.consumerdept80;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class ConsumerDept80Application {

	public static void main(String[] args) {
		SpringApplication.run(ConsumerDept80Application.class, args);
	}

}
