package com.agriculture.ai.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 病虫害检测请求
 */
@Data
@Accessors(chain = true)
public class PestDetectionRequest {
    private String userId;
    private String cropType;
    private String symptoms;
    private String affectedArea;
    private String occurrenceTime;
    private String environmentalConditions;
}