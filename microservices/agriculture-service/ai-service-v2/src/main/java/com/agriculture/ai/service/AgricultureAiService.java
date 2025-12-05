package com.agriculture.ai.service;

import com.agriculture.ai.dto.*;
import com.agriculture.ai.entity.ChatMessage;
import com.agriculture.ai.entity.PestDetection;

import java.util.List;

/**
 * 农业AI服务接口
 */
public interface AgricultureAiService {
    
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
    
    /**
     * 智能农田分析
     */
    FarmAnalysisResponse analyzeFarm(FarmAnalysisRequest request);
    
    /**
     * 作物推荐
     */
    CropRecommendationResponse recommendCrop(CropRecommendationRequest request);
    
    /**
     * 农业知识问答
     */
    KnowledgeResponse askKnowledge(KnowledgeRequest request);
    
    /**
     * 天气影响分析
     */
    WeatherImpactResponse analyzeWeatherImpact(WeatherImpactRequest request);
    
    /**
     * 土壤分析建议
     */
    SoilAnalysisResponse analyzeSoil(SoilAnalysisRequest request);
}
