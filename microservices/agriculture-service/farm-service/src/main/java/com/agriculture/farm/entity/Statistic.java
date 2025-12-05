package com.agriculture.farm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 统计数据实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("statistic")
public class Statistic implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer fieldId;

    private String type;

    private BigDecimal value;

    private String unit;

    private LocalDateTime recordDate;

    private LocalDateTime createTime;
}
