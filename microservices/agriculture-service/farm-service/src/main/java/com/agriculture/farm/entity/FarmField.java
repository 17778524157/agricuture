package com.agriculture.farm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 农田实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("farm_field")
public class FarmField implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("field_name")
    private String fieldName;

    @TableField("field_code")
    private String fieldCode;

    @TableField("area")
    private BigDecimal area;

    @TableField("location")
    private String location;

    @TableField("soil_type")
    private String soilType;

    @TableField("status")
    private String status; // AVAILABLE-可用 RESERVED-已预订 OCCUPIED-已占用

    @TableField("description")
    private String description;

    @TableField("creator_id")
    private Integer creatorId;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;
}


