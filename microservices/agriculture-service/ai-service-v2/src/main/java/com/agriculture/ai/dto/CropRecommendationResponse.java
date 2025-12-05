package com.agriculture.ai.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 作物推荐响应
 */
@Data
@Accessors(chain = true)
public class CropRecommendationResponse {
    private String recommendationId;
    private List<CropOption> recommendedCrops;
    private String reasoning;
    private String marketAnalysis;
    private String riskAssessment;
    private LocalDateTime recommendationTime;
    private Long responseTime;
}
