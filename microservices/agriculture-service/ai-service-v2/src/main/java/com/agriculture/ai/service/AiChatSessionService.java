package com.agriculture.ai.service;

import com.agriculture.ai.entity.ChatMessage;

import java.util.List;

/**
 * AI聊天会话服务接口
 */
public interface AiChatSessionService {

    /**
     * 创建聊天会话
     * @param userId 用户ID
     * @return 会话ID
     */
    String createChatSession(Integer userId);

    /**
     * 发送消息
     * @param sessionId 会话ID
     * @param userId 用户ID
     * @param message 消息内容
     * @return AI回复消息
     */
    ChatMessage sendMessage(String sessionId, Integer userId, String message);

    /**
     * 获取会话消息历史
     * @param sessionId 会话ID
     * @return 消息列表
     */
    List<ChatMessage> getSessionMessages(String sessionId);

    /**
     * 结束聊天会话
     * @param sessionId 会话ID
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean endChatSession(String sessionId, Integer userId);
}
