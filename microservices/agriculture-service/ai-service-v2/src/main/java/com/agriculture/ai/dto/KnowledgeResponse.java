package com.agriculture.ai.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 知识问答响应
 */
@Data
@Accessors(chain = true)
public class KnowledgeResponse {
    private String questionId;
    private String question;
    private String answer;
    private String category;
    private String subcategory;
    private List<String> relatedQuestions;
    private List<String> references;
    private LocalDateTime answerTime;
    private Long responseTime;
}
