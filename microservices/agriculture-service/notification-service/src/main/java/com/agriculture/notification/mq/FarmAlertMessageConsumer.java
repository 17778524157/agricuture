package com.agriculture.notification.mq;

import com.agriculture.notification.config.FarmAlertRabbitMQConfig;
import com.agriculture.notification.entity.FarmAlertMessage;
import com.agriculture.notification.service.FarmAlertEmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;

/**
 * 农田异常预警消息消费者
 * 用于监听RabbitMQ队列中的预警消息，并调用邮件服务发送通知
 */
@Component
public class FarmAlertMessageConsumer {
    
    private static final Logger logger = LoggerFactory.getLogger(FarmAlertMessageConsumer.class);
    
    @Autowired
    private FarmAlertEmailService farmAlertEmailService;
    
    /**
     * 监听农田异常预警消息队列
     * 当有新的预警消息到达时，自动调用此方法处理
     * @param alertMessage 预警消息对象
     */
    @RabbitListener(queues = FarmAlertRabbitMQConfig.FARM_ALERT_QUEUE)
    public void processFarmAlertMessage(FarmAlertMessage alertMessage) {
        logger.info("接收到农田异常预警消息: {}", alertMessage);
        
        try {
            // 检查是否有用户邮箱
            if (alertMessage.getUserEmail() != null && !alertMessage.getUserEmail().isEmpty()) {
                // 发送农田异常预警邮件
                farmAlertEmailService.sendFarmAlertEmail(
                        alertMessage.getUserEmail(),
                        alertMessage.getAlertType(),
                        alertMessage.getFarmName(),
                        alertMessage.getAlertContent(),
                        alertMessage.getAlertTime()
                );
                logger.info("农田异常预警邮件发送成功，收件人: {}", alertMessage.getUserEmail());
            } else {
                logger.warn("无法发送预警邮件：用户邮箱为空");
            }
        } catch (MessagingException e) {
            logger.error("发送农田异常预警邮件时发生异常: {}", e.getMessage(), e);
        } catch (Exception e) {
            logger.error("处理农田异常预警消息时发生异常: {}", e.getMessage(), e);
        }
    }
}