package com.hyman.consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * Spring-Cloud-Stream 是一个构建消息驱动微服务的框架，它使我们不再关注具体某个 mq 的细节，只需要用一种适配绑定的方式，自动的
 * 在各种 MQ 中进行切换。是屏蔽了底层消息中间件的差异，降低切换成本，统一消息的编程模型。就例如 jdbc 之对于 mysql，oracle，pg
 * 等数据库的作用。
 *
 * 应用程序通过 inputs 或 outputs 来与 Spring-Cloud-Stream 中的 binder 对象交互，并通过配置来 binding 绑定，而 Spring-Cloud-Stream
 * 中的 binder 对象负责与消息中间件交互。所以只要搞清楚如何与 Spring-Cloud-Stream 交互就可以方便地使用消息驱动的方式。
 * 通过使用 spring integration 来连接消息代理中间件以实现消息事件驱动，Spring-Cloud-Stream 为消息中间件提供了个性化的自动化配
 * 置实现，引用了发布-订阅，消费组，分区三个核心概念。
 *
 * 目前仅支持 rabbitmq、kafka。
 */
@SpringBootApplication
@EnableEurekaClient
public class ConsumerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConsumerApplication.class, args);
	}

}
