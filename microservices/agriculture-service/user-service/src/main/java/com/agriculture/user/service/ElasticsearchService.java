package com.agriculture.user.service;

import com.agriculture.user.entity.es.UserDocument;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Elasticsearch服务
 */
@Slf4j
@Service
public class ElasticsearchService {
    
    @Autowired
    private RestHighLevelClient restHighLevelClient;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private static final String INDEX_NAME = "user_index";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /**
     * 同步用户数据到ES（插入或更新）
     */
    public void syncUserToEs(Map<String, Object> data) {
        try {
            String id = String.valueOf(data.get("id"));
            UserDocument userDocument = convertToUserDocument(data);
            
            IndexRequest indexRequest = new IndexRequest(INDEX_NAME)
                    .id(id)
                    .source(objectMapper.writeValueAsString(userDocument), XContentType.JSON);
            
            restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
            log.info("同步用户数据到ES成功: userId={}", id);
            
        } catch (Exception e) {
            log.error("同步用户数据到ES失败", e);
            throw new RuntimeException("同步用户数据到ES失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 更新用户数据到ES
     */
    public void updateUserToEs(Map<String, Object> data) {
        try {
            String id = String.valueOf(data.get("id"));
            UserDocument userDocument = convertToUserDocument(data);
            
            UpdateRequest updateRequest = new UpdateRequest(INDEX_NAME, id)
                    .doc(objectMapper.writeValueAsString(userDocument), XContentType.JSON)
                    .upsert(objectMapper.writeValueAsString(userDocument), XContentType.JSON);
            
            restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
            log.info("更新用户数据到ES成功: userId={}", id);
            
        } catch (Exception e) {
            log.error("更新用户数据到ES失败", e);
            throw new RuntimeException("更新用户数据到ES失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 从ES删除用户数据
     */
    public void deleteUserFromEs(String userId) {
        try {
            DeleteRequest deleteRequest = new DeleteRequest(INDEX_NAME, userId);
            restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
            log.info("从ES删除用户数据成功: userId={}", userId);
            
        } catch (Exception e) {
            log.error("从ES删除用户数据失败: userId={}", userId, e);
            throw new RuntimeException("从ES删除用户数据失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 将Map转换为UserDocument
     */
    private UserDocument convertToUserDocument(Map<String, Object> data) {
        UserDocument doc = new UserDocument();
        doc.setId(getLongValue(data, "id"));
        doc.setUsername(getStringValue(data, "username"));
        doc.setNickname(getStringValue(data, "nickname"));
        doc.setPhone(getStringValue(data, "phone"));
        doc.setEmail(getStringValue(data, "email"));
        doc.setStatus(getIntegerValue(data, "status"));
        doc.setPoints(getIntegerValue(data, "points"));
        doc.setCreateTime(parseDateTime(getStringValue(data, "create_time")));
        doc.setUpdateTime(parseDateTime(getStringValue(data, "update_time")));
        return doc;
    }
    
    private String getStringValue(Map<String, Object> data, String key) {
        Object value = data.get(key);
        return value != null ? value.toString() : null;
    }
    
    private Long getLongValue(Map<String, Object> data, String key) {
        Object value = data.get(key);
        if (value == null) return null;
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return Long.parseLong(value.toString());
    }
    
    private Integer getIntegerValue(Map<String, Object> data, String key) {
        Object value = data.get(key);
        if (value == null) return null;
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return Integer.parseInt(value.toString());
    }
    
    private LocalDateTime parseDateTime(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }
        try {
            return LocalDateTime.parse(dateStr, DATE_FORMATTER);
        } catch (Exception e) {
            log.warn("解析日期时间失败: {}", dateStr, e);
            return null;
        }
    }
}

