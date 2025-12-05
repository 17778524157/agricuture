package com.agriculture.notification.service.impl;

import com.agriculture.notification.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 通知消息生产者
 * 负责向RabbitMQ发送通知消息
 */
@Component
public class NotificationMessageProducer {
    
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    /**
     * 发送通知消息到队列
     * @param noticeMessage 通知消息内容
     */
    public void sendNotification(String noticeMessage) {
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.NOTICE_EXCHANGE,
            RabbitMQConfig.NOTICE_ROUTING_KEY,
            noticeMessage
        );
    }
    
    /**
     * 发送通知对象到队列
     * @param notification 通知对象
     */
    public void sendNotificationObject(Object notification) {
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.NOTICE_EXCHANGE,
            RabbitMQConfig.NOTICE_ROUTING_KEY,
            notification
        );
    }
}