package com.agriculture.notification.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 延迟发布公告任务实体
 */
@Data
@TableName("delayed_notice_task")
public class DelayedNoticeTask implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 任务ID（主键）
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 公告ID（关联notice表）
     */
    @TableField("notice_id")
    private Integer noticeId;

    /**
     * 公告标题
     */
    @TableField("title")
    private String title;

    /**
     * 公告内容
     */
    @TableField("content")
    private String content;

    /**
     * 发布人
     */
    @TableField("user")
    private String user;

    /**
     * 封面图片URL
     */
    @TableField("img")
    private String img;

    /**
     * 计划发布时间（时间戳，毫秒）
     */
    @TableField("scheduled_publish_time")
    private Long scheduledPublishTime;

    /**
     * 延迟时间（毫秒）
     */
    @TableField("delay_milliseconds")
    private Long delayMilliseconds;

    /**
     * 任务状态：0-待发布，1-已发布，2-已取消，3-发布失败
     */
    @TableField("status")
    private Integer status;

    /**
     * 创建时间（时间戳，毫秒）
     */
    @TableField("create_time")
    private Long createTime;

    /**
     * 实际发布时间（时间戳，毫秒）
     */
    @TableField("publish_time")
    private Long publishTime;

    /**
     * 任务描述信息
     */
    @TableField("description")
    private String description;
}

