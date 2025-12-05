package com.agriculture.ai.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

/**
 * 作物推荐请求
 */
@Data
@Accessors(chain = true)
public class CropRecommendationRequest {
    private String userId;
    private String region;
    private String season;
    private String soilType;
    private String climate;
    private BigDecimal farmArea;
    private String budget;
    private String experience;
    private List<String> preferences;
}
