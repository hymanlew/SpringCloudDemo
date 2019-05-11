package com.hyman.consumerdept80.cfgbeans;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * SpringCloud Ribbon 是一套客户端的负载均衡的工具，其主要功能是提供客户端的软负载均衡算法，将 netfix 的中间层服务连接起来。
 * ribbon 客户端组件提供一系列完善的配置项如连接超时，重试等等。简单来说就是在配置文件中列出 load balancer（LB 负载均衡）后面
 * 所有的机器，Ribbon 会自动的帮助你基于（遵循）某种规则（如简单轮询，随机连接等）去连接这些机器。我们也很容易使用 Ribbon 实现
 * 自定义的负载均衡算法。
 *
 * LB（load balancer）负载均衡，在微服务或分布式集群中经常用的一种应用。简单来说就是将用户的请求平摊的分配到多个服务上，从而达
 * 到系统的 HA。
 * 常见的负载均衡又分为：
 *
 * 集中式 LB（偏硬件的），它在服务的消费方和提供方之间使用独立的 LB 设施（可以是硬件，如F5，也可以是软件，如Nginx），由该设施负
 * 责把访问请求通过某种策略转发至服务的提供方。
 * 进程式 LB（偏软件的），它是将 LB 逻辑集成到消费方，消费方从服务注册中心获知有哪些地址可用，然后自己再从这些地址中选出一个合适
 * 的服务器。
 *
 * Ribbon 就属于进程式 LB，它只是一个类库，集成于消费方进程，消费方通过它来获取到服务提供方的地址。它工作时是分成两步：
 * 第一步先选择 eurekaserver，它优先选择在同一个区域内负载较少的 server。
 * 第二步再根据用户指定的策略，从 server 中取到服务注册列表信息，并从中选择出一个地址访问。
 *
 * 从 Ribbon 默认的轮询方法可以看出，它是一个软负载均衡的客户端组件，它可以和其他所需请求的客户端结合使用，和 eureka 结合只是其
 * 中的一个实例。
 *
 *
 * 相应的在中间件如 dubbo，cloud中均提供了负载均衡，其中 cloud 的负载均衡算法可以自定义。
 */
// 从 spring 到 boot：从 applicationContext.xml 到 configuration 注解配置。
@Configuration
public class ConfigBean {

    @Bean
    // 客户端负载均衡工具注解，其默认是轮询算法，即每个 server 都访问一次（一人一次）实现轮询。
    @LoadBalanced
    public RestTemplate getRestTemplate(){
        // springboot 为 restful 规范的接口提供的模版实现类，从而执行相关的操作（即对 POST,GET,PUT,DELETE 等等请求方式进行了封装）。
        // 它提供了多种便捷访问远程 HTTP 服务的方法。是一种简单便捷的访问 restful 服务模版类，是spring 提供的用于访问 rest 服
        // 务的客户端模版工具集。
        return new RestTemplate();
    }
}
