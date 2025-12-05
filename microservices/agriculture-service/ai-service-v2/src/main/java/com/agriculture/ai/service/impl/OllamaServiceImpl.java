package com.agriculture.ai.service.impl;

import com.agriculture.ai.service.OllamaService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Ollama AI服务实现
 * 使用WebClient直接调用Ollama REST API，兼容Spring Boot 2.5.9
 */
@Slf4j
@Service
public class OllamaServiceImpl implements OllamaService {
    
    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    
    @Value("${spring.ai.ollama.base-url:http://localhost:11434}")
    private String baseUrl;
    
    @Value("${spring.ai.ollama.chat.options.model:deepseek-r1:7b}")
    private String defaultModel;
    
    @Value("${spring.ai.ollama.chat.options.temperature:0.7}")
    private Double temperature;
    
    public OllamaServiceImpl(
            @Value("${spring.ai.ollama.base-url:http://localhost:11434}") String baseUrl) {
        this.baseUrl = baseUrl;
        this.objectMapper = new ObjectMapper();
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
    
    @Override
    public String chat(String prompt, String model) {
        log.info("调用Ollama API，模型: {}, 提示词长度: {}", model, prompt.length());
        
        try {
            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            requestBody.put("prompt", prompt);
            requestBody.put("stream", false);
            
            // 添加可选参数
            if (temperature != null) {
                Map<String, Object> options = new HashMap<>();
                options.put("temperature", temperature);
                requestBody.put("options", options);
            }
            
            // 调用Ollama API
            String response = webClient.post()
                    .uri("/api/generate")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(120)) // 2分钟超时
                    .retryWhen(Retry.fixedDelay(2, Duration.ofSeconds(2))
                            .filter(throwable -> {
                                log.warn("Ollama API调用失败，重试中...", throwable);
                                return true;
                            }))
                    .block();
            
            if (response == null || response.isEmpty()) {
                log.error("Ollama API返回空响应");
                return "抱歉，AI服务暂时无响应，请稍后重试。";
            }
            
            // 解析响应
            JsonNode jsonNode = objectMapper.readTree(response);
            String aiResponse = jsonNode.has("response") 
                    ? jsonNode.get("response").asText() 
                    : response;
            
            log.info("Ollama API调用成功，响应长度: {}", aiResponse.length());
            return aiResponse.trim();
            
        } catch (Exception e) {
            log.error("调用Ollama API失败", e);
            return "抱歉，AI服务调用失败: " + e.getMessage();
        }
    }
    
    @Override
    public String chat(String prompt) {
        return chat(prompt, defaultModel);
    }
}


