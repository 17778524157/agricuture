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
 * 天气数据实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("weather_data")
public class WeatherData implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer fieldId;

    private BigDecimal temperature;

    private BigDecimal humidity;

    private BigDecimal rainfall;

    private BigDecimal windSpeed;

    private String weatherCondition;

    private LocalDateTime recordTime;

    private LocalDateTime createTime;
}
