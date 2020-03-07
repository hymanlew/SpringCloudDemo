package com.hyman.cloudsidecar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.sidecar.EnableSidecar;

/**
 * Sidecar 可以使得 springcloud 接入非 java 语言编写的应用程序
 */
@SpringBootApplication
@EnableSidecar
public class CloudSidecarApplication {

	public static void main(String[] args) {
		SpringApplication.run(CloudSidecarApplication.class, args);
	}

}
