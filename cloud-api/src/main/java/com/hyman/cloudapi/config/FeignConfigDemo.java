package com.hyman.cloudapi.config;

import feign.Feign;
import feign.Logger;
import feign.auth.BasicAuthRequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class FeignConfigDemo {

    /**
     * 使用 feign 负载请求时，声明权限的用户名密码，以使得请求验证通过。（此处用于请求 eureka7001 时的认证）。
     * @return
     */
    @Bean
    public BasicAuthRequestInterceptor basicAuthRequestInterceptor(){
        return new BasicAuthRequestInterceptor("admin", "admin");
    }

    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    /**
     * 当配置 feign.Hystrix.enabled=true 时，则 feign 将用断路器包装所有方法。并且返回 com.netflix.hystrix.HystrixCommand。
     * 它允许您使用响应模式的编程（通过调用.toObservable（）或.observe（）或异步使用（通过调用.queue（））。
     *
     * 如果要为某个客户端上禁用 Hystrix 支持，则需要创建一个具有 “prototype” 作用域的 vanilla Feign.Builder：
     */
    @Bean
    @Scope("prototype")
    public Feign.Builder feignBuilder() {
        return Feign.builder();
    }
}
