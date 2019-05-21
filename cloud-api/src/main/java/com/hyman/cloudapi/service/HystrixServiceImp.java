package com.hyman.cloudapi.service;

import org.springframework.stereotype.Component;

@Component
public class HystrixServiceImp implements HystrixService {

    @Override
    public Object fallbackMethod() {
        return "熔断，异常！";
    }
}
