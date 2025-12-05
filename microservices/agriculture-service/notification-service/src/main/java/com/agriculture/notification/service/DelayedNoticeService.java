package com.agriculture.notification.service;

import com.agriculture.notification.dto.DelayedNoticeMessage;

/**
 * 定时发布公告服务接口
 */
public interface DelayedNoticeService {
    
    /**
     * 发送延迟发布公告消息
     * @param message 延迟发布消息
     * @param delayMilliseconds 延迟时间（毫秒）
     * @return 是否发送成功
     */
    boolean sendDelayedNotice(DelayedNoticeMessage message, Long delayMilliseconds);
    
    /**
     * 根据指定时间发送延迟发布公告消息
     * @param message 延迟发布消息
     * @param scheduledPublishTime 计划发布时间（时间戳）
     * @return 是否发送成功
     */
    boolean sendDelayedNoticeAtTime(DelayedNoticeMessage message, Long scheduledPublishTime);
    
    /**
     * 取消延迟发布（需要移除队列中的消息）
     * @param noticeId 通知ID
     * @return 是否取消成功
     */
    boolean cancelDelayedNotice(Integer noticeId);
    
    /**
     * 根据任务ID查询延迟发布任务详情
     * @param taskId 任务ID
     * @return 任务详情
     */
    com.agriculture.notification.entity.DelayedNoticeTask getTaskById(Integer taskId);
    
    /**
     * 根据公告ID查询延迟发布任务详情
     * @param noticeId 公告ID
     * @return 任务详情
     */
    com.agriculture.notification.entity.DelayedNoticeTask getTaskByNoticeId(Integer noticeId);
    
    /**
     * 更新任务状态
     * @param taskId 任务ID
     * @param status 新状态：0-待发布，1-已发布，2-已取消，3-发布失败
     * @param publishTime 实际发布时间（可选）
     * @return 是否更新成功
     */
    boolean updateTaskStatus(Integer taskId, Integer status, Long publishTime);
    
    /**
     * 分页查询延迟发布任务列表
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param status 状态筛选（可选）
     * @param title 标题搜索（可选）
     * @return 任务列表
     */
    com.baomidou.mybatisplus.core.metadata.IPage<com.agriculture.notification.entity.DelayedNoticeTask> listTasks(Integer pageNum, Integer pageSize, Integer status, String title);
}
