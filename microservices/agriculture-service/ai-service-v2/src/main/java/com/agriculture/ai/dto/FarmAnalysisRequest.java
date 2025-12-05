package com.agriculture.ai.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 农田分析请求
 */
@Data
@Accessors(chain = true)
public class FarmAnalysisRequest {
    private String userId;
    private String farmId;
    private String farmName;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String soilType;
    private String climate;
    private String cropType;
    private BigDecimal area;
    private String description;
    private List<String> images;
}
