package com.hyman.consumerdeptfeign.controller;

import com.hyman.cloudapi.entity.Department;
import com.hyman.cloudapi.service.DeptClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping("/deptConsumer")
public class DeptController_consumer {

    @Autowired
    private DeptClientService deptClientService;

    @GetMapping("/getById/{id}")
    public Department findById(@PathVariable("id") Integer id){
        return deptClientService.findById(id);
    }

    @PostMapping("/findAll")
    public List<Department> findall(){
        return deptClientService.findall();
    }

    @GetMapping("/save")
    public boolean save(@RequestBody Department department){
        return deptClientService.save(department);
    }

    // 消费者调用服务发现
    @RequestMapping("/discovery")
    public Object discovery(){
        return  deptClientService.discovery();
    }
}
