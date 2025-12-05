package com.agriculture.ai.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 知识问答请求
 */
@Data
@Accessors(chain = true)
public class KnowledgeRequest {
    private String userId;
    private String question;
    private String category;
    private String subcategory;
}
