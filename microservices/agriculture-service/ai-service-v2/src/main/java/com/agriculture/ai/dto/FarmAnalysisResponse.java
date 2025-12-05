package com.agriculture.ai.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 农田分析响应
 */
@Data
@Accessors(chain = true)
public class FarmAnalysisResponse {
    private String farmId;
    private String analysisId;
    private String overallScore;
    private String healthStatus;
    private String recommendations;
    private String risks;
    private String opportunities;
    private LocalDateTime analysisTime;
    private Long responseTime;
}
