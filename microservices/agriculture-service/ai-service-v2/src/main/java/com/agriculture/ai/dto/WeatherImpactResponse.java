package com.agriculture.ai.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 天气影响分析响应
 */
@Data
@Accessors(chain = true)
public class WeatherImpactResponse {
    private String analysisId;
    private String impactLevel;
    private String impactDescription;
    private String recommendations;
    private String preventiveMeasures;
    private String riskAssessment;
    private LocalDateTime analysisTime;
    private Long responseTime;
}
