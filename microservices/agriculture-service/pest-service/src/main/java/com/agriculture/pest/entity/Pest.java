package com.agriculture.pest.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;
import java.math.BigDecimal;

@TableName("pests")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pest {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("farm_id")
    private Integer farmId;

    @TableField(value = "pest_type")
    private String pestType;

    @TableField(value = "discovery_time")
    private LocalDateTime discoveryTime;

    @TableField(value = "latitude")
    private BigDecimal latitude;

    @TableField(value = "longitude")
    private BigDecimal longitude;

    @TableField("severity")
    private String severity;

    @TableField(value = "description")
    private String description;

    @TableField("reporter_id")
    private Integer reporterId;

    @TableField(value = "status")
    private String status;

    @TableField(value = "create_time")
    private LocalDateTime createTime;

    @TableField(value = "update_time")
    private LocalDateTime updateTime;

    @TableField(value = "image_urls")
    private String imageUrls;

    @TableField(value = "treatment_plan")
    private String treatmentPlan;

    @TableField(value = "treatment_result")
    private String treatmentResult;

    @TableField(value = "handler_id")
    private Integer handlerId;

    @TableField(value = "treatment_time")
    private LocalDateTime treatmentTime;

    @TableField(value = "is_deleted")
    private Integer isDeleted = 0;
}