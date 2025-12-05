package com.agriculture.notification.dto;

import lombok.Data;
import java.io.Serializable;

/**
 * 延迟发布公告消息实体
 * 该类用于封装延迟发布公告所需的信息，包括通知的基本信息、发布时间等
 */
@Data
public class DelayedNoticeMessage implements Serializable {
    
    // 序列化版本号，用于Java对象的序列化和反序列化
    private static final long serialVersionUID = 1L;
    
    /**
     * 通知ID
     * 唯一标识一条通知记录
     */
    private Integer noticeId;
    
    /**
     * 通知标题
     * 用于显示通知的主题
     */
    private String title;
    
    /**
     * 通知内容
     * 包含通知的具体文本信息
     */
    private String content;
    
    /**
     * 发布人
     * 记录发布通知的用户信息
     */
    private String user;
    
    /**
     * 封面图片
     * 通知的封面图片URL或路径
     */
    private String img;
    
    /**
     * 计划发布时间（时间戳）
     * 设定通知计划发布的具体时间，使用时间戳格式
     */
    private Long scheduledPublishTime;
    
    /**
     * 创建时间（时间戳）
     * 记录通知创建的时间，使用时间戳格式
     */
    private Long createTime;
    
    /**
     * 任务ID（数据库任务表的ID）
     * 关联到数据库中的任务记录
     */
    private Integer taskId;
    
    /**
     * 无参构造函数
     * 初始化创建时间为当前系统时间
     */
    public DelayedNoticeMessage() {
        this.createTime = System.currentTimeMillis();
    }
    
    /**
     * 全参构造函数
     * @param noticeId 通知ID
     * @param title 通知标题
     * @param content 通知内容
     * @param user 发布人
     * @param img 封面图片
     * @param scheduledPublishTime 计划发布时间
     */
    public DelayedNoticeMessage(Integer noticeId, String title, String content,
                               String user, String img, Long scheduledPublishTime) {
        this.noticeId = noticeId;
        this.title = title;
        this.content = content;
        this.user = user;
        this.img = img;
        this.scheduledPublishTime = scheduledPublishTime;
        this.createTime = System.currentTimeMillis();
    }
}
