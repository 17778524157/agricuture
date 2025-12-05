package com.agriculture.user.dto;

import com.agriculture.user.entity.Menu;
import lombok.Data;

import java.util.List;

/**
 * 登录响应DTO
 * 用于封装登录成功后返回给客户端的用户信息
 */
@Data  // 使用Lombok注解自动生成getter、setter、toString等方法
public class LoginResponse {
    
    private Integer id;      // 用户ID，数据库主键
    
    private String token;    // JWT令牌，用于后续请求的身份认证
    
    private String username; // 用户名，账号的唯一标识
    
    private String nickname; // 用户昵称，显示用的友好名称
    
    private String role;     // 用户角色，用于权限控制
    
    private String avatarUrl;   // 用户头像URL，用于显示用户头像

    private Integer points;     // 用户积分，用于积分商城

    private List<Menu> menus;  // 用户菜单列表，包含该用户可访问的菜单项
}
