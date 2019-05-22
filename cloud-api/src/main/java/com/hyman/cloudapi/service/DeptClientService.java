package com.hyman.cloudapi.service;

import com.hyman.cloudapi.entity.Department;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 增加 Feign 面向接口的负载均衡配置.
 * 并且注解指定的名称是在 spring 中配置的提供者微服务的名字，而不是在 eureka 中自定义的名字。
 *
 * Feign 已经集成了hystrix服务，所以直接声明 fallback 返回操作即可。
 */
@FeignClient(value = "microservice-provider-dept", fallbackFactory = HystrixFallback.class)
@Component
public interface DeptClientService {

    @GetMapping("/getById/{id}")
    public Department findById(@PathVariable("id") Integer id);

    @PostMapping("/findAll")
    public List<Department> findall();

    @GetMapping("/save")
    public boolean save(@RequestBody Department department);

    // 消费者调用服务发现
    @RequestMapping("/discovery")
    public Object discovery();
}
