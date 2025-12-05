package com.agriculture.weather.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.math.BigDecimal;

@TableName("weather_data")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeatherData {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Integer farmId;

    private LocalDateTime recordTime;

    private BigDecimal temperature;

    private BigDecimal precipitation;

    private BigDecimal windSpeed;

    private BigDecimal humidity;

    private BigDecimal lightIntensity;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

}