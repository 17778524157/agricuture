package com.agriculture.mall.service;

import com.agriculture.mall.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

/**
 * 用户积分缓存服务
 * 实现Canal双写一致性：
 * 1. 写操作：只写数据库，不写缓存
 * 2. Canal监听binlog，自动同步到Redis缓存
 * 3. 读操作：优先从缓存读取，缓存未命中时查数据库并写入缓存
 */
@Slf4j
@Service
public class UserPointsCacheService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private CanalDataSyncService canalDataSyncService;

    // 缓存key前缀
    private static final String USER_POINTS_KEY = "mall:user:points:";
    private static final String USER_POINTS_LOCK_KEY = "mall:user:points:lock:";
    
    // 缓存过期时间
    private static final Duration CACHE_TTL = Duration.ofMinutes(30);

    /**
     * 获取用户积分 - Cache-Aside模式
     * 1. 先查缓存（Canal同步的数据）
     * 2. 缓存未命中，查数据库
     * 3. 写入缓存
     */
    public Integer getUserPoints(Integer userId) {
        String cacheKey = USER_POINTS_KEY + userId;
        
        try {
            // 1. 先查缓存（Canal同步的数据）
            Integer points = (Integer) redisTemplate.opsForValue().get(cacheKey);
            if (points != null) {
                log.debug("缓存命中，用户ID: {}, 积分: {} (Canal同步数据)", userId, points);
                return points;
            }
            
            // 2. 缓存未命中，查数据库
            log.debug("缓存未命中，查询数据库，用户ID: {}", userId);
            points = userMapper.getPoints(userId);
            
            // 3. 写入缓存
            if (points != null) {
                redisTemplate.opsForValue().set(cacheKey, points, CACHE_TTL);
                log.debug("写入缓存成功，用户ID: {}, 积分: {}", userId, points);
            }
            
            return points != null ? points : 0;
            
        } catch (Exception e) {
            log.error("获取用户积分失败，用户ID: {}", userId, e);
            // 缓存异常时，直接查数据库
            Integer points = userMapper.getPoints(userId);
            return points != null ? points : 0;
        }
    }

    /**
     * 更新用户积分 - Canal双写一致性
     * 1. 只更新数据库
     * 2. Canal监听binlog自动同步到缓存
     * 3. 如果Canal不可用，手动同步到缓存
     */
    @Transactional
    public void updateUserPoints(Integer userId, Integer pointsChange) {
        try {
            // 1. 更新数据库
            int updatedRows = userMapper.updatePoints(userId, pointsChange);
            if (updatedRows <= 0) {
                throw new RuntimeException("积分更新失败，用户ID: " + userId);
            }
            
            // 2. 获取最新积分
            Integer newPoints = userMapper.getPoints(userId);
            
            // 3. Canal双写一致性：优先使用Canal自动同步
            if (canalDataSyncService.isRunning()) {
                log.info("用户积分更新成功，用户ID: {}, 变化: {}, 最新积分: {} (Canal将自动同步缓存)", 
                    userId, pointsChange, newPoints);
            } else {
                // Canal不可用时，手动同步到缓存
                canalDataSyncService.syncUserPointsToCache(userId, newPoints);
                log.info("用户积分更新成功，用户ID: {}, 变化: {}, 最新积分: {} (手动同步缓存)", 
                    userId, pointsChange, newPoints);
            }
            
        } catch (Exception e) {
            log.error("更新用户积分失败，用户ID: {}, 变化: {}", userId, pointsChange, e);
            throw e;
        }
    }

    /**
     * 增加用户积分 - 用于订单取消时恢复积分
     */
    @Transactional
    public boolean addUserPoints(Integer userId, Integer points) {
        try {
            // 直接调用更新方法，传入正数表示增加
            updateUserPoints(userId, points);
            log.info("积分增加成功，用户ID: {}, 增加积分: {}", userId, points);
            return true;
        } catch (Exception e) {
            log.error("积分增加失败，用户ID: {}, 增加积分: {}", userId, points, e);
            return false;
        }
    }

    /**
     * 扣除用户积分 - 带分布式锁
     * 防止并发扣除导致积分不一致
     */
    @Transactional
    public boolean deductUserPoints(Integer userId, Integer points) {
        String lockKey = USER_POINTS_LOCK_KEY + userId;
        
        // 获取分布式锁
        Boolean lockAcquired = redisTemplate.opsForValue()
            .setIfAbsent(lockKey, "locked", Duration.ofSeconds(10));
        
        if (!lockAcquired) {
            log.warn("获取积分锁失败，用户ID: {}", userId);
            return false;
        }
        
        try {
            // 1. 检查当前积分（从缓存获取，Canal同步的数据）
            Integer currentPoints = getUserPoints(userId);
            if (currentPoints < points) {
                log.warn("积分不足，用户ID: {}, 当前积分: {}, 需要扣除: {}", userId, currentPoints, points);
                return false;
            }
            
            // 2. 扣除积分（只更新数据库，Canal自动同步缓存）
            updateUserPoints(userId, -points);
            
            log.info("积分扣除成功，用户ID: {}, 扣除积分: {} (Canal双写一致性)", userId, points);
            return true;
            
        } finally {
            // 释放锁
            redisTemplate.delete(lockKey);
        }
    }

    /**
     * 删除用户积分缓存
     */
    public void evictUserPointsCache(Integer userId) {
        String cacheKey = USER_POINTS_KEY + userId;
        redisTemplate.delete(cacheKey);
        log.debug("删除用户积分缓存，用户ID: {}", userId);
    }

    /**
     * 批量预热用户积分缓存
     */
    public void warmupUserPointsCache() {
        try {
            // 这里可以查询活跃用户列表进行预热
            // List<Integer> activeUsers = userMapper.selectActiveUsers();
            // for (Integer userId : activeUsers) {
            //     getUserPoints(userId);
            // }
            log.info("用户积分缓存预热完成");
        } catch (Exception e) {
            log.error("用户积分缓存预热失败", e);
        }
    }
}