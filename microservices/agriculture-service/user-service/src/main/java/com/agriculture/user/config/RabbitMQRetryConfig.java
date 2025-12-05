package com.agriculture.user.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ重试队列配置
 * 用于处理ES同步失败的重试
 */
@Slf4j
@Configuration
public class RabbitMQRetryConfig {
    
    // 正常队列
    public static final String ES_SYNC_QUEUE = "es.sync.queue";
    // 死信交换机
    public static final String ES_SYNC_DLX_EXCHANGE = "es.sync.dlx.exchange";
    // 死信队列
    public static final String ES_SYNC_DLX_QUEUE = "es.sync.dlx.queue";
    // 重试队列
    public static final String ES_SYNC_RETRY_QUEUE = "es.sync.retry.queue";
    // 重试交换机
    public static final String ES_SYNC_RETRY_EXCHANGE = "es.sync.retry.exchange";
    
    /**
     * 正常队列（带TTL和死信配置）
 * 这个方法用于创建一个名为ES_SYNC_QUEUE的持久化队列，并配置了死信交换机和死信路由键
 * 当消息在这个队列中过期或被拒绝时，会被路由到死信交换机，并根据指定的路由键重新路由
     */
    @Bean
    public Queue esSyncQueue() {
    // 使用QueueBuilder构建一个持久化的队列
    // 设置队列名称为ES_SYNC_QUEUE
    // 配置死信交换机为ES_SYNC_DLX_EXCHANGE
    // 配置死信路由键为"es.sync.retry"
        return QueueBuilder.durable(ES_SYNC_QUEUE)
                .withArgument("x-dead-letter-exchange", ES_SYNC_DLX_EXCHANGE)  // 设置死信交换机参数
                .withArgument("x-dead-letter-routing-key", "es.sync.retry")    // 设置死信路由键参数
                .build();    // 构建并返回队列
    }
    
    /**
     * 死信交换机
     */
    @Bean
    public DirectExchange esSyncDlxExchange() {
        return new DirectExchange(ES_SYNC_DLX_EXCHANGE);
    }
    
    /**
     * 死信队列（用于重试）
     */
    @Bean
    public Queue esSyncDlxQueue() {
        return QueueBuilder.durable(ES_SYNC_DLX_QUEUE)
                .withArgument("x-dead-letter-exchange", ES_SYNC_RETRY_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", "es.sync.retry")
                .withArgument("x-message-ttl", 60000) // 1分钟后重试
                .build();
    }
    
    /**
     * 死信队列绑定
     */
    @Bean
    public Binding esSyncDlxBinding() {
        return BindingBuilder
                .bind(esSyncDlxQueue())
                .to(esSyncDlxExchange())
                .with("es.sync.retry");
    }
    
    /**
     * 重试交换机
     */
    @Bean
    public DirectExchange esSyncRetryExchange() {
        return new DirectExchange(ES_SYNC_RETRY_EXCHANGE);
    }
    
    /**
     * 重试队列
     */
    @Bean
    public Queue esSyncRetryQueue() {
        return QueueBuilder.durable(ES_SYNC_RETRY_QUEUE).build();
    }
    
    /**
     * 重试队列绑定
     */
    @Bean
    public Binding esSyncRetryBinding() {
        return BindingBuilder
                .bind(esSyncRetryQueue())
                .to(esSyncRetryExchange())
                .with("es.sync.retry");
    }
    
    /**
     * JSON消息转换器
     */
    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    
    /**
     * RabbitTemplate配置
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
    
    /**
     * 监听器容器工厂
     */
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter());
        factory.setConcurrentConsumers(3);
        factory.setMaxConcurrentConsumers(10);
        return factory;
    }
}












