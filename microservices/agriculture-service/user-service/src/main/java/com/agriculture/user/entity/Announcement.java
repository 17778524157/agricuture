package com.agriculture.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 公告实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_announcement")
public class Announcement implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 公告标题
     */
    private String title;

    /**
     * 公告内容
     */
    private String content;

    /**
     * 公告类型（1:系统公告 2:活动公告 3:维护公告）
     */
    private Integer type;

    /**
     * 优先级（1:低 2:中 3:高）
     */
    private Integer priority;

    /**
     * 发布状态（0:草稿 1:待发布 2:已发布 3:已撤回）
     */
    private Integer status;

    /**
     * 创建人ID
     */
    @TableField("creator_id")
    private Long creatorId;

    /**
     * 创建人名称
     */
    @TableField("creator_name")
    private String creatorName;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 计划发布时间
     */
    @TableField("scheduled_time")
    private LocalDateTime scheduledTime;

    /**
     * 实际发布时间
     */
    @TableField("publish_time")
    private LocalDateTime publishTime;

    /**
     * 过期时间
     */
    @TableField("expire_time")
    private LocalDateTime expireTime;

    /**
     * 阅读次数
     */
    @TableField("read_count")
    private Integer readCount;
}
