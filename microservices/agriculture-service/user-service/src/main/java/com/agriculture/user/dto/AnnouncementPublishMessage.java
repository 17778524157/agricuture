package com.agriculture.user.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 公告发布消息
 * 用于在RabbitMQ中传递
 */
@Data
public class AnnouncementPublishMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 公告ID
     */
    private Long announcementId;

    /**
     * 发布模式
     * DELAY: 延迟发布（延迟X秒后发布）
     * SCHEDULED: 定时发布（在指定时间发布）
     */
    private PublishMode publishMode;

    /**
     * 延迟时间（毫秒）
     * publishMode=DELAY时使用
     */
    private Long delayMillis;

    /**
     * 计划发布时间戳（毫秒）
     * publishMode=SCHEDULED时使用
     */
    private Long scheduledTimestamp;

    /**
     * 消息创建时间戳
     */
    private Long createTimestamp;

    /**
     * 发布模式枚举
     */
    public enum PublishMode {
        DELAY,      // 延迟发布
        SCHEDULED   // 定时发布
    }
}
