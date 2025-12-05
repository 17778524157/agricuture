package com.agriculture.user.service;

import com.agriculture.common.result.Result;
import com.agriculture.user.dto.AnnouncementPublishMessage;
import com.agriculture.user.entity.Announcement;

import java.time.LocalDateTime;

/**
 * 公告服务接口
 */
public interface AnnouncementService {
    
    /**
     * 创建公告（草稿）
     */
    Result<Announcement> createAnnouncement(Announcement announcement);
    
    /**
     * 延迟发布公告
     * @param announcementId 公告ID
     * @param delaySeconds 延迟秒数
     */
    Result scheduleDelayPublish(Long announcementId, Long delaySeconds);
    
    /**
     * 定时发布公告（指定时间）
     * @param announcementId 公告ID
     * @param scheduledTime 计划发布时间
     */
    Result scheduleTimedPublish(Long announcementId, LocalDateTime scheduledTime);
    
    /**
     * 立即发布公告
     * @param announcementId 公告ID
     */
    Result publishImmediately(Long announcementId);
    
    /**
     * 撤回公告
     */
    Result withdrawAnnouncement(Long announcementId);
}
