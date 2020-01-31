package com.hyman.cloudapi.service;

import com.hyman.cloudapi.config.FeignConfig;
import com.hyman.cloudapi.entity.Department;
import feign.Param;
import feign.RequestLine;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 增加 Feign 面向接口的负载均衡配置.
 * 并且注解指定的名称是在 spring 中配置的提供者微服务的名字，而不是在 eureka 中自定义的名字。
 *
 * Feign 已经集成了hystrix服务，所以直接声明 fallback 返回操作即可。
 *
 * 其中心概念就是命名客户端，每个 feign client 都是整体的一部分，它们一起工作以按需联系远程服务器，并且该整体具有一个名称，即使
 * 用 @feignclient 命名。spring cloud 根据需要使用 FeignClient 的 Configuration 属性为每个命名的客户端创建一个新的整体作为
 * ApplicationContext。也包含其他 feign Encoder，feign Decoder 和 feign Contract。
 */
@FeignClient(value = "microservice-provider-dept", configuration = FeignConfig.class, fallbackFactory = HystrixFallback.class)
@Component
public interface DeptClientService {

    //@GetMapping("/getById/{id}")
    //public Department findById(@PathVariable("id") Integer id);
    //
    //@PostMapping("/findAll")
    //public List<Department> findall();

    @GetMapping("/save")
    public boolean save(@RequestBody Department department);

    // 消费者调用服务发现
    @RequestMapping("/discovery")
    public Object discovery();

    /**
     * feign 默认采用的是 springMVC 的契约（注解），如 GetMapping，PostMapping。
     * 也可以采用 feign 自身的合同，契约（即注解），使用 configuration 属性指定自定义的配置类即可。
     */
    @RequestLine("GET /getById/{id}")
    public Department findById(@Param("id") Integer id);

    @RequestLine("POST /findAll")
    public List<Department> findall();
}
