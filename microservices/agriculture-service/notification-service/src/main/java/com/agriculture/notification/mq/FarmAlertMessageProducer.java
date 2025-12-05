package com.agriculture.notification.mq;

import com.agriculture.notification.config.FarmAlertRabbitMQConfig;
import com.agriculture.notification.entity.FarmAlertMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 农田异常预警消息生产者
 * 用于向RabbitMQ发送农田异常预警消息
 */
@Component
public class FarmAlertMessageProducer {
    
    private static final Logger logger = LoggerFactory.getLogger(FarmAlertMessageProducer.class);
    
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    /**
     * 发送农田异常预警消息
     * @param alertMessage 预警消息对象
     */
    public void sendFarmAlertMessage(FarmAlertMessage alertMessage) {
        try {
            logger.info("准备发送农田异常预警消息: {}", alertMessage);
            
            // 向指定的交换机发送消息，使用指定的路由键
            rabbitTemplate.convertAndSend(
                    FarmAlertRabbitMQConfig.FARM_ALERT_EXCHANGE,
                    FarmAlertRabbitMQConfig.FARM_ALERT_ROUTING_KEY,
                    alertMessage
            );
            
            logger.info("农田异常预警消息发送成功");
        } catch (Exception e) {
            logger.error("发送农田异常预警消息时发生异常: {}", e.getMessage(), e);
            throw new RuntimeException("发送农田异常预警消息失败", e);
        }
    }
}