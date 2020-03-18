package com.hyman.cloudconfigbus3356;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
@EnableDiscoveryClient
public class CloudConfigBus3356Application {

	public static void main(String[] args) {
		SpringApplication.run(CloudConfigBus3356Application.class, args);
	}

}
