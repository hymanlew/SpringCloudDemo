package com.hyman.cloudconfigclient3355.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConfigClientRest {

    // 这些配置数据都是从 bootsrtap.yml 指定的 github 上读取下来的 yml 文件中的配置
    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${eureka.client.service-url.defaultZone}")
    private String[] eurekaServers;

    @Value("${server.port}")
    private String port;

    @RequestMapping("/config")
    public String getConfig() {
        return applicationName + "---" + String.join(",", eurekaServers) + "---" + port + "---";
    }
}
