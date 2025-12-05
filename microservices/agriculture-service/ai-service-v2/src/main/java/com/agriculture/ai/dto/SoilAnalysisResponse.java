package com.agriculture.ai.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 土壤分析响应
 */
@Data
@Accessors(chain = true)
public class SoilAnalysisResponse {
    private String analysisId;
    private String soilHealth;
    private String nutrientStatus;
    private String recommendations;
    private String fertilizerSuggestions;
    private String improvementPlan;
    private LocalDateTime analysisTime;
    private Long responseTime;
}
