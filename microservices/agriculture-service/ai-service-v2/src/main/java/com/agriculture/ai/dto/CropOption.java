package com.agriculture.ai.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

/**
 * 作物选项
 */
@Data
@Accessors(chain = true)
public class CropOption {
    private String cropName;
    private String cropType;
    private BigDecimal suitabilityScore;
    private String description;
    private String plantingSeason;
    private String harvestTime;
    private BigDecimal expectedYield;
    private BigDecimal marketPrice;
    private String difficulty;
    private List<String> advantages;
    private List<String> disadvantages;
}
