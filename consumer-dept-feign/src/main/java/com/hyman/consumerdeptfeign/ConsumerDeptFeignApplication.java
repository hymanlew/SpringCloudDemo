package com.hyman.consumerdeptfeign;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * Feign：是一个声明式的 web service REST 客户端。使用时只需要创建一个接口再加上一个注解（EnableFeignClients）即可。同时也支
 * 持 JAX-RS 标准的注解和可插拔式的编码器和解码器。
 * springCloud 对 Feign 进行了封装，使其支持了 spring MVC 标准注解和 HttpMessageConverters，可以与 Eureka 和 Ribbon 组合使
 * 用以支持负载均衡。
 *
 * Ribbon 主要是面向微服务的调用，因为在消费端要指定声明一个提供者的微服务名称。而 java 是面向接口（例如 DAO 层的面向对象）的
 * 编程。
 *
 * 所以 springCloud 提供了两种方式来进行微服务的调用：
 * 1，是通过微服务的名称 + RestTemplate，来获得调用地址（Ribbon）。
 * 2，是通过接口 + 注解，获得调用服务并调用服务中的方法（Feign）。
 *
 * Feign 旨在使编写 java http 客户端变得更容易。之前使用 Ribbon + RestTemplate 时，利用 template 对 http 请求的封装处理，形
 * 成了一套模版化的调用方法。但在实际开发中由于对服务依赖的调用可能不止一处，往往一个接口会被多处调用，所以通常都会针对每个微服
 * 务自行封装一些客户端类来包装这些依赖服务的调用。所以 Feign 在此基础上做了进一步封装，由他来帮助我们定义和实现依赖服务接口的
 * 定义。在 Feign 实现下，我们只需创建一个接口并使用注解的方式来配置它即可完成对服务提供方的接口绑定，简化了使用 SpringCloud
 * Ribbon 时，自动封装服务调用客户端的开发量。
 *
 * Feign 集成了 Ribbon，前者利用后者维护了提供者微服务的服务列表信息，并且通过轮询实现了客户端的负载均衡。而两者不同的是，通过
 * Feign 只需要定义服务绑定接口且以声明式的方法，优雅而简单的实现了服务调用。
 */

@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients(basePackages = {"com.hyman.consumerdeptfeign"})
@ComponentScan("com.hyman.consumerdeptfeign")
public class ConsumerDeptFeignApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConsumerDeptFeignApplication.class, args);
	}

}
