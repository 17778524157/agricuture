package com.agriculture.mall.controller;

import com.agriculture.common.result.Result;
import com.agriculture.mall.mapper.UserMapper;
import com.agriculture.mall.service.CanalDataSyncService;
import com.agriculture.mall.service.UserPointsCacheService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 积分缓存管理控制器
 * 用于诊断和修复积分显示问题
 */
@Slf4j
@RestController
@RequestMapping("/api/mall/cache")
@Api(tags = "积分缓存管理")
public class CacheManagementController {

    @Autowired
    private UserPointsCacheService userPointsCacheService;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
    private CanalDataSyncService canalDataSyncService;

    /**
     * 获取用户积分详情（数据库 + 缓存）
     */
    @GetMapping("/points/detail/{userId}")
    @ApiOperation("获取用户积分详情")
    public Result<Map<String, Object>> getUserPointsDetail(@PathVariable Integer userId) {
        try {
            Map<String, Object> result = new HashMap<>();
            
            // 1. 从数据库获取积分
            Integer dbPoints = userMapper.getPoints(userId);
            result.put("dbPoints", dbPoints);
            
            // 2. 从缓存获取积分
            String cacheKey = "mall:user:points:" + userId;
            Integer cachePoints = (Integer) redisTemplate.opsForValue().get(cacheKey);
            result.put("cachePoints", cachePoints);
            
            // 3. 检查数据一致性
            boolean isConsistent = (dbPoints != null && cachePoints != null && dbPoints.equals(cachePoints)) ||
                                 (dbPoints == null && cachePoints == null);
            result.put("isConsistent", isConsistent);
            
            // 4. Canal服务状态
            result.put("canalRunning", canalDataSyncService.isRunning());
            
            // 5. 缓存键信息
            result.put("cacheKey", cacheKey);
            result.put("cacheExists", redisTemplate.hasKey(cacheKey));
            
            log.info("用户积分详情查询: userId={}, dbPoints={}, cachePoints={}, isConsistent={}", 
                userId, dbPoints, cachePoints, isConsistent);
            
            return Result.success(result);
            
        } catch (Exception e) {
            log.error("获取用户积分详情失败", e);
            return Result.error("获取用户积分详情失败：" + e.getMessage());
        }
    }

    /**
     * 强制刷新用户积分缓存
     */
    @PostMapping("/points/refresh/{userId}")
    @ApiOperation("强制刷新用户积分缓存")
    public Result<String> refreshUserPointsCache(@PathVariable Integer userId) {
        try {
            // 1. 删除现有缓存
            userPointsCacheService.evictUserPointsCache(userId);
            
            // 2. 从数据库重新获取积分
            Integer dbPoints = userMapper.getPoints(userId);
            
            // 3. 手动同步到缓存
            if (dbPoints != null) {
                canalDataSyncService.syncUserPointsToCache(userId, dbPoints);
                log.info("强制刷新用户积分缓存成功: userId={}, points={}", userId, dbPoints);
                return Result.success("缓存刷新成功，积分：" + dbPoints);
            } else {
                return Result.error("用户不存在或积分为空");
            }
            
        } catch (Exception e) {
            log.error("强制刷新用户积分缓存失败", e);
            return Result.error("缓存刷新失败：" + e.getMessage());
        }
    }

    /**
     * 清除所有积分缓存
     */
    @PostMapping("/points/clear")
    @ApiOperation("清除所有积分缓存")
    public Result<String> clearAllPointsCache() {
        try {
            // 删除所有积分相关的缓存
            redisTemplate.delete(redisTemplate.keys("mall:user:points:*"));
            log.info("清除所有积分缓存成功");
            return Result.success("所有积分缓存已清除");
            
        } catch (Exception e) {
            log.error("清除所有积分缓存失败", e);
            return Result.error("清除缓存失败：" + e.getMessage());
        }
    }

    /**
     * 手动同步用户积分到缓存
     */
    @PostMapping("/points/sync/{userId}")
    @ApiOperation("手动同步用户积分到缓存")
    public Result<String> syncUserPointsToCache(@PathVariable Integer userId) {
        try {
            // 1. 从数据库获取积分
            Integer dbPoints = userMapper.getPoints(userId);
            
            if (dbPoints == null) {
                return Result.error("用户不存在或积分为空");
            }
            
            // 2. 手动同步到缓存
            canalDataSyncService.syncUserPointsToCache(userId, dbPoints);
            
            log.info("手动同步用户积分到缓存成功: userId={}, points={}", userId, dbPoints);
            return Result.success("同步成功，积分：" + dbPoints);
            
        } catch (Exception e) {
            log.error("手动同步用户积分到缓存失败", e);
            return Result.error("同步失败：" + e.getMessage());
        }
    }

    /**
     * 获取Canal服务状态
     */
    @GetMapping("/canal/status")
    @ApiOperation("获取Canal服务状态")
    public Result<Map<String, Object>> getCanalStatus() {
        try {
            Map<String, Object> result = new HashMap<>();
            result.put("isRunning", canalDataSyncService.isRunning());
            result.put("canalHost", "192.168.10.128");
            result.put("canalPort", 11111);
            result.put("destination", "example");
            
            return Result.success(result);
            
        } catch (Exception e) {
            log.error("获取Canal服务状态失败", e);
            return Result.error("获取Canal状态失败：" + e.getMessage());
        }
    }

    /**
     * 重启Canal同步服务
     */
    @PostMapping("/canal/restart")
    @ApiOperation("重启Canal同步服务")
    public Result<String> restartCanalSync() {
        try {
            // 停止现有服务
            canalDataSyncService.stopCanalSync();
            
            // 等待一秒
            Thread.sleep(1000);
            
            // 重新启动
            canalDataSyncService.startCanalSync();
            
            log.info("Canal同步服务重启成功");
            return Result.success("Canal同步服务已重启");
            
        } catch (Exception e) {
            log.error("重启Canal同步服务失败", e);
            return Result.error("重启Canal服务失败：" + e.getMessage());
        }
    }

    /**
     * 获取Redis缓存信息
     */
    @GetMapping("/redis/info")
    @ApiOperation("获取Redis缓存信息")
    public Result<Map<String, Object>> getRedisInfo() {
        try {
            Map<String, Object> result = new HashMap<>();
            
            // 获取积分缓存数量
            Long pointsCacheCount = redisTemplate.countExistingKeys(redisTemplate.keys("mall:user:points:*"));
            result.put("pointsCacheCount", pointsCacheCount);
            
            // 获取商品缓存数量
            Long productCacheCount = redisTemplate.countExistingKeys(redisTemplate.keys("mall:product:*"));
            result.put("productCacheCount", productCacheCount);
            
            // 获取总缓存数量
            Long totalCacheCount = redisTemplate.countExistingKeys(redisTemplate.keys("mall:*"));
            result.put("totalCacheCount", totalCacheCount);
            
            return Result.success(result);
            
        } catch (Exception e) {
            log.error("获取Redis缓存信息失败", e);
            return Result.error("获取Redis信息失败：" + e.getMessage());
        }
    }
}
