package com.hyman.consumer.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.messaging.Message;

/**
 * Stream 中的消息通信方式遵循了发布-订阅的模式，在 binder 中：
 * - input 对应于消费者。
 * - output 对应于生产者。
 * - topic 对应于 rabbitmq 中的 exchange，kafka 中的 topic。
 *
 * Stream 工作流程：消息生产者 -> source -> channel -> binder -> MQ 中间件 -> binder -> channel -> sink -> 消息消费者。
 * - Binder，output --> topic --> input。
 * - channel，通道是队列 queue 的抽象，是实现存储和转发的媒介，通过 channel 对队列进行配置。
 * - source，sink，分别对应于生产者，消费者。
 *
 * - @input，@output，输入，输出通道。
 * - @streamListener，监听队列，用于消费者的队列的消息接收。
 * - @EnableBinding，绑定 channel 和 exchange。
 *
 * 在 stream 中导致重复消费的原因是，默认每个消费者的分组 group 是不同的。可使用分组和持久化属性 group（消息分组）来解决，因
 * 为在 stream 中处于同一 group 的多个消费者是竞争关系，这就能保证消息只被一个消费者消费。但是不同组之间还是可以全面消费（重
 * 复消费）的。并且 group 属性也具有消息持久化的功能。
 */
@Slf4j
@EnableBinding(Sink.class)
public class DeptController {

    @StreamListener(Sink.INPUT)
    public boolean receive(Message<String> msg){

        log.info("接收到消息 : " + msg.getPayload());
        return true;
    }
}
