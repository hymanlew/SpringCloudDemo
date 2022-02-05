package com.hyman.providerdept8003.controller;

import com.hyman.cloudapi.entity.Department;
import com.hyman.providerdept8003.service.DeptMsgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/dept")
public class DeptController {

    @Resource
    private DeptMsgService deptMsgService;

    @GetMapping("/send")
    public boolean save(@RequestBody Department department){
        return deptMsgService.send();
    }
}
