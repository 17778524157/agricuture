package com.agriculture.gateway.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * 网关Redis登录服务
 * 简化版本，只包含网关需要的功能
 */
@Slf4j
@Service
public class GatewayRedisLoginService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    // Redis Key 常量
    private static final String LOGIN_USER_KEY = "login:user:";
    private static final String LOGIN_TOKEN_BLACKLIST_KEY = "login:token:blacklist:";

    /**
     * 检查Token是否在黑名单中
     */
    public boolean isTokenInBlacklist(String token) {
        String key = LOGIN_TOKEN_BLACKLIST_KEY + token;
        return stringRedisTemplate.hasKey(key);
    }

    /**
     * 获取用户会话
     */
    public String getUserSession(String token) {
        String key = LOGIN_USER_KEY + token;
        return stringRedisTemplate.opsForValue().get(key);
    }

    /**
     * 刷新用户会话过期时间
     */
    public void refreshUserSession(String token) {
        String key = LOGIN_USER_KEY + token;
        stringRedisTemplate.expire(key, java.time.Duration.ofHours(10));
    }
}
