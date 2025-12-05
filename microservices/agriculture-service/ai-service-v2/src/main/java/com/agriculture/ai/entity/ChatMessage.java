package com.agriculture.ai.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 聊天消息实体
 */
@Data
@Accessors(chain = true)
@TableName("chat_messages")
public class ChatMessage {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String sessionId;
    
    private String userId;
    
    private String username;
    
    private String message;
    
    private Boolean isUser; // true: 用户消息, false: AI回复
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
}
