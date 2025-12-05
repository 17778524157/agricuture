package com.agriculture.notification.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 定时发布公告的延迟队列配置
 * 使用死信交换机实现延迟队列功能
 */
@Configuration
public class DelayedNoticeQueueConfig {

    // 延迟队列（死信队列）- 消息先进入这个队列，过期后转发到死信交换机
    public static final String DELAYED_NOTICE_QUEUE = "delayed.notice.queue";
    
    // 死信交换机 - 接收过期消息
    public static final String DELAYED_NOTICE_DLX_EXCHANGE = "delayed.notice.dlx.exchange";
    
    // 死信队列 - 实际处理消息的队列
    public static final String DELAYED_NOTICE_DLX_QUEUE = "delayed.notice.dlx.queue";
    
    // 路由键
    public static final String DELAYED_NOTICE_ROUTING_KEY = "delayed.notice.routing.key";

    /**
     * 创建延迟队列（死信队列）
     * 消息不会直接消费，而是等待TTL过期后转发到死信交换机
     */
    @Bean
    public Queue delayedNoticeQueue() {
        return QueueBuilder.durable(DELAYED_NOTICE_QUEUE)
                .withArgument("x-dead-letter-exchange", DELAYED_NOTICE_DLX_EXCHANGE) // 死信交换机
                .withArgument("x-dead-letter-routing-key", DELAYED_NOTICE_ROUTING_KEY) // 死信路由键
                .build();
    }

    /**
     * 创建死信交换机
     * 接收从延迟队列过期转发的消息
     */
    @Bean
    public DirectExchange delayedNoticeDlxExchange() {
        return new DirectExchange(DELAYED_NOTICE_DLX_EXCHANGE, true, false);
    }

    /**
     * 创建死信队列（实际处理队列）
     * 消费这个队列的消息，实现定时发布功能
     */
    @Bean
    public Queue delayedNoticeDlxQueue() {
        return QueueBuilder.durable(DELAYED_NOTICE_DLX_QUEUE).build();
    }

    /**
     * 绑定死信队列和死信交换机
     */
    @Bean
    public Binding delayedNoticeDlxBinding() {
        return BindingBuilder
                .bind(delayedNoticeDlxQueue())
                .to(delayedNoticeDlxExchange())
                .with(DELAYED_NOTICE_ROUTING_KEY);
    }
}
