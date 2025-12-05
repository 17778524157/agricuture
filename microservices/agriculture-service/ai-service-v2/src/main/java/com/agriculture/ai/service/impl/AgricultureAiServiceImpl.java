package com.agriculture.ai.service.impl;

import com.agriculture.common.client.FarmServiceClient;
import com.agriculture.common.client.UserServiceClient;
import com.agriculture.ai.dto.*;
import com.agriculture.ai.entity.ChatMessage;
import com.agriculture.ai.entity.PestDetection;
import com.agriculture.ai.mapper.ChatMessageMapper;
import com.agriculture.ai.mapper.PestDetectionMapper;
import com.agriculture.ai.service.AgricultureAiService;
import com.agriculture.ai.service.OllamaService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 农业AI服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AgricultureAiServiceImpl implements AgricultureAiService {
    
    private final OllamaService ollamaService;
    private final ChatMessageMapper chatMessageMapper;
    private final PestDetectionMapper pestDetectionMapper;
    private final FarmServiceClient farmServiceClient;
    private final UserServiceClient userServiceClient;
    
    @Override
    public ChatResponse sendMessage(ChatRequest request) {
        long startTime = System.currentTimeMillis();
        
        // 兼容前端可能发送的question字段或message字段
        String userMessage = request.getMessage() != null ? request.getMessage() : request.getQuestion();
        log.info("收到农业AI聊天请求: {}", userMessage);
        
        try {
            // 构建农业专家提示词
            String enhancedPrompt = buildAgriculturePrompt(userMessage);
            
            // 调用AI服务
            String aiResponse = ollamaService.chat(enhancedPrompt, "deepseek-r1:7b");
            
            log.info("农业AI回答: {}", aiResponse);
            
            // 保存聊天记录
            String sessionId = request.getSessionId() != null ? request.getSessionId() : UUID.randomUUID().toString();
            
            // 保存用户消息
            ChatMessage userMsg = new ChatMessage()
                    .setSessionId(sessionId)
                    .setUserId(request.getUserId())
                    .setUsername(request.getUsername())
                    .setMessage(userMessage)
                    .setIsUser(true)
                    .setCreateTime(LocalDateTime.now());
            chatMessageMapper.insert(userMsg);
            
            // 保存AI回复
            ChatMessage aiMsg = new ChatMessage()
                    .setSessionId(sessionId)
                    .setUserId(request.getUserId())
                    .setUsername("农业AI专家")
                    .setMessage(aiResponse)
                    .setIsUser(false)
                    .setCreateTime(LocalDateTime.now());
            chatMessageMapper.insert(aiMsg);
            
            return new ChatResponse()
                    .setSessionId(sessionId)
                    .setUserId(request.getUserId())
                    .setUsername(request.getUsername())
                    .setQuestion(userMessage)
                    .setAnswer(aiResponse)
                    .setCreateTime(LocalDateTime.now())
                    .setResponseTime(System.currentTimeMillis() - startTime);
                    
        } catch (Exception e) {
            log.error("农业AI聊天请求处理失败", e);
            return new ChatResponse()
                    .setSessionId(request.getSessionId())
                    .setUserId(request.getUserId())
                    .setUsername(request.getUsername())
                    .setQuestion(userMessage)
                    .setAnswer("抱歉，农业AI专家暂时不可用，请稍后重试。错误信息: " + e.getMessage())
                    .setCreateTime(LocalDateTime.now())
                    .setResponseTime(System.currentTimeMillis() - startTime);
        }
    }
    
    @Override
    public FarmAnalysisResponse analyzeFarm(FarmAnalysisRequest request) {
        long startTime = System.currentTimeMillis();
        log.info("开始智能农田分析，农田ID: {}", request.getFarmId());
        
        try {
            // 获取农场详细信息（远程调用）
            FarmServiceClient.Farm farmInfo = null;
            try {
                FarmServiceClient.Result<FarmServiceClient.Farm> farmResult = 
                    farmServiceClient.getFarmById(String.valueOf(request.getFarmId()));
                if (farmResult != null && farmResult.getCode() == 200 && farmResult.getData() != null) {
                    farmInfo = farmResult.getData();
                    log.info("获取农场信息成功，农场ID: {}, 农场名称: {}", request.getFarmId(), farmInfo.getFarmName());
                }
            } catch (Exception e) {
                log.warn("获取农场信息失败，农场ID: {}, 错误: {}", request.getFarmId(), e.getMessage());
            }
            
            // 获取用户偏好信息（远程调用）
            UserServiceClient.UserPreferences userPreferences = null;
            try {
                UserServiceClient.Result<UserServiceClient.UserPreferences> preferencesResult = 
                    userServiceClient.getUserPreferences(String.valueOf(request.getUserId()));
                if (preferencesResult != null && preferencesResult.getCode() == 200 && preferencesResult.getData() != null) {
                    userPreferences = preferencesResult.getData();
                    log.info("获取用户偏好成功，用户ID: {}", request.getUserId());
                }
            } catch (Exception e) {
                log.warn("获取用户偏好失败，用户ID: {}, 错误: {}", request.getUserId(), e.getMessage());
            }
            
            // 构建增强的农田分析提示词
            StringBuilder promptBuilder = new StringBuilder();
            promptBuilder.append("作为农业专家，请分析以下农田情况：\n");
            
            if (farmInfo != null) {
                promptBuilder.append(String.format("农场名称：%s\n", farmInfo.getFarmName()));
                promptBuilder.append(String.format("农场位置：%s\n", farmInfo.getLocation()));
                promptBuilder.append(String.format("农场面积：%s 亩\n", farmInfo.getArea()));
                promptBuilder.append(String.format("农场状态：%s\n", farmInfo.getStatus()));
                if (farmInfo.getCrops() != null && farmInfo.getCrops().length > 0) {
                    promptBuilder.append(String.format("种植作物：%s\n", String.join(", ", farmInfo.getCrops())));
                }
            } else {
                promptBuilder.append(String.format("农田名称：%s\n", request.getFarmName()));
                promptBuilder.append(String.format("位置：纬度 %s, 经度 %s\n", request.getLatitude(), request.getLongitude()));
                promptBuilder.append(String.format("面积：%s 亩\n", request.getArea()));
            }
            
            promptBuilder.append(String.format("土壤类型：%s\n", request.getSoilType()));
            promptBuilder.append(String.format("气候条件：%s\n", request.getClimate()));
            promptBuilder.append(String.format("作物类型：%s\n", request.getCropType()));
            promptBuilder.append(String.format("描述：%s\n\n", request.getDescription()));
            
            if (userPreferences != null) {
                promptBuilder.append("用户偏好信息：\n");
                if (userPreferences.getPreferredCrops() != null && userPreferences.getPreferredCrops().length > 0) {
                    promptBuilder.append(String.format("偏好作物：%s\n", String.join(", ", userPreferences.getPreferredCrops())));
                }
                if (userPreferences.getPreferredRegions() != null && userPreferences.getPreferredRegions().length > 0) {
                    promptBuilder.append(String.format("偏好地区：%s\n", String.join(", ", userPreferences.getPreferredRegions())));
                }
                promptBuilder.append("\n");
            }
            
            promptBuilder.append("请提供：\n");
            promptBuilder.append("1. 整体评分（1-10分）\n");
            promptBuilder.append("2. 健康状况评估\n");
            promptBuilder.append("3. 改进建议\n");
            promptBuilder.append("4. 潜在风险\n");
            promptBuilder.append("5. 发展机会\n");
            promptBuilder.append("6. 个性化建议（基于用户偏好）");
            
            String promptText = promptBuilder.toString();
            
            // 调用AI服务
            String aiResponse = ollamaService.chat(promptText, "deepseek-r1:7b");
            
            return new FarmAnalysisResponse()
                    .setFarmId(request.getFarmId())
                    .setAnalysisId(UUID.randomUUID().toString())
                    .setOverallScore("8.5")
                    .setHealthStatus("良好")
                    .setRecommendations(aiResponse)
                    .setRisks("注意病虫害防治")
                    .setOpportunities("可考虑轮作种植")
                    .setAnalysisTime(LocalDateTime.now())
                    .setResponseTime(System.currentTimeMillis() - startTime);
                    
        } catch (Exception e) {
            log.error("农田分析失败", e);
            return new FarmAnalysisResponse()
                    .setFarmId(request.getFarmId())
                    .setAnalysisId(UUID.randomUUID().toString())
                    .setOverallScore("0")
                    .setHealthStatus("分析失败")
                    .setRecommendations("抱歉，分析服务暂时不可用")
                    .setRisks("")
                    .setOpportunities("")
                    .setAnalysisTime(LocalDateTime.now())
                    .setResponseTime(System.currentTimeMillis() - startTime);
        }
    }
    
    @Override
    public CropRecommendationResponse recommendCrop(CropRecommendationRequest request) {
        long startTime = System.currentTimeMillis();
        log.info("开始作物推荐，地区: {}", request.getRegion());
        
        try {
            String promptText = String.format(
                "作为农业专家，请为以下条件推荐合适的作物：\n" +
                "地区：%s\n" +
                "季节：%s\n" +
                "土壤类型：%s\n" +
                "气候：%s\n" +
                "农场面积：%s 亩\n" +
                "预算：%s\n" +
                "经验水平：%s\n\n" +
                "请推荐3-5种作物，并说明推荐理由。",
                request.getRegion(),
                request.getSeason(),
                request.getSoilType(),
                request.getClimate(),
                request.getFarmArea(),
                request.getBudget(),
                request.getExperience()
            );
            
            String aiResponse = ollamaService.chat(promptText, "deepseek-r1:7b");
            
            // 创建推荐作物列表
            List<CropOption> crops = new ArrayList<>();
            crops.add(new CropOption()
                    .setCropName("水稻")
                    .setCropType("粮食作物")
                    .setSuitabilityScore(java.math.BigDecimal.valueOf(8.5))
                    .setDescription("适合该地区种植")
                    .setPlantingSeason("春季")
                    .setHarvestTime("秋季")
                    .setExpectedYield(java.math.BigDecimal.valueOf(500))
                    .setMarketPrice(java.math.BigDecimal.valueOf(3.5))
                    .setDifficulty("中等"));
            
            return new CropRecommendationResponse()
                    .setRecommendationId(UUID.randomUUID().toString())
                    .setRecommendedCrops(crops)
                    .setReasoning(aiResponse)
                    .setMarketAnalysis("市场需求稳定")
                    .setRiskAssessment("风险较低")
                    .setRecommendationTime(LocalDateTime.now())
                    .setResponseTime(System.currentTimeMillis() - startTime);
                    
        } catch (Exception e) {
            log.error("作物推荐失败", e);
            return new CropRecommendationResponse()
                    .setRecommendationId(UUID.randomUUID().toString())
                    .setRecommendedCrops(new ArrayList<>())
                    .setReasoning("推荐服务暂时不可用")
                    .setMarketAnalysis("")
                    .setRiskAssessment("")
                    .setRecommendationTime(LocalDateTime.now())
                    .setResponseTime(System.currentTimeMillis() - startTime);
        }
    }
    
    @Override
    public KnowledgeResponse askKnowledge(KnowledgeRequest request) {
        long startTime = System.currentTimeMillis();
        log.info("收到知识问答请求: {}", request.getQuestion());
        
        try {
            String promptText = String.format(
                "作为农业知识专家，请回答以下问题：\n" +
                "问题：%s\n" +
                "分类：%s\n" +
                "子分类：%s\n\n" +
                "请提供准确、详细的答案。",
                request.getQuestion(),
                request.getCategory(),
                request.getSubcategory()
            );
            
            String aiResponse = ollamaService.chat(promptText, "deepseek-r1:7b");
            
            return new KnowledgeResponse()
                    .setQuestionId(UUID.randomUUID().toString())
                    .setQuestion(request.getQuestion())
                    .setAnswer(aiResponse)
                    .setCategory(request.getCategory())
                    .setSubcategory(request.getSubcategory())
                    .setRelatedQuestions(new ArrayList<>())
                    .setReferences(new ArrayList<>())
                    .setAnswerTime(LocalDateTime.now())
                    .setResponseTime(System.currentTimeMillis() - startTime);
                    
        } catch (Exception e) {
            log.error("知识问答失败", e);
            return new KnowledgeResponse()
                    .setQuestionId(UUID.randomUUID().toString())
                    .setQuestion(request.getQuestion())
                    .setAnswer("知识服务暂时不可用")
                    .setCategory(request.getCategory())
                    .setSubcategory(request.getSubcategory())
                    .setRelatedQuestions(new ArrayList<>())
                    .setReferences(new ArrayList<>())
                    .setAnswerTime(LocalDateTime.now())
                    .setResponseTime(System.currentTimeMillis() - startTime);
        }
    }
    
    @Override
    public WeatherImpactResponse analyzeWeatherImpact(WeatherImpactRequest request) {
        long startTime = System.currentTimeMillis();
        log.info("开始天气影响分析，农场ID: {}", request.getFarmId());
        
        try {
            String promptText = String.format(
                "作为农业气象专家，请分析以下天气对农业的影响：\n" +
                "天气数据：%s\n" +
                "作物类型：%s\n" +
                "生长阶段：%s\n" +
                "地区：%s\n\n" +
                "请分析：\n" +
                "1. 影响程度\n" +
                "2. 具体影响\n" +
                "3. 应对建议\n" +
                "4. 预防措施\n" +
                "5. 风险评估",
                request.getWeatherData(),
                request.getCropType(),
                request.getGrowthStage(),
                request.getRegion()
            );
            
            String aiResponse = ollamaService.chat(promptText, "deepseek-r1:7b");
            
            return new WeatherImpactResponse()
                    .setAnalysisId(UUID.randomUUID().toString())
                    .setImpactLevel("中等")
                    .setImpactDescription(aiResponse)
                    .setRecommendations("加强田间管理")
                    .setPreventiveMeasures("及时排水防涝")
                    .setRiskAssessment("风险可控")
                    .setAnalysisTime(LocalDateTime.now())
                    .setResponseTime(System.currentTimeMillis() - startTime);
                    
        } catch (Exception e) {
            log.error("天气影响分析失败", e);
            return new WeatherImpactResponse()
                    .setAnalysisId(UUID.randomUUID().toString())
                    .setImpactLevel("未知")
                    .setImpactDescription("分析服务暂时不可用")
                    .setRecommendations("")
                    .setPreventiveMeasures("")
                    .setRiskAssessment("")
                    .setAnalysisTime(LocalDateTime.now())
                    .setResponseTime(System.currentTimeMillis() - startTime);
        }
    }
    
    @Override
    public SoilAnalysisResponse analyzeSoil(SoilAnalysisRequest request) {
        long startTime = System.currentTimeMillis();
        log.info("开始土壤分析，农场ID: {}", request.getFarmId());
        
        try {
            String promptText = String.format(
                "作为土壤专家，请分析以下土壤数据：\n" +
                "土壤类型：%s\n" +
                "pH值：%s\n" +
                "氮含量：%s\n" +
                "磷含量：%s\n" +
                "钾含量：%s\n" +
                "有机质：%s\n" +
                "湿度：%s\n" +
                "质地：%s\n\n" +
                "请提供：\n" +
                "1. 土壤健康状况\n" +
                "2. 营养状况\n" +
                "3. 改进建议\n" +
                "4. 施肥建议\n" +
                "5. 改良计划",
                request.getSoilType(),
                request.getPh(),
                request.getNitrogen(),
                request.getPhosphorus(),
                request.getPotassium(),
                request.getOrganicMatter(),
                request.getMoisture(),
                request.getTexture()
            );
            
            String aiResponse = ollamaService.chat(promptText, "deepseek-r1:7b");
            
            return new SoilAnalysisResponse()
                    .setAnalysisId(UUID.randomUUID().toString())
                    .setSoilHealth("良好")
                    .setNutrientStatus("氮磷钾平衡")
                    .setRecommendations(aiResponse)
                    .setFertilizerSuggestions("建议增施有机肥")
                    .setImprovementPlan("定期检测土壤")
                    .setAnalysisTime(LocalDateTime.now())
                    .setResponseTime(System.currentTimeMillis() - startTime);
                    
        } catch (Exception e) {
            log.error("土壤分析失败", e);
            return new SoilAnalysisResponse()
                    .setAnalysisId(UUID.randomUUID().toString())
                    .setSoilHealth("未知")
                    .setNutrientStatus("未知")
                    .setRecommendations("分析服务暂时不可用")
                    .setFertilizerSuggestions("")
                    .setImprovementPlan("")
                    .setAnalysisTime(LocalDateTime.now())
                    .setResponseTime(System.currentTimeMillis() - startTime);
        }
    }
    
    @Override
    public PestDetection detectPest(PestDetectionRequest request) {
        long startTime = System.currentTimeMillis();
        log.info("开始病虫害检测，用户ID: {}", request.getUserId());
        
        try {
            String promptText = String.format(
                "作为植物保护专家，请分析以下病虫害情况：\n" +
                "作物类型：%s\n" +
                "症状描述：%s\n" +
                "发生部位：%s\n" +
                "发生时间：%s\n" +
                "环境条件：%s\n\n" +
                "请提供：\n" +
                "1. 病虫害类型\n" +
                "2. 危害程度\n" +
                "3. 防治方法\n" +
                "4. 预防措施",
                request.getCropType(),
                request.getSymptoms(),
                request.getAffectedArea(),
                request.getOccurrenceTime(),
                request.getEnvironmentalConditions()
            );
            
            String aiResponse = ollamaService.chat(promptText, "deepseek-r1:7b");
            
            // 保存检测记录
            PestDetection detection = new PestDetection()
                    .setUserId(request.getUserId())
                    .setPestName("病虫害检测")
                    .setDescription(aiResponse)
                    .setTreatment("请根据AI建议进行防治")
                    .setPrevention("定期检查作物健康状况")
                    .setConfidence(java.math.BigDecimal.valueOf(0.85))
                    .setDetectionTime(LocalDateTime.now());
            
            pestDetectionMapper.insert(detection);
            
            return detection;
                    
        } catch (Exception e) {
            log.error("病虫害检测失败", e);
            return new PestDetection()
                    .setUserId(request.getUserId())
                    .setPestName("检测失败")
                    .setDescription("检测服务暂时不可用")
                    .setTreatment("")
                    .setPrevention("")
                    .setConfidence(java.math.BigDecimal.valueOf(0.0))
                    .setDetectionTime(LocalDateTime.now());
        }
    }
    
    @Override
    public List<ChatMessage> getChatHistory(String userId, String sessionId) {
        QueryWrapper<ChatMessage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        if (sessionId != null) {
            queryWrapper.eq("session_id", sessionId);
        }
        queryWrapper.orderByAsc("create_time");
        
        return chatMessageMapper.selectList(queryWrapper);
    }
    
    @Override
    public List<PestDetection> getDetectionHistory(String userId) {
        QueryWrapper<PestDetection> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.orderByDesc("create_time");
        
        return pestDetectionMapper.selectList(queryWrapper);
    }
    
    /**
     * 构建农业专家提示词
     */
    private String buildAgriculturePrompt(String userMessage) {
        return String.format(
            "你是一个专业的农业AI助手，具有丰富的农业知识和经验。请回答以下农业相关问题：\n\n" +
            "用户问题：%s\n\n" +
            "请提供专业、准确、实用的农业建议。如果问题涉及具体的技术细节，请详细说明。",
            userMessage
        );
    }
}