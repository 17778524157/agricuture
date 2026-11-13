package com.agriculture.user.consumer;

import com.agriculture.user.config.RabbitMQRetryConfig;
import com.agriculture.user.dto.EsSyncMessage;
import com.agriculture.user.service.ElasticsearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * ES同步重试消费者
 * 处理同步失败的消息，进行重试
 */
@Slf4j
@Component
public class EsSyncRetryConsumer {
    
    @Autowired
    private ElasticsearchService elasticsearchService;
    
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    /**
     * 监听重试队列
     */
    @RabbitListener(queues = RabbitMQRetryConfig.ES_SYNC_RETRY_QUEUE)
    public void handleRetry(EsSyncMessage syncMessage) {
        log.info("收到ES同步重试消息: userId={}, operation={}, retryCount={}", 
            syncMessage.getId(), syncMessage.getOperationType(), syncMessage.getRetryCount());
        
        try {
            // 尝试同步到ES
            syncToEs(syncMessage);
            log.info("ES同步重试成功: userId={}, operation={}", 
                syncMessage.getId(), syncMessage.getOperationType());
            
        } catch (Exception e) {
            log.error("ES同步重试失败: userId={}, operation={}, retryCount={}", 
                syncMessage.getId(), syncMessage.getOperationType(), syncMessage.getRetryCount(), e);
            
            // 检查是否超过最大重试次数
            if (syncMessage.isMaxRetryReached()) {
                log.error("ES同步失败，已达到最大重试次数，放弃同步: userId={}, operation={}", 
                    syncMessage.getId(), syncMessage.getOperationType());
                // 可以在这里记录到数据库或发送告警
                return;
            }
            
            // 增加重试次数并重新发送到死信队列（延迟重试）
            syncMessage.incrementRetry();
            try {
                rabbitTemplate.convertAndSend(
                    RabbitMQRetryConfig.ES_SYNC_DLX_QUEUE,
                    syncMessage
                );
                log.info("ES同步消息重新发送到延迟队列，等待下次重试: userId={}, retryCount={}", 
                    syncMessage.getId(), syncMessage.getRetryCount());
            } catch (Exception ex) {
                log.error("重新发送ES同步消息到延迟队列失败", ex);
            }
        }
    }
    
    /**
     * 同步数据到ES
     */
    private void syncToEs(EsSyncMessage syncMessage) {
        String operationType = syncMessage.getOperationType();
        Map<String, Object> data = syncMessage.getData();
        String userId = syncMessage.getId();
        
        switch (operationType) {
            case "INSERT":
            case "UPDATE":
                elasticsearchService.syncUserToEs(data);
                break;
            case "DELETE":
                elasticsearchService.deleteUserFromEs(userId);
                break;
            default:
                log.warn("未知的操作类型: {}", operationType);
                throw new IllegalArgumentException("未知的操作类型: " + operationType);
        }
    }
}













