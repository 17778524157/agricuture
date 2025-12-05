package com.agriculture.ai.mapper;

import com.agriculture.ai.entity.ChatMessage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 聊天消息Mapper
 */
@Mapper
public interface ChatMessageMapper extends BaseMapper<ChatMessage> {

    /**
     * 根据会话ID查询消息
     * @param sessionId 会话ID
     * @return 消息列表
     */
    @Select("SELECT * FROM chat_messages WHERE session_id = #{sessionId} ORDER BY create_time ASC")
    List<ChatMessage> findBySessionId(@Param("sessionId") String sessionId);

    /**
     * 查询用户的活跃会话
     * @param userId 用户ID
     * @return 活跃会话消息列表
     */
    @Select("SELECT * FROM chat_messages WHERE user_id = #{userId} AND create_time >= DATE_SUB(NOW(), INTERVAL 1 HOUR) ORDER BY create_time DESC LIMIT 1")
    List<ChatMessage> findActiveSessionByUserId(@Param("userId") Integer userId);
}
