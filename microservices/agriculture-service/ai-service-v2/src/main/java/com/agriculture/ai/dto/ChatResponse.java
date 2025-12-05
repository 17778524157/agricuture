package com.agriculture.ai.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 聊天响应DTO
 */
@Data
@Accessors(chain = true)
public class ChatResponse {
    
    private String sessionId;
    
    private String userId;
    
    private String username;
    
    private String question;
    
    private String answer;
    
    private LocalDateTime createTime;
    
    private Long responseTime;
}
