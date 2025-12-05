package com.agriculture.mall.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 缓存监控服务
 */
@Slf4j
@Service
public class CacheMonitorService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 获取缓存统计信息
     */
    public Map<String, Object> getCacheStats() {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            // 缓存命中率统计
            Long hitCount = (Long) redisTemplate.opsForValue().get("mall:cache:hit:count");
            Long missCount = (Long) redisTemplate.opsForValue().get("mall:cache:miss:count");
            
            if (hitCount != null && missCount != null) {
                double hitRate = (double) hitCount / (hitCount + missCount);
                stats.put("hitRate", hitRate);
                stats.put("hitCount", hitCount);
                stats.put("missCount", missCount);
            }
            
            // 缓存大小统计
            Set<String> userPointsKeys = redisTemplate.keys("mall:user:points:*");
            Set<String> productKeys = redisTemplate.keys("mall:product:*");
            
            stats.put("userPointsCacheSize", userPointsKeys != null ? userPointsKeys.size() : 0);
            stats.put("productCacheSize", productKeys != null ? productKeys.size() : 0);
            
            // Redis内存使用情况
            Map<String, Object> info = new HashMap<>();
            try {
                Object usedMemory = redisTemplate.getConnectionFactory()
                    .getConnection().info("memory").get("used_memory_human");
                info.put("usedMemory", usedMemory);
            } catch (Exception e) {
                info.put("usedMemory", "获取失败");
            }
            stats.putAll(info);
            
            log.info("缓存统计信息: {}", stats);
            
        } catch (Exception e) {
            log.error("获取缓存统计信息失败", e);
            stats.put("error", e.getMessage());
        }
        
        return stats;
    }

    /**
     * 清除所有缓存
     */
    public void clearAllCache() {
        try {
            Set<String> mallKeys = redisTemplate.keys("mall:*");
            if (mallKeys != null && !mallKeys.isEmpty()) {
                redisTemplate.delete(mallKeys);
                log.info("清除所有缓存成功，数量: {}", mallKeys.size());
            }
        } catch (Exception e) {
            log.error("清除所有缓存失败", e);
        }
    }

    /**
     * 清除用户积分缓存
     */
    public void clearUserPointsCache() {
        try {
            Set<String> userPointsKeys = redisTemplate.keys("mall:user:points:*");
            if (userPointsKeys != null && !userPointsKeys.isEmpty()) {
                redisTemplate.delete(userPointsKeys);
                log.info("清除用户积分缓存成功，数量: {}", userPointsKeys.size());
            }
        } catch (Exception e) {
            log.error("清除用户积分缓存失败", e);
        }
    }

    /**
     * 清除商品缓存
     */
    public void clearProductCache() {
        try {
            Set<String> productKeys = redisTemplate.keys("mall:product:*");
            if (productKeys != null && !productKeys.isEmpty()) {
                redisTemplate.delete(productKeys);
                log.info("清除商品缓存成功，数量: {}", productKeys.size());
            }
        } catch (Exception e) {
            log.error("清除商品缓存失败", e);
        }
    }
}
