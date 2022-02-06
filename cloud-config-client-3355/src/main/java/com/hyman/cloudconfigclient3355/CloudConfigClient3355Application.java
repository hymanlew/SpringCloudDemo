package com.hyman.cloudconfigclient3355;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * 当使用 config 配置中心的远程配置文件时，则所有的客户端就必须使用 bootstrap.yml 文件，从而优先拉取远程配置并覆盖本地配置。
 */
@SpringBootApplication
@EnableEurekaClient
@EnableDiscoveryClient
public class CloudConfigClient3355Application {

	public static void main(String[] args) {
		SpringApplication.run(CloudConfigClient3355Application.class, args);
	}

}
