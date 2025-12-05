package com.agriculture.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String username;

    private String password;

    private String nickname;

    private String email;

    private String phone;

    private String address;

    @TableField("avatar_url")
    private String avatarUrl;

    private String role;

    @TableField("create_time")
    private LocalDateTime createTime;
    
    @TableField("bio")
    private String bio;
    
    @TableField("company")
    private String company;
    
    @TableField("position")
    private String position;
    
    @TableField("expertise")
    private String expertise;
    
    @TableField("last_login_time")
    private LocalDateTime lastLoginTime;
    
    @TableField("login_count")
    private Integer loginCount;
    
    @TableField("status")
    private String status;
    
    @TableField("points")
    private Integer points;
}
