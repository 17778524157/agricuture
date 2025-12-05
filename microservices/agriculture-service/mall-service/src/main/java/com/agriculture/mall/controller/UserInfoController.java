package com.agriculture.mall.controller;

import com.agriculture.common.result.Result;
import com.agriculture.mall.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户信息控制器
 */
@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
public class UserInfoController {

    @Autowired
    private UserMapper userMapper;

    /**
     * 获取个人信息
     */
    @GetMapping("/personalInfo")
    public Result<Map<String, Object>> getPersonalInfo(@RequestHeader("userId") Integer userId) {
        try {
            // 从数据库获取用户信息
            Integer points = userMapper.getPoints(userId);
            
            // 构建用户信息响应
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", userId);
            userInfo.put("username", "admin");
            userInfo.put("nickname", "小伍同学");
            userInfo.put("email", "admin@qq.com");
            userInfo.put("phone", "17712345678");
            userInfo.put("address", "四川省成都市");
            userInfo.put("bio", "有志者事竟成!!");
            userInfo.put("company", "农业科技公司");
            userInfo.put("position", "技术总监");
            userInfo.put("expertise", "农业信息化");
            userInfo.put("points", points != null ? points : 1000);
            userInfo.put("avatarUrl", "/api/file/avatar/default-avatar.jpg"); // 默认头像
            
            return Result.success(userInfo);
        } catch (Exception e) {
            return Result.error("获取个人信息失败: " + e.getMessage());
        }
    }

    /**
     * 更新个人信息
     */
    @PutMapping("/personalInfo")
    public Result<Map<String, Object>> updatePersonalInfo(
            @RequestHeader("userId") Integer userId,
            @RequestBody Map<String, Object> request) {
        try {
            // 这里可以添加更新用户信息的逻辑
            // 目前只是返回成功响应
            
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", userId);
            userInfo.put("username", "admin");
            userInfo.put("nickname", request.get("nickname"));
            userInfo.put("email", request.get("email"));
            userInfo.put("phone", request.get("phone"));
            userInfo.put("address", request.get("address"));
            userInfo.put("bio", request.get("bio"));
            userInfo.put("company", request.get("company"));
            userInfo.put("position", request.get("position"));
            userInfo.put("expertise", request.get("expertise"));
            userInfo.put("avatarUrl", request.get("avatarUrl"));
            
            // 获取当前积分
            Integer points = userMapper.getPoints(userId);
            userInfo.put("points", points != null ? points : 1000);
            
            return Result.success(userInfo);
        } catch (Exception e) {
            return Result.error("更新个人信息失败: " + e.getMessage());
        }
    }

    /**
     * 根据用户名获取用户信息
     */
    @GetMapping("/username/{username}")
    public Result<Map<String, Object>> getUserByUsername(@PathVariable("username") String username) {
        try {
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", 1);
            userInfo.put("username", username);
            userInfo.put("nickname", "小伍同学");
            userInfo.put("email", "admin@qq.com");
            userInfo.put("phone", "17712345678");
            userInfo.put("points", 1000);
            
            return Result.success(userInfo);
        } catch (Exception e) {
            return Result.error("获取用户信息失败: " + e.getMessage());
        }
    }
}
