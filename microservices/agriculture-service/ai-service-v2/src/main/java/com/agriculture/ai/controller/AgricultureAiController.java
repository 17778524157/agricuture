package com.agriculture.ai.controller;

import com.agriculture.ai.dto.*;
import com.agriculture.ai.entity.ChatMessage;
import com.agriculture.ai.entity.PestDetection;
import com.agriculture.ai.service.AgricultureAiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

/**
 * 农业AI控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AgricultureAiController {
    
    private final AgricultureAiService agricultureAiService;
    
    @PostMapping("/chat")
    public ChatResponse chat(@RequestBody ChatRequest request) {
        log.info("收到AI聊天请求: {}", request);
        return agricultureAiService.sendMessage(request);
    }
    
    @PostMapping("/farm/analyze")
    public FarmAnalysisResponse analyzeFarm(@RequestBody FarmAnalysisRequest request) {
        log.info("收到农田分析请求: {}", request);
        return agricultureAiService.analyzeFarm(request);
    }
    
    @PostMapping("/crop/recommend")
    public CropRecommendationResponse recommendCrop(@RequestBody CropRecommendationRequest request) {
        log.info("收到作物推荐请求: {}", request);
        return agricultureAiService.recommendCrop(request);
    }
    
    @PostMapping("/knowledge/ask")
    public KnowledgeResponse askKnowledge(@RequestBody KnowledgeRequest request) {
        log.info("收到知识问答请求: {}", request);
        return agricultureAiService.askKnowledge(request);
    }
    
    @PostMapping("/weather/analyze")
    public WeatherImpactResponse analyzeWeatherImpact(@RequestBody WeatherImpactRequest request) {
        log.info("收到天气影响分析请求: {}", request);
        return agricultureAiService.analyzeWeatherImpact(request);
    }
    
    @PostMapping("/soil/analyze")
    public SoilAnalysisResponse analyzeSoil(@RequestBody SoilAnalysisRequest request) {
        log.info("收到土壤分析请求: {}", request);
        return agricultureAiService.analyzeSoil(request);
    }
    
    @PostMapping("/pest/detect")
    public PestDetection detectPest(@RequestBody PestDetectionRequest request) {
        log.info("收到病虫害检测请求: {}", request);
        return agricultureAiService.detectPest(request);
    }
    
    @GetMapping("/chat/history")
    public List<ChatMessage> getChatHistory(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String sessionId) {
        log.info("获取聊天历史，用户ID: {}, 会话ID: {}", userId, sessionId);
        if (userId == null || userId.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return agricultureAiService.getChatHistory(userId, sessionId);
    }

    @GetMapping("/detection/history")
    public List<PestDetection> getDetectionHistory(@RequestParam(required = false) String userId) {
        log.info("获取检测历史，用户ID: {}", userId);
        if (userId == null || userId.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return agricultureAiService.getDetectionHistory(userId);
    }
    
    @GetMapping("/health")
    public String healthCheck() {
        return "农业AI服务运行正常";
    }
    
    @GetMapping("/models")
    public String getAvailableModels() {
        return "当前使用模型: deepseek-r1:7b";
    }
}
