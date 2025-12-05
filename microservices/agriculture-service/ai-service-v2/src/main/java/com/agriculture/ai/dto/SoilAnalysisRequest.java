package com.agriculture.ai.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 土壤分析请求
 */
@Data
@Accessors(chain = true)
public class SoilAnalysisRequest {
    private String userId;
    private String farmId;
    private String soilType;
    private BigDecimal ph;
    private BigDecimal nitrogen;
    private BigDecimal phosphorus;
    private BigDecimal potassium;
    private BigDecimal organicMatter;
    private String moisture;
    private String texture;
}
