package com.agriculture.ai.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 聊天请求DTO
 */
@Data
public class ChatRequest {
    
    @NotBlank(message = "用户ID不能为空")
    private String userId;
    
    @NotBlank(message = "用户名不能为空")
    private String username;
    
    @NotBlank(message = "消息内容不能为空")
    private String message;
    
    // 兼容前端可能发送的question字段
    private String question;
    
    private String sessionId;
}
