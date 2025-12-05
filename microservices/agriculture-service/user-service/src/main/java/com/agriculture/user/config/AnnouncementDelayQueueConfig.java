package com.agriculture.user.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 公告延迟发布队列配置
 * 使用RabbitMQ死信交换机实现延迟队列
 * 
 * 核心原理：
 * 1. 消息先发送到延迟队列，设置TTL（过期时间）
 * 2. 延迟队列不设置消费者，消息过期后成为死信
 * 3. 死信消息路由到死信交换机
 * 4. 死信交换机将消息转发到实际的工作队列
 * 5. 工作队列的消费者处理消息，执行公告发布
 */
@Configuration
public class AnnouncementDelayQueueConfig {
    
    // ==================== 延迟队列相关 ====================
    
    /**
     * 延迟队列交换机（普通交换机）
     */
    public static final String DELAY_EXCHANGE = "announcement.delay.exchange";
    
    /**
     * 延迟队列
     * 特点：
     * 1. 不设置消费者
     * 2. 消息设置TTL
     * 3. 绑定死信交换机
     */
    public static final String DELAY_QUEUE = "announcement.delay.queue";
    
    /**
     * 延迟队列路由键
     */
    public static final String DELAY_ROUTING_KEY = "announcement.delay";
    
    // ==================== 死信队列相关 ====================
    
    /**
     * 死信交换机（Dead Letter Exchange）
     */
    public static final String DLX_EXCHANGE = "announcement.dlx.exchange";
    
    /**
     * 死信队列（工作队列，实际消费的队列）
     */
    public static final String DLX_QUEUE = "announcement.publish.queue";
    
    /**
     * 死信路由键
     */
    public static final String DLX_ROUTING_KEY = "announcement.publish";
    
    // ==================== Bean定义 ====================
    
    /**
     * 创建延迟队列交换机
     */
    @Bean
    public DirectExchange delayExchange() {
        return new DirectExchange(DELAY_EXCHANGE, true, false);
    }
    
    /**
     * 创建延迟队列
     * 重点配置：
     * 1. x-dead-letter-exchange: 绑定死信交换机
     * 2. x-dead-letter-routing-key: 死信消息的路由键
     * 3. x-message-ttl: 可选，队列级别的默认TTL
     */
    @Bean
    public Queue delayQueue() {
        return QueueBuilder.durable(DELAY_QUEUE)
                // 配置死信交换机
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
                // 配置死信路由键
                .withArgument("x-dead-letter-routing-key", DLX_ROUTING_KEY)
                // 注意：不设置x-message-ttl，由消息自己携带TTL更灵活
                .build();
    }
    
    /**
     * 绑定延迟队列到延迟交换机
     */
    @Bean
    public Binding delayQueueBinding() {
        return BindingBuilder
                .bind(delayQueue())
                .to(delayExchange())
                .with(DELAY_ROUTING_KEY);
    }
    
    /**
     * 创建死信交换机
     */
    @Bean
    public DirectExchange dlxExchange() {
        return new DirectExchange(DLX_EXCHANGE, true, false);
    }
    
    /**
     * 创建死信队列（工作队列）
     * 这个队列会被消费者监听
     */
    @Bean
    public Queue dlxQueue() {
        return QueueBuilder.durable(DLX_QUEUE).build();
    }
    
    /**
     * 绑定死信队列到死信交换机
     */
    @Bean
    public Binding dlxQueueBinding() {
        return BindingBuilder
                .bind(dlxQueue())
                .to(dlxExchange())
                .with(DLX_ROUTING_KEY);
    }
}
