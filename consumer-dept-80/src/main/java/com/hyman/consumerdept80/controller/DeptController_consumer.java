package com.hyman.consumerdept80.controller;

import com.hyman.cloudapi.entity.Department;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping("/deptConsumer")
public class DeptController_consumer {

    // 该模版包含三个参数：url，requestMap，ResponseBean。分别代表 rest 请求地址，请求参数，http 响应的对象类型
    @Autowired
    private RestTemplate restTemplate;

    private static final String REST_URL_PREFIX = "http://localhost:8001/dept";

    @GetMapping("/getById/{id}")
    public Department findById(@PathVariable("id") Integer id){
        return restTemplate.getForObject(REST_URL_PREFIX + "/getById/"+ id, Department.class);
    }

    @PostMapping("/findAll")
    public List<Department> findall(){
        return restTemplate.getForObject(REST_URL_PREFIX + "/findAll", List.class);
    }

    @GetMapping("/save")
    public boolean save(@RequestBody Department department){
        return restTemplate.postForObject(REST_URL_PREFIX + "/save", department, boolean.class);
    }

    // 消费者调用服务发现
    @RequestMapping("/discovery")
    public Object discovery(){
        return  restTemplate.getForObject(REST_URL_PREFIX + "/discovery", Object.class);
    }
}
