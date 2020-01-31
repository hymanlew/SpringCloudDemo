package com.hyman.cloudapi.config;

import feign.Contract;
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
}
