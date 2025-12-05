package com.agriculture.notification.service.impl;

import com.agriculture.notification.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 通知消息消费者
 * 负责从RabbitMQ接收并处理通知消息
 */
@Component
public class NotificationMessageConsumer {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificationMessageConsumer.class); // 创建日志记录器实例，用于记录类中的日志信息
    
    /**
     * 监听通知队列中的消息
     * @param message 接收到的消息
     */
    @RabbitListener(queues = RabbitMQConfig.NOTICE_QUEUE) // 声明该方法是一个消息监听器，监听指定的队列
    public void receiveNotification(String message) { // 接收通知消息的方法
        try {
            logger.info("收到通知消息: {}", message); // 记录接收到的消息日志
            
            // 在这里处理接收到的通知消息
            // 例如：保存到数据库、发送推送通知等
            processNotification(message); // 调用方法处理通知消息
            
        } catch (Exception e) { // 捕获处理过程中可能发生的异常
            logger.error("处理通知消息时发生错误: {}", e.getMessage()); // 记录错误日志
            // 可以根据需要实现错误处理逻辑
        }
    }
    
    /**
     * 处理通知消息的业务逻辑
     * @param message 通知消息内容
     */
    private void processNotification(String message) { // 处理通知消息的私有方法
        // 实现具体的通知处理逻辑
        // 例如：解析消息内容、根据消息类型进行不同处理等
        logger.info("正在处理通知消息..."); // 记录处理消息的日志
        // 这里可以添加实际的业务逻辑代码
    }
}