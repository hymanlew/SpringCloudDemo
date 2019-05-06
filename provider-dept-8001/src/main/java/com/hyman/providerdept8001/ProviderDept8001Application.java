package com.hyman.providerdept8001;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@MapperScan(basePackages = "com.hyman.providerdept8001.dao")
@EnableEurekaClient
public class ProviderDept8001Application {

	public static void main(String[] args) {
		SpringApplication.run(ProviderDept8001Application.class, args);
	}

}
