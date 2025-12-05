package com.agriculture.notification.service.impl;

import com.agriculture.notification.config.DelayedNoticeQueueConfig;
import com.agriculture.notification.dto.DelayedNoticeMessage;
import com.agriculture.notification.entity.DelayedNoticeTask;
import com.agriculture.notification.mapper.DelayedNoticeTaskMapper;
import com.agriculture.notification.service.DelayedNoticeService;
import com.agriculture.notification.service.NotificationService;
import com.agriculture.notification.entity.Notice;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

/**
 * 定时发布公告服务实现
 * 提供延迟发布、取消、查询等功能
 */
@Service
public class DelayedNoticeServiceImpl implements DelayedNoticeService {

    private static final Logger logger = LoggerFactory.getLogger(DelayedNoticeServiceImpl.class);

    @Autowired
    private RabbitTemplate rabbitTemplate; // RabbitMQ模板，用于发送消息

    @Autowired
    private NotificationService notificationService; // 通知服务，用于实际的通知发布

    @Autowired
    private ObjectMapper objectMapper; // JSON序列化工具

    @Autowired
    private DelayedNoticeTaskMapper delayedNoticeTaskMapper; // 延迟通知任务数据访问层

    /**
     * 发送延迟通知
     * @param message 延迟通知消息
     * @param delayMilliseconds 延迟时间（毫秒）
     * @return 是否发送成功
     */
    @Override
    @Transactional
    public boolean sendDelayedNotice(DelayedNoticeMessage message, Long delayMilliseconds) {
        try {
            // 计算计划发布时间
            Long scheduledPublishTime = System.currentTimeMillis() + delayMilliseconds;
            message.setScheduledPublishTime(scheduledPublishTime);
            
            // 将消息对象序列化为JSON
            String messageJson = objectMapper.writeValueAsString(message);
            
            // 构建消息
            Message rabbitMessage = MessageBuilder
                    .withBody(messageJson.getBytes(StandardCharsets.UTF_8))
                    .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                    // 设置消息过期时间（TTL），单位：毫秒
                    .setExpiration(String.valueOf(delayMilliseconds))
                    .build();
            
            // 发送到延迟队列
            rabbitTemplate.send(
                    DelayedNoticeQueueConfig.DELAYED_NOTICE_QUEUE,
                    rabbitMessage
            );
            
            // 先保存任务到数据库（为了获取任务ID）
            DelayedNoticeTask task = new DelayedNoticeTask();
            task.setNoticeId(message.getNoticeId());
            task.setTitle(message.getTitle());
            task.setContent(message.getContent());
            task.setUser(message.getUser());
            task.setImg(message.getImg());
            task.setScheduledPublishTime(scheduledPublishTime);
            task.setDelayMilliseconds(delayMilliseconds);
            task.setStatus(0); // 0-待发布
            task.setCreateTime(System.currentTimeMillis());
            task.setDescription("延迟发布任务，计划在 " + scheduledPublishTime + " 发布");
            
            int result = delayedNoticeTaskMapper.insert(task);
            
            // 将任务ID设置到消息中，供消费者更新状态使用
            message.setTaskId(task.getId());
            
            // 重新序列化消息（包含任务ID）
            messageJson = objectMapper.writeValueAsString(message);
            rabbitMessage = MessageBuilder
                    .withBody(messageJson.getBytes(StandardCharsets.UTF_8))
                    .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                    .setExpiration(String.valueOf(delayMilliseconds))
                    .build();
            
            // 重新发送到延迟队列（包含任务ID）
            rabbitTemplate.send(
                    DelayedNoticeQueueConfig.DELAYED_NOTICE_QUEUE,
                    rabbitMessage
            );
            
            logger.info("延迟发布公告消息已发送 - NoticeId: {}, TaskId: {}, 延迟时间: {}ms, 计划发布时间: {}", 
                       message.getNoticeId(), task.getId(), delayMilliseconds, scheduledPublishTime);
            
            return result > 0;
        } catch (Exception e) {
            logger.error("发送延迟发布公告消息失败 - NoticeId: {}", message.getNoticeId(), e);
            return false;
        }
    }

    /**
     * 在指定时间发送延迟通知
     * @param message 延迟通知消息
     * @param scheduledPublishTime 计划发布时间
     * @return 是否发送成功
     */
    @Override
    public boolean sendDelayedNoticeAtTime(DelayedNoticeMessage message, Long scheduledPublishTime) {
        try {
            Long currentTime = System.currentTimeMillis();
            if (scheduledPublishTime <= currentTime) {
                logger.warn("计划发布时间已过期，立即发布 - NoticeId: {}, 计划时间: {}, 当前时间: {}", 
                           message.getNoticeId(), scheduledPublishTime, currentTime);
                // 立即发布
                return publishNoticeImmediately(message);
            }
            
            // 计算延迟时间
            Long delayMilliseconds = scheduledPublishTime - currentTime;
            message.setScheduledPublishTime(scheduledPublishTime);
            
            return sendDelayedNotice(message, delayMilliseconds);
        } catch (Exception e) {
            logger.error("按指定时间发送延迟发布公告消息失败 - NoticeId: {}", message.getNoticeId(), e);
            return false;
        }
    }

    /**
     * 取消延迟通知
     * 注意：RabbitMQ无法直接取消队列中的消息
     * @param noticeId 通知ID
     * @return 是否取消成功
     */
    @Override
    public boolean cancelDelayedNotice(Integer noticeId) {
        // 注意：RabbitMQ无法直接取消队列中的消息
        // 实际应用中需要通过其他方式实现，比如：
        // 1. 使用Redis存储已取消的ID，消费者检查后忽略
        // 2. 使用数据库标记，消费者检查后忽略
        // 3. 删除并重新创建队列（不推荐）
        logger.warn("取消延迟发布功能需要在消息处理时判断，NoticeId: {}", noticeId);
        return true;
    }
    
    /**
     * 根据任务ID获取任务
     * @param taskId 任务ID
     * @return 延迟通知任务
     */
    @Override
    public DelayedNoticeTask getTaskById(Integer taskId) {
        return delayedNoticeTaskMapper.selectById(taskId);
    }

    /**
     * 根据通知ID获取任务
     * @param noticeId 通知ID
     * @return 延迟通知任务
     */
    @Override
    public DelayedNoticeTask getTaskByNoticeId(Integer noticeId) {
        QueryWrapper<DelayedNoticeTask> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("notice_id", noticeId);
        queryWrapper.orderByDesc("create_time");
        queryWrapper.last("LIMIT 1");
        return delayedNoticeTaskMapper.selectOne(queryWrapper);
    }

    /**
     * 更新任务状态
     * @param taskId 任务ID
     * @param status 状态
     * @param publishTime 发布时间
     * @return 是否更新成功
     */
    @Override
    @Transactional
    public boolean updateTaskStatus(Integer taskId, Integer status, Long publishTime) {
        try {
            DelayedNoticeTask task = delayedNoticeTaskMapper.selectById(taskId);
            if (task == null) {
                logger.warn("任务不存在，无法更新状态 - TaskId: {}", taskId);
                return false;
            }
            task.setStatus(status);
            if (publishTime != null) {
                task.setPublishTime(publishTime);
            }
            int result = delayedNoticeTaskMapper.updateById(task);
            logger.info("更新任务状态 - TaskId: {}, Status: {}, Result: {}", taskId, status, result > 0);
            return result > 0;
        } catch (Exception e) {
            logger.error("更新任务状态失败 - TaskId: {}", taskId, e);
            return false;
        }
    }
    
    /**
     * 分页查询任务列表
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param status 状态
     * @param title 标题
     * @return 任务分页列表
     */
    @Override
    public IPage<DelayedNoticeTask> listTasks(Integer pageNum, Integer pageSize, Integer status, String title) {
        Page<DelayedNoticeTask> page = new Page<>(pageNum, pageSize);
        QueryWrapper<DelayedNoticeTask> queryWrapper = new QueryWrapper<>();
        
        // 状态筛选
        if (status != null) {
            queryWrapper.eq("status", status);
        }
        
        // 标题搜索
        if (title != null && !title.trim().isEmpty()) {
            queryWrapper.like("title", title.trim());
        }
        
        // 按创建时间倒序
        queryWrapper.orderByDesc("create_time");
        
        return delayedNoticeTaskMapper.selectPage(page, queryWrapper);
    }
    
    /**
     * 立即发布通知
     * @param message 通知消息
     * @return 是否发布成功
     */
    private boolean publishNoticeImmediately(DelayedNoticeMessage message) {
        try {
            Notice notice = new Notice();
            notice.setId(message.getNoticeId());
            notice.setName(message.getTitle());
            notice.setContent(message.getContent());
            notice.setUser(message.getUser());
            notice.setImg(message.getImg());
            notice.setTime(String.valueOf(System.currentTimeMillis()));
            
            // 如果noticeId存在，更新通知；否则创建新通知
            if (message.getNoticeId() != null && message.getNoticeId() > 0) {
                return notificationService.updateNotice(notice);
            } else {
                return notificationService.createNotice(notice);
            }
        } catch (Exception e) {
            logger.error("立即发布通知失败 - NoticeId: {}", message.getNoticeId(), e);
            return false;
        }
    }
}
