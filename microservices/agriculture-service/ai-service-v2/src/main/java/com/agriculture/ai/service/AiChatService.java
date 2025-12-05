package com.agriculture.ai.service;

import com.agriculture.ai.dto.ChatRequest;
import com.agriculture.ai.dto.ChatResponse;
import com.agriculture.ai.dto.PestDetectionRequest;
import com.agriculture.ai.entity.ChatMessage;
import com.agriculture.ai.entity.PestDetection;

import java.util.List;

/**
 * AI聊天服务接口
 */
public interface AiChatService {
    
    /**
     * 发送聊天消息
     */
    ChatResponse sendMessage(ChatRequest request);
    
    /**
     * 病虫害检测
     */
    PestDetection detectPest(PestDetectionRequest request);
    
    /**
     * 获取聊天历史
     */
    List<ChatMessage> getChatHistory(String userId, String sessionId);
    
    /**
     * 获取检测历史
     */
    List<PestDetection> getDetectionHistory(String userId);
}
