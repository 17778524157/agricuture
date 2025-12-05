package com.agriculture.notification.service.impl;

import com.agriculture.notification.dto.DelayedNoticeMessage;
import com.agriculture.notification.entity.Notice;
import com.agriculture.notification.service.DelayedNoticeService;
import com.agriculture.notification.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 延迟发布公告消息消费者
 * 消费从死信队列转发过来的消息，实现定时发布功能
 */
@Component
public class DelayedNoticeConsumer {

    private static final Logger logger = LoggerFactory.getLogger(DelayedNoticeConsumer.class);

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private DelayedNoticeService delayedNoticeService;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 监听死信队列，处理延迟发布的消息
     */
    @RabbitListener(queues = "delayed.notice.dlx.queue")
    public void handleDelayedNotice(String messageJson) {
        try {
            logger.info("接收到延迟发布公告消息: {}", messageJson);
            
            // 反序列化消息
            DelayedNoticeMessage message = objectMapper.readValue(messageJson, DelayedNoticeMessage.class);
            
            // 检查计划发布时间是否到达
            Long currentTime = System.currentTimeMillis();
            if (message.getScheduledPublishTime() != null && 
                message.getScheduledPublishTime() > currentTime) {
                logger.warn("消息计划发布时间未到，延迟处理 - NoticeId: {}, 计划时间: {}, 当前时间: {}", 
                           message.getNoticeId(), message.getScheduledPublishTime(), currentTime);
                // 可以选择重新发送到延迟队列，或者直接忽略（因为TTL已经过期）
                return;
            }
            
            // 发布通知
            boolean publishSuccess = publishNotice(message);
            
            // 更新任务状态
            if (message.getTaskId() != null) {
                if (publishSuccess) {
                    // 发布成功，更新状态为已发布
                    delayedNoticeService.updateTaskStatus(message.getTaskId(), 1, System.currentTimeMillis());
                    logger.info("延迟发布公告成功，任务状态已更新 - TaskId: {}, NoticeId: {}, Title: {}", 
                               message.getTaskId(), message.getNoticeId(), message.getTitle());
                } else {
                    // 发布失败，更新状态为发布失败
                    delayedNoticeService.updateTaskStatus(message.getTaskId(), 3, null);
                    logger.error("延迟发布公告失败，任务状态已更新 - TaskId: {}, NoticeId: {}, Title: {}", 
                                message.getTaskId(), message.getNoticeId(), message.getTitle());
                }
            } else {
                logger.info("延迟发布公告成功 - NoticeId: {}, Title: {}", 
                           message.getNoticeId(), message.getTitle());
            }
            
        } catch (Exception e) {
            logger.error("处理延迟发布公告消息失败: {}", messageJson, e);
        }
    }
    
    /**
     * 发布通知到数据库
     * @return 是否发布成功
     */
    private boolean publishNotice(DelayedNoticeMessage message) {
        try {
            Notice notice = new Notice();
            
            // 如果noticeId存在，更新通知；否则创建新通知
            if (message.getNoticeId() != null && message.getNoticeId() > 0) {
                notice.setId(message.getNoticeId());
                notice.setName(message.getTitle());
                notice.setContent(message.getContent());
                notice.setUser(message.getUser());
                notice.setImg(message.getImg());
                notice.setTime(String.valueOf(System.currentTimeMillis()));
                
                boolean updated = notificationService.updateNotice(notice);
                if (!updated) {
                    // 如果更新失败，尝试创建新通知
                    notice.setId(null);
                    notificationService.createNotice(notice);
                }
            } else {
                notice.setName(message.getTitle());
                notice.setContent(message.getContent());
                notice.setUser(message.getUser());
                notice.setImg(message.getImg());
                notice.setTime(String.valueOf(System.currentTimeMillis()));
                
                notificationService.createNotice(notice);
            }
            
            logger.info("通知已发布 - NoticeId: {}, Title: {}", 
                       message.getNoticeId(), message.getTitle());
            return true;
            
        } catch (Exception e) {
            logger.error("发布通知失败 - NoticeId: {}, Title: {}", 
                        message.getNoticeId(), message.getTitle(), e);
            return false;
        }
    }
}
