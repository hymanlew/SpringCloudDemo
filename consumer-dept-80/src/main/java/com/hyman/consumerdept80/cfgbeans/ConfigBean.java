package com.hyman.consumerdept80.cfgbeans;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

// 从 spring 到 boot：从 applicationContext.xml 到 configuration 注解配置。
@Configuration
public class ConfigBean {

    @Bean
    public RestTemplate getRestTemplate(){
        // springboot 为 restful 规范的接口提供的模版实现类，从而执行相关的操作（即对 POST,GET,PUT,DELETE 等等请求方式进行了封装）。
        // 它提供了多种便捷访问远程 HTTP 服务的方法。是一种简单便捷的访问 restful 服务模版类，是spring 提供的用于访问 rest 服
        // 务的客户端模版工具集。
        return new RestTemplate();
    }
}
