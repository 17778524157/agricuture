package com.agriculture.ai.service.impl;

import com.agriculture.ai.entity.ChatMessage;
import com.agriculture.ai.mapper.ChatMessageMapper;
import com.agriculture.ai.service.AiChatSessionService;
import com.agriculture.common.lock.DistributedLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * AI聊天会话服务实现类
 * 使用分布式锁控制AI对话的并发访问
 */
@Slf4j
@Service
public class AiChatSessionServiceImpl implements AiChatSessionService {

    @Autowired
    private ChatMessageMapper chatMessageMapper;

    @Autowired
    private DistributedLock distributedLock;

    /**
     * 创建聊天会话 - 使用分布式锁防止重复创建
     */
    @Override
    @Transactional
    public String createChatSession(Integer userId) {
        String lockKey = "ai:chat:session:create:lock:" + userId;
        
        return distributedLock.executeWithLock(lockKey, () -> {
            log.info("开始创建AI聊天会话，用户ID: {}", userId);
            
            // 1. 检查用户是否有活跃会话
            List<ChatMessage> activeMessages = chatMessageMapper.findActiveSessionByUserId(userId);
            if (!activeMessages.isEmpty()) {
                String existingSessionId = activeMessages.get(0).getSessionId();
                log.info("用户已有活跃会话，会话ID: {}", existingSessionId);
                return existingSessionId;
            }
            
            // 2. 创建新会话
            String sessionId = "SESSION_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8);
            
            // 3. 创建欢迎消息
            ChatMessage welcomeMessage = new ChatMessage();
            welcomeMessage.setSessionId(sessionId);
            welcomeMessage.setUserId(String.valueOf(userId));
            welcomeMessage.setUsername("系统");
            welcomeMessage.setMessage("您好！我是农业AI专家，有什么农业问题可以为您解答？");
            welcomeMessage.setIsUser(false);
            welcomeMessage.setCreateTime(LocalDateTime.now());
            
            chatMessageMapper.insert(welcomeMessage);
            
            log.info("AI聊天会话创建成功，会话ID: {}, 用户ID: {}", sessionId, userId);
            return sessionId;
        });
    }

    /**
     * 发送消息 - 使用分布式锁防止同一会话并发处理
     */
    @Override
    @Transactional
    public ChatMessage sendMessage(String sessionId, Integer userId, String message) {
        String lockKey = "ai:chat:message:lock:" + sessionId;
        
        return distributedLock.executeWithLock(lockKey, () -> {
            log.info("开始处理AI聊天消息，会话ID: {}, 用户ID: {}, 消息: {}", sessionId, userId, message);
            
            // 1. 保存用户消息
            ChatMessage userMessage = new ChatMessage();
            userMessage.setSessionId(sessionId);
            userMessage.setUserId(String.valueOf(userId));
            userMessage.setUsername("用户");
            userMessage.setMessage(message);
            userMessage.setIsUser(true);
            userMessage.setCreateTime(LocalDateTime.now());
            
            chatMessageMapper.insert(userMessage);
            
            // 2. 模拟AI处理（实际项目中这里会调用AI模型）
            String aiResponse = generateAiResponse(message);
            
            // 3. 保存AI回复
            ChatMessage aiMessage = new ChatMessage();
            aiMessage.setSessionId(sessionId);
            aiMessage.setUserId(String.valueOf(userId));
            aiMessage.setUsername("农业AI专家");
            aiMessage.setMessage(aiResponse);
            aiMessage.setIsUser(false);
            aiMessage.setCreateTime(LocalDateTime.now());
            
            chatMessageMapper.insert(aiMessage);
            
            log.info("AI聊天消息处理完成，会话ID: {}", sessionId);
            return aiMessage;
        });
    }

    /**
     * 获取会话消息历史
     */
    @Override
    public List<ChatMessage> getSessionMessages(String sessionId) {
        return chatMessageMapper.findBySessionId(sessionId);
    }

    /**
     * 结束聊天会话
     */
    @Override
    @Transactional
    public boolean endChatSession(String sessionId, Integer userId) {
        String lockKey = "ai:chat:session:end:lock:" + sessionId;
        
        return distributedLock.executeWithLock(lockKey, () -> {
            log.info("开始结束AI聊天会话，会话ID: {}, 用户ID: {}", sessionId, userId);
            
            // 1. 验证会话归属
            List<ChatMessage> messages = chatMessageMapper.findBySessionId(sessionId);
            if (messages.isEmpty()) {
                throw new RuntimeException("会话不存在");
            }
            
            if (!messages.get(0).getUserId().equals(userId)) {
                throw new RuntimeException("无权限操作此会话");
            }
            
            // 2. 创建结束消息
            ChatMessage endMessage = new ChatMessage();
            endMessage.setSessionId(sessionId);
            endMessage.setUserId(String.valueOf(userId));
            endMessage.setUsername("系统");
            endMessage.setMessage("会话已结束，感谢您的使用！");
            endMessage.setIsUser(false);
            endMessage.setCreateTime(LocalDateTime.now());
            
            chatMessageMapper.insert(endMessage);
            
            log.info("AI聊天会话结束成功，会话ID: {}", sessionId);
            return true;
        });
    }

    /**
     * 生成AI回复（模拟）
     */
    private String generateAiResponse(String userMessage) {
        // 这里应该调用实际的AI模型
        // 为了演示，返回一个简单的回复
        return "感谢您的提问：" + userMessage + "。作为农业AI专家，我建议您关注土壤质量、作物生长环境等因素。";
    }
}
