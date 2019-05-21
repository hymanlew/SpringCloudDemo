package com.hyman.providerdepthystrix8001.controller;

import com.hyman.cloudapi.entity.Department;
import com.hyman.providerdepthystrix8001.service.DeptService;
//import com.netflix.ribbon.proxy.annotation.Hystrix;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.openfeign.support.FallbackCommand;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dept")
public class DeptController {

    @Autowired
    private DeptService deptService;
    // 对于注册进 eureka 里面的微服务，可以通过服务发现来获得该服务的信息。
    @Autowired
    private DiscoveryClient client;

    // 一旦调用服务方法失败并抛出异常后，会自动调用 fallback 指定的方法。
    @GetMapping("/getById/{id}")
    @HystrixCommand(fallbackMethod = "hystrixMethod")
    public Department findById(@PathVariable("id") Integer id){

        Department department = deptService.findById(id);
        if(null == department){
            throw new RuntimeException("depart is null");
        }
        return department;
    }

    @PostMapping("/findAll")
    public List<Department> findall(){
        return deptService.findAll();
    }

    @GetMapping("/save")
    public boolean save(@RequestBody Department department){
        return deptService.addDept(department);
    }

    @RequestMapping("/discovery")
    public Object discovery(){
        List<String> list = client.getServices();
        System.out.println("eureka 中所有已经注册的服务列表："+list);

        // 按照服务名称来得到某个服务，注意该名称是在 spring 中配置的名字，而不是在 eureka 中自定义的名字，且eureka 会自动转换为大写。
        List<ServiceInstance> serviceInstances = client.getInstances("microservice-provider-dept");
        for(ServiceInstance instance : serviceInstances){
            System.out.println(instance.getServiceId() + "\t" + instance.getHost() + "\t" + instance.getUri());
        }
        return this.client;
    }

    public Object hystrixMethod(@PathVariable("id") Integer id){
        return "熔断异常！没有指定 ID 的数据："+id;
    }
}
