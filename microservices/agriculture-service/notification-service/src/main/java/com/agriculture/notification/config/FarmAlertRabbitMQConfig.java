package com.agriculture.notification.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 农田异常预警RabbitMQ配置类
 * 用于定义农田异常预警相关的消息队列、交换器和路由键配置
 */
@Configuration
public class FarmAlertRabbitMQConfig {
    // 定义队列名称常量
    public static final String FARM_ALERT_QUEUE = "farm.alert.queue";
    // 定义交换器名称常量
    public static final String FARM_ALERT_EXCHANGE = "farm.alert.exchange";
    // 定义路由键常量
    public static final String FARM_ALERT_ROUTING_KEY = "farm.alert.routing.key";
    
    // 创建农田异常预警队列
    @Bean
    public Queue farmAlertQueue() {
        // 队列持久化，避免服务器重启后消息丢失
        return new Queue(FARM_ALERT_QUEUE, true);
    }
    
    // 创建农田异常预警交换机
    @Bean
    public DirectExchange farmAlertExchange() {
        // 交换机持久化
        return new DirectExchange(FARM_ALERT_EXCHANGE, true, false);
    }
    
    // 绑定队列和交换机
    @Bean
    public Binding farmAlertBinding(Queue farmAlertQueue, DirectExchange farmAlertExchange) {
        return BindingBuilder.bind(farmAlertQueue).to(farmAlertExchange).with(FARM_ALERT_ROUTING_KEY);
    }
}