package com.agriculture.ai.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 病虫害检测结果实体
 */
@Data
@Accessors(chain = true)
@TableName("pest_detections")
public class PestDetection {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String userId;
    
    private String imagePath;
    
    private String imageUrl;
    
    private String pestName;
    
    private BigDecimal confidence;
    
    private String description;
    
    private String treatment;
    
    private String prevention;
    
    private LocalDateTime detectionTime;

    public PestDetection setDetectionTime(LocalDateTime now) {
        this.detectionTime = now;
        return this;
    }
}
