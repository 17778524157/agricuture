package com.agriculture.ai.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 天气影响分析请求
 */
@Data
@Accessors(chain = true)
public class WeatherImpactRequest {
    private String userId;
    private String farmId;
    private String weatherData;
    private String cropType;
    private String growthStage;
    private String region;
}
