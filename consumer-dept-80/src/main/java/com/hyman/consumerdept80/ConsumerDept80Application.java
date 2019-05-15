package com.hyman.consumerdept80;

import com.hyman.myrule.MyRule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.ribbon.RibbonClient;

/**
 * @RibbonClient, 它可以在启动该微服务的时候去加载自定义的 Ribbon 配置类（即自定义的负载算法），从而使其生效。
 * 		name，是指定自定义的负载算法作用到的哪个微服务。
 * 		config，是指定自定义的配置算法类。
 */
@SpringBootApplication
@EnableEurekaClient
@RibbonClient(name = "microservice-consumer-dept", configuration = MyRule.class)
public class ConsumerDept80Application {

	public static void main(String[] args) {
		SpringApplication.run(ConsumerDept80Application.class, args);
	}

}
