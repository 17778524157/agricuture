package com.agriculture.notification.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.*;

/**
 * RabbitMQ配置类
 * 用于定义RabbitMQ相关的常量配置，包括队列名称、交换器名称和路由键
 */
@Configuration // 将此类标记为Spring组件，使其成为Spring容器中的一个Bean
public class RabbitMQConfig {
    // 定义队列名称常量
    public static final String NOTICE_QUEUE = "notice.queue";  // 消息队列的名称，用于标识特定的消息队列
    // 定义交换器名称常量
    public static final String NOTICE_EXCHANGE = "notice.exchange";  // 交换器的名称，用于消息的路由和分发
    // 定义路由键常量
    public static final String NOTICE_ROUTING_KEY = "notice.routing.key";  // 路由键，用于将消息路由到特定的队列
    // 创建队列
    @Bean
    public Queue noticeQueue() {
        return new Queue(NOTICE_QUEUE, true); // 持久化队列
    }
    // 创建交换机
    @Bean
    public DirectExchange noticeExchange() {
        return new DirectExchange(NOTICE_EXCHANGE, true, false);
    }
    // 绑定队列和交换机
    @Bean
    public Binding noticeBinding(Queue noticeQueue, DirectExchange noticeExchange) {
        return BindingBuilder.bind(noticeQueue).to(noticeExchange).with(NOTICE_ROUTING_KEY);
    }
}
