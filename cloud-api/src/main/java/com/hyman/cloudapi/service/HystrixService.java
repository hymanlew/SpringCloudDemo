package com.hyman.cloudapi.service;

public interface HystrixService {

    Object fallbackMethod();
}
