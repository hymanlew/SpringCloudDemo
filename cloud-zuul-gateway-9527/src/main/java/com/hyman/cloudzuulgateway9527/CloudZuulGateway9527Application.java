package com.hyman.cloudzuulgateway9527;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

/**
 * Zuul 包含了对请求的路由和过滤两个最主要的功能：
 * 其中路由功能负责将外部请求转发到具体的微服务实例上，是实现外部访问统一入口的基础而过滤器功能则负责对请求的处理过程进行干预，
 * 是实现请求校验，服务聚合等功能的基础。
 * 即提供：代理，路由，过滤，三大功能。
 *
 * Zuul 与 Eureka 整合：
 * 将 Zuul 自身注册为 Eureka 服务治理下的应用，同时从 Eureka 中获得其他微服务的消息，也即以后的访问微服务都是通过 Zuul 跳转后
 * 获得。
 */
@SpringBootApplication
@EnableZuulProxy
public class CloudZuulGateway9527Application {

	public static void main(String[] args) {
		SpringApplication.run(CloudZuulGateway9527Application.class, args);
	}

}
