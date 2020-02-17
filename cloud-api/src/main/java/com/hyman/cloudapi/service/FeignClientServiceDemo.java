package com.hyman.cloudapi.service;

import com.hyman.cloudapi.config.FeignConfigDemo;
import feign.RequestLine;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 增加 Feign 面向接口的负载均衡配置.
 * 如果注解指定了 url 地址（一般用于调试，可以手动指定 @FeignClient 调用的地址），则名称（value = name）就会被忽略掉。
 */
@FeignClient(value = "xxx", url = "http://admin:admin@eureka7001:7001/eureka/", configuration = FeignConfigDemo.class)
@Component
public interface FeignClientServiceDemo {

    @RequestLine("POST /eureka/discovery/{id}")
    public Object getService(@PathVariable("id") Integer id);

}
