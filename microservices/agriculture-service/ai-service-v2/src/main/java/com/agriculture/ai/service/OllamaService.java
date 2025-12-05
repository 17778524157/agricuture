package com.agriculture.ai.service;

/**
 * Ollama AI服务接口
 * 用于与Ollama大模型进行交互
 */
public interface OllamaService {
    
    /**
     * 发送聊天消息
     * 
     * @param prompt 提示词
     * @param model 模型名称，如 "deepseek-r1:7b"
     * @return AI回复内容
     */
    String chat(String prompt, String model);
    
    /**
     * 发送聊天消息（使用默认模型）
     * 
     * @param prompt 提示词
     * @return AI回复内容
     */
    String chat(String prompt);
}


