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
 * 其中心概念就是命名客户端，每个 feignclient 都是整体的一部分，它们一起工作以按需联系远程服务器，并且该整体具有一个名称（即
 * feignclient 指定的名称）。SpringCloud 会按 name 及 Configuration 属性创建不同的 ApplicationContext，通过不同的 Context
 * 来隔离 FeignClient 的配置信息，在使用配置类时，不能把配置类放到 ComponentScan 的路径下，否则配置类会对所有FeignClient生效。
 *
 * configuration: Feign配置类，也包含自定义的 feign 的 Encoder，Decoder，LogLevel 和 Contract。
 * fallback: 定义容错的处理类，当调用远程接口失败或超时时，会调用对应接口的容错逻辑。并且指定的类必须实现 @FeignClient 标记的接口。
 * fallbackFactory: 工厂类，用于生成fallback类示例，通过这个属性可以实现每个接口通用的容错逻辑，减少重复的代码。
 * path: 定义当前FeignClient的统一前缀。
 *
 * 另外要注意，在 Feign中实现回退以及 Hystrix回退的工作方式是有限制的。返回 com.netflix.hystrix.HystrixCommand 和 rx.observate
 * 的方法目前不支持回退。
 */
@FeignClient(value = "microservice-provider-dept", configuration = FeignConfig.class, fallbackFactory = HystrixFallback.class)
@Component
public interface DeptClientService {

    //@GetMapping("/getById/{id}")
    //public Department findById(@PathVariable("id") Integer id);
    //
    //@PostMapping("/findAll")
    //public List<Department> findall();

    /**
     * Feign 暂不支持复杂对象作为参数？（待确认）
     */
    @GetMapping("/save")
    public boolean save(@RequestBody Department department);

    /**
     * 消费者调用服务发现
     */
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
