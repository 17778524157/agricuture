package com.agriculture.notification.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 农田异常预警消息实体类
 * 用于在微服务之间传递农田异常预警信息
 */
@Data
public class FarmAlertMessage implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    // 预警类型（如：干旱、病虫害、水灾等）
    private String alertType;
    
    // 农田名称
    private String farmName;
    
    // 农田位置
    private String farmLocation;
    
    // 预警详细内容
    private String alertContent;
    
    // 预警时间
    private String alertTime;
    
    // 相关用户ID
    private String userId;
    
    // 相关用户邮箱
    private String userEmail;
    
    // 预警级别（如：低、中、高）
    private String alertLevel;
    
    // 建议处理措施
    private String suggestedActions;
}