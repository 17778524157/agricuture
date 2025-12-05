package com.agriculture.user.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * ES同步消息DTO
 */
@Data
public class EsSyncMessage implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 操作类型：INSERT, UPDATE, DELETE
     */
    private String operationType;
    
    /**
     * 表名
     */
    private String tableName;
    
    /**
     * 数据库名
     */
    private String databaseName;
    
    /**
     * 数据ID
     */
    private String id;
    
    /**
     * 数据内容（INSERT/UPDATE时的完整数据）
     */
    private Map<String, Object> data;
    
    /**
     * 重试次数
     */
    private Integer retryCount = 0;
    
    /**
     * 最大重试次数
     */
    private static final int MAX_RETRY_COUNT = 5;
    
    /**
     * 是否超过最大重试次数
     */
    public boolean isMaxRetryReached() {
        return retryCount >= MAX_RETRY_COUNT;
    }
    
    /**
     * 增加重试次数
     */
    public void incrementRetry() {
        this.retryCount++;
    }
}












