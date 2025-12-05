package com.agriculture.user.entity.es;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * ES用户文档实体
 */
@Data
public class UserDocument {
    
    private Long id;
    
    private String username;
    
    private String nickname;
    
    private String phone;
    
    private String email;
    
    private Integer status;
    
    private Integer points;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}

