package com.hyman.cloudapi.config;

import feign.Contract;
import feign.Logger;
import org.springframework.cloud.netflix.ribbon.ZonePreferenceServerListFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    /**
     * feign 所采用的合同，契约（即注解）。默认是使用 springMVC 的契约（注解），如 GetMapping，PostMapping。
     */
    @Bean
    public Contract feignContract(){
        return new feign.Contract.Default();
    }

    @Bean
    public ZonePreferenceServerListFilter serverListFilter() {
        ZonePreferenceServerListFilter filter = new ZonePreferenceServerListFilter();
        filter.setZone("myTestZone");
        return filter;
    }

    @Bean
    public Logger.Level feignLoggerLevel() {

        // 输出所有级别的日志
        return Logger.Level.FULL;
    }
}
