package com.hyman.providerdept8003.service.impl;

import com.hyman.providerdept8003.service.DeptMsgService;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;

import javax.annotation.Resource;

/**
 * 定义消息的推送管道，生产者
 */
@EnableBinding(Source.class)
public class DeptMsgServiceImp implements DeptMsgService {

    @Resource
    private MessageChannel output;

    @Override
    public boolean send() {
        String msg = "测试发送信息";
        return output.send(MessageBuilder.withPayload(msg).build());
    }
}
