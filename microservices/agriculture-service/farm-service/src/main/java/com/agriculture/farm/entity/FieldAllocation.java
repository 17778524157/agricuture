package com.agriculture.farm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 农田分配实体类
 * 用于存储农田分配相关的信息，包括分配的用户、农田、时间状态等
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("field_allocation")
public class FieldAllocation implements Serializable {

    private static final long serialVersionUID = 1L; // 序列化版本UID，用于对象序列化和反序列化时的版本控制

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id; // 分配记录的主键ID，自增长类型

    @TableField("user_id")
    private Integer userId; // 分配到的用户ID，关联用户表的主键

    @TableField("field_id")
    private Integer fieldId; // 分配到的农田ID，关联农田表的主键

    @TableField("start_date")
    private LocalDateTime startDate; // 农田分配的开始时间，精确到秒

    @TableField("end_date")
    private LocalDateTime endDate; // 农田分配的结束时间，精确到秒

    @TableField("allocation_time")
    private LocalDateTime allocationTime; // 农田分配的分配时间，精确到秒

    @TableField("confirm_time")
    private LocalDateTime confirmTime; // 农田分配的确认时间，精确到秒

    @TableField("release_time")
    private LocalDateTime releaseTime; // 农田分配的释放时间，精确到秒

    @TableField("status")
    private String status; // PENDING-待确认 CONFIRMED-已确认 RELEASED-已释放 CANCELLED-已取消

    @TableField("purpose")
    private String purpose;

    @TableField("notes")
    private String notes;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;
}
