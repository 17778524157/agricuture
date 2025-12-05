package com.agriculture.common.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.agriculture.common.utils.RedisConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis登录服务
 * 提供登录相关的Redis操作功能
 */
@Slf4j
@Service
public class RedisLoginService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 1. 用户会话管理
     */
    
    /**
     * 存储用户登录信息到Redis
     * @param token 用户token
     * @param userInfo 用户信息JSON字符串
     */
    public void saveUserSession(String token, String userInfo) {
        String key = RedisConstants.LOGIN_USER_KEY + token;
        stringRedisTemplate.opsForValue().set(key, userInfo, RedisConstants.LOGIN_USER_TTL, TimeUnit.SECONDS);
        log.info("用户会话已保存到Redis: {}", token);
    }

    /**
     * 获取用户登录信息
     * @param token 用户token
     * @return 用户信息JSON字符串
     */
    public String getUserSession(String token) {
        String key = RedisConstants.LOGIN_USER_KEY + token;
        return stringRedisTemplate.opsForValue().get(key);
    }

    /**
     * 删除用户会话
     * @param token 用户token
     */
    public void deleteUserSession(String token) {
        String key = RedisConstants.LOGIN_USER_KEY + token;
        stringRedisTemplate.delete(key);
        log.info("用户会话已从Redis删除: {}", token);
    }

    /**
     * 刷新用户会话过期时间
     * @param token 用户token
     */
    public void refreshUserSession(String token) {
        String key = RedisConstants.LOGIN_USER_KEY + token;
        stringRedisTemplate.expire(key, RedisConstants.LOGIN_USER_TTL, TimeUnit.SECONDS);
    }

    /**
     * 2. Token黑名单管理
     */
    
    /**
     * 将Token加入黑名单
     * @param token 要加入黑名单的token
     */
    public void addTokenToBlacklist(String token) {
        String key = RedisConstants.LOGIN_TOKEN_BLACKLIST_KEY + token;
        stringRedisTemplate.opsForValue().set(key, "1", RedisConstants.LOGIN_TOKEN_BLACKLIST_TTL, TimeUnit.SECONDS);
        log.info("Token已加入黑名单: {}", token);
    }

    /**
     * 检查Token是否在黑名单中
     * @param token 要检查的token
     * @return true-在黑名单中，false-不在黑名单中
     */
    public boolean isTokenInBlacklist(String token) {
        String key = RedisConstants.LOGIN_TOKEN_BLACKLIST_KEY + token;
        return StrUtil.isNotBlank(stringRedisTemplate.opsForValue().get(key));
    }

    /**
     * 3. 登录失败次数限制
     */
    
    /**
     * 增加登录失败次数
     * @param username 用户名或手机号
     * @return 当前失败次数
     */
    public int incrementLoginFailCount(String username) {
        String key = RedisConstants.LOGIN_FAIL_COUNT_KEY + username;
        Long count = stringRedisTemplate.opsForValue().increment(key);
        // 设置过期时间（只在第一次设置时生效）
        if (count == 1) {
            stringRedisTemplate.expire(key, RedisConstants.LOGIN_FAIL_COUNT_TTL, TimeUnit.MINUTES);
        }
        log.warn("用户 {} 登录失败，当前失败次数: {}", username, count);
        return count.intValue();
    }

    /**
     * 获取登录失败次数
     * @param username 用户名或手机号
     * @return 失败次数
     */
    public int getLoginFailCount(String username) {
        String key = RedisConstants.LOGIN_FAIL_COUNT_KEY + username;
        String count = stringRedisTemplate.opsForValue().get(key);
        return StrUtil.isBlank(count) ? 0 : Integer.parseInt(count);
    }

    /**
     * 清除登录失败次数
     * @param username 用户名或手机号
     */
    public void clearLoginFailCount(String username) {
        String key = RedisConstants.LOGIN_FAIL_COUNT_KEY + username;
        stringRedisTemplate.delete(key);
        log.info("用户 {} 登录成功，已清除失败次数", username);
    }

    /**
     * 检查是否达到最大失败次数
     * @param username 用户名或手机号
     * @return true-达到最大失败次数，false-未达到
     */
    public boolean isMaxFailCountReached(String username) {
        return getLoginFailCount(username) >= RedisConstants.MAX_LOGIN_FAIL_COUNT;
    }

    /**
     * 4. 账户锁定管理
     */
    
    /**
     * 锁定用户账户
     * @param username 用户名或手机号
     */
    public void lockUserAccount(String username) {
        String key = RedisConstants.LOGIN_LOCK_KEY + username;
        stringRedisTemplate.opsForValue().set(key, "1", RedisConstants.LOGIN_LOCK_TTL, TimeUnit.MINUTES);
        log.warn("用户账户已锁定: {}, 锁定时间: {} 分钟", username, RedisConstants.LOGIN_LOCK_TTL);
    }

    /**
     * 检查用户账户是否被锁定
     * @param username 用户名或手机号
     * @return true-已锁定，false-未锁定
     */
    public boolean isUserAccountLocked(String username) {
        String key = RedisConstants.LOGIN_LOCK_KEY + username;
        return StrUtil.isNotBlank(stringRedisTemplate.opsForValue().get(key));
    }

    /**
     * 解锁用户账户
     * @param username 用户名或手机号
     */
    public void unlockUserAccount(String username) {
        String key = RedisConstants.LOGIN_LOCK_KEY + username;
        stringRedisTemplate.delete(key);
        log.info("用户账户已解锁: {}", username);
    }

    /**
     * 5. 在线用户统计
     */
    
    /**
     * 用户上线
     * @param userId 用户ID
     * @param userInfo 用户信息
     */
    public void userOnline(String userId, String userInfo) {
        String key = RedisConstants.ONLINE_USER_KEY + userId;
        stringRedisTemplate.opsForValue().set(key, userInfo, RedisConstants.ONLINE_USER_TTL, TimeUnit.MINUTES);
        
        // 增加在线用户计数
        stringRedisTemplate.opsForValue().increment(RedisConstants.ONLINE_USER_COUNT_KEY);
        log.info("用户上线: {}", userId);
    }

    /**
     * 用户下线
     * @param userId 用户ID
     */
    public void userOffline(String userId) {
        String key = RedisConstants.ONLINE_USER_KEY + userId;
        Boolean deleted = stringRedisTemplate.delete(key);
        
        // 减少在线用户计数
        if (Boolean.TRUE.equals(deleted)) {
            stringRedisTemplate.opsForValue().decrement(RedisConstants.ONLINE_USER_COUNT_KEY);
            log.info("用户下线: {}", userId);
        }
    }

    /**
     * 刷新用户在线状态
     * @param userId 用户ID
     */
    public void refreshUserOnlineStatus(String userId) {
        String key = RedisConstants.ONLINE_USER_KEY + userId;
        stringRedisTemplate.expire(key, RedisConstants.ONLINE_USER_TTL, TimeUnit.MINUTES);
    }

    /**
     * 获取在线用户数量
     * @return 在线用户数量
     */
    public int getOnlineUserCount() {
        String count = stringRedisTemplate.opsForValue().get(RedisConstants.ONLINE_USER_COUNT_KEY);
        return StrUtil.isBlank(count) ? 0 : Integer.parseInt(count);
    }

    /**
     * 获取所有在线用户
     * @return 在线用户ID集合
     */
    public Set<String> getOnlineUsers() {
        String pattern = RedisConstants.ONLINE_USER_KEY + "*";
        return stringRedisTemplate.keys(pattern);
    }

    /**
     * 6. 用户信息缓存
     */
    
    /**
     * 缓存用户信息
     * @param userId 用户ID
     * @param userInfo 用户信息
     */
    public void cacheUserInfo(String userId, Object userInfo) {
        String key = RedisConstants.USER_INFO_KEY + userId;
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(userInfo), RedisConstants.USER_INFO_TTL, TimeUnit.MINUTES);
    }

    /**
     * 获取缓存的用户信息
     * @param userId 用户ID
     * @param clazz 用户信息类型
     * @return 用户信息对象
     */
    public <T> T getCachedUserInfo(String userId, Class<T> clazz) {
        String key = RedisConstants.USER_INFO_KEY + userId;
        String userInfoJson = stringRedisTemplate.opsForValue().get(key);
        if (StrUtil.isNotBlank(userInfoJson)) {
            return JSONUtil.toBean(userInfoJson, clazz);
        }
        return null;
    }

    /**
     * 删除缓存的用户信息
     * @param userId 用户ID
     */
    public void deleteCachedUserInfo(String userId) {
        String key = RedisConstants.USER_INFO_KEY + userId;
        stringRedisTemplate.delete(key);
    }

    /**
     * 7. 综合登录检查
     */
    
    /**
     * 检查用户是否可以登录
     * @param username 用户名或手机号
     * @return 检查结果信息
     */
    public String checkLoginPermission(String username) {
        // 检查账户是否被锁定
        if (isUserAccountLocked(username)) {
            return "账户已被锁定，请稍后再试";
        }
        
        // 检查是否达到最大失败次数
        if (isMaxFailCountReached(username)) {
            lockUserAccount(username);
            return "登录失败次数过多，账户已被锁定30分钟";
        }
        
        return null; // 可以登录
    }

    /**
     * 处理登录失败
     * @param username 用户名或手机号
     * @return 是否需要锁定账户
     */
    public boolean handleLoginFailure(String username) {
        int failCount = incrementLoginFailCount(username);
        if (failCount >= RedisConstants.MAX_LOGIN_FAIL_COUNT) {
            lockUserAccount(username);
            return true; // 已锁定账户
        }
        return false; // 未锁定账户
    }

    /**
     * 处理登录成功
     * @param username 用户名或手机号
     * @param token 用户token
     * @param userInfo 用户信息
     */
    public void handleLoginSuccess(String username, String token, Object userInfo) {
        // 清除失败次数
        clearLoginFailCount(username);
        
        // 保存用户会话
        saveUserSession(token, JSONUtil.toJsonStr(userInfo));
        
        // 用户上线
        userOnline(username, JSONUtil.toJsonStr(userInfo));
        
        log.info("用户 {} 登录成功", username);
    }

    /**
     * 处理用户登出
     * @param token 用户token
     * @param userId 用户ID
     */
    public void handleLogout(String token, String userId) {
        // 将token加入黑名单，防止token被重复使用
        addTokenToBlacklist(token);
        
        // 删除用户会话信息，确保会话完全终止
        deleteUserSession(token);
        
        // 用户下线
        userOffline(userId);
        
        log.info("用户 {} 已登出", userId);
    }
}
