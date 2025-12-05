package com.agriculture.user.dto;

import lombok.Data;

/**
 * 用户注册响应DTO
 */
@Data
public class RegisterResponse {

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 角色
     */
    private String role;

    /**
     * 注册时间
     */
    private String createTime;

    /**
     * 注册成功消息
     */
    private String message;
}
