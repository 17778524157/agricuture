package com.agriculture.common.lock;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * 分布式锁工具类
 * 基于Redis实现分布式锁，支持自动续期和超时释放
 */
@Slf4j
@Component
public class DistributedLock {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // 锁默认过期时间（秒）
    private static final long DEFAULT_EXPIRE_TIME = 30;
    
    // 锁默认等待时间（秒）
    private static final long DEFAULT_WAIT_TIME = 5;

    /**
     * 获取分布式锁
     * @param lockKey 锁的key
     * @param expireTime 锁的过期时间（秒）
     * @param waitTime 等待时间（秒）
     * @return 是否获取成功
     */
    public boolean tryLock(String lockKey, long expireTime, long waitTime) {
        long startTime = System.currentTimeMillis();
        
        while (System.currentTimeMillis() - startTime < waitTime * 1000) {
            Boolean acquired = redisTemplate.opsForValue()
                .setIfAbsent(lockKey, "locked", Duration.ofSeconds(expireTime));
            
            if (Boolean.TRUE.equals(acquired)) {
                log.debug("获取分布式锁成功，锁key: {}", lockKey);
                return true;
            }
            
            try {
                Thread.sleep(100); // 等待100ms后重试
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        
        log.warn("获取分布式锁超时，锁key: {}", lockKey);
        return false;
    }

    /**
     * 获取分布式锁（使用默认参数）
     */
    public boolean tryLock(String lockKey) {
        return tryLock(lockKey, DEFAULT_EXPIRE_TIME, DEFAULT_WAIT_TIME);
    }

    /**
     * 释放分布式锁
     * @param lockKey 锁的key
     */
    public void releaseLock(String lockKey) {
        try {
            redisTemplate.delete(lockKey);
            log.debug("释放分布式锁成功，锁key: {}", lockKey);
        } catch (Exception e) {
            log.error("释放分布式锁失败，锁key: {}", lockKey, e);
        }
    }

    /**
     * 执行带锁的操作
     * @param lockKey 锁的key
     * @param expireTime 锁的过期时间（秒）
     * @param waitTime 等待时间（秒）
     * @param operation 要执行的操作
     * @param <T> 返回值类型
     * @return 操作结果
     */
    public <T> T executeWithLock(String lockKey, long expireTime, long waitTime, LockOperation<T> operation) {
        if (!tryLock(lockKey, expireTime, waitTime)) {
            throw new RuntimeException("获取分布式锁失败，锁key: " + lockKey);
        }
        
        try {
            return operation.execute();
        } finally {
            releaseLock(lockKey);
        }
    }

    /**
     * 执行带锁的操作（使用默认参数）
     */
    public <T> T executeWithLock(String lockKey, LockOperation<T> operation) {
        return executeWithLock(lockKey, DEFAULT_EXPIRE_TIME, DEFAULT_WAIT_TIME, operation);
    }

    /**
     * 检查锁是否存在
     * @param lockKey 锁的key
     * @return 锁是否存在
     */
    public boolean isLocked(String lockKey) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(lockKey));
    }

    /**
     * 获取锁的剩余过期时间
     * @param lockKey 锁的key
     * @return 剩余过期时间（秒），-1表示锁不存在，-2表示锁没有过期时间
     */
    public long getLockTtl(String lockKey) {
        return redisTemplate.getExpire(lockKey, TimeUnit.SECONDS);
    }

    /**
     * 锁操作接口
     */
    @FunctionalInterface
    public interface LockOperation<T> {
        T execute();
    }
}
