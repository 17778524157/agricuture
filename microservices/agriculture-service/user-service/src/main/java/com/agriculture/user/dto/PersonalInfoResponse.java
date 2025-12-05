package com.agriculture.user.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 个人信息响应DTO
 * 用于封装和传输用户个人信息的响应数据
 */
@Data  // 使用Lombok的@Data注解，自动生成getter、setter、toString等方法
public class PersonalInfoResponse {
    
    private Integer id;          // 用户唯一标识ID
    
    private String username;     // 用户名
    
    private String nickname;     // 用户昵称
    
    private String email;        // 电子邮箱
    
    private String phone;        // 手机号码
    
    private String address;      // 地址信息
    
    private String avatarUrl;    // 头像图片链接
    
    private String role;         // 用户角色
    
    private String bio;          // 个人简介
    
    private String company;      // 所属公司
    
    private String position;     // 职位
    
    private String expertise;    // 专业领域
    
    private LocalDateTime createTime;     // 账户创建时间
    
    private LocalDateTime lastLoginTime;  // 最后登录时间
    
    private Integer loginCount;   // 登录次数
    
    private String status;       // 账户状态（如：激活、禁用等）
}
