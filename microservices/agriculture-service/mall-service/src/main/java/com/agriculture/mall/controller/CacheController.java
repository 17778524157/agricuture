package com.agriculture.mall.controller;

import com.agriculture.mall.service.CacheMonitorService;
import com.agriculture.mall.service.UserPointsCacheService;
import com.agriculture.mall.service.ProductCacheService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 缓存管理控制器
 */
@RestController
@RequestMapping("/api/mall/cache")
@Api(tags = "缓存管理")
public class CacheController {

    @Autowired
    private CacheMonitorService cacheMonitorService;
    
    @Autowired
    private UserPointsCacheService userPointsCacheService;
    
    @Autowired
    private ProductCacheService productCacheService;

    /**
     * 获取缓存统计信息
     */
    @GetMapping("/stats")
    @ApiOperation("获取缓存统计信息")
    public Map<String, Object> getCacheStats() {
        return cacheMonitorService.getCacheStats();
    }

    /**
     * 清除所有缓存
     */
    @PostMapping("/clear/all")
    @ApiOperation("清除所有缓存")
    public Map<String, Object> clearAllCache() {
        Map<String, Object> result = new HashMap<>();
        try {
            cacheMonitorService.clearAllCache();
            result.put("success", true);
            result.put("message", "清除所有缓存成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "清除缓存失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 清除用户积分缓存
     */
    @PostMapping("/clear/user-points")
    @ApiOperation("清除用户积分缓存")
    public Map<String, Object> clearUserPointsCache() {
        Map<String, Object> result = new HashMap<>();
        try {
            cacheMonitorService.clearUserPointsCache();
            result.put("success", true);
            result.put("message", "清除用户积分缓存成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "清除用户积分缓存失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 清除商品缓存
     */
    @PostMapping("/clear/products")
    @ApiOperation("清除商品缓存")
    public Map<String, Object> clearProductCache() {
        Map<String, Object> result = new HashMap<>();
        try {
            cacheMonitorService.clearProductCache();
            result.put("success", true);
            result.put("message", "清除商品缓存成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "清除商品缓存失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 预热用户积分缓存
     */
    @PostMapping("/warmup/user-points")
    @ApiOperation("预热用户积分缓存")
    public Map<String, Object> warmupUserPointsCache() {
        Map<String, Object> result = new HashMap<>();
        try {
            userPointsCacheService.warmupUserPointsCache();
            result.put("success", true);
            result.put("message", "预热用户积分缓存成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "预热用户积分缓存失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 预热商品缓存
     */
    @PostMapping("/warmup/products")
    @ApiOperation("预热商品缓存")
    public Map<String, Object> warmupProductCache() {
        Map<String, Object> result = new HashMap<>();
        try {
            productCacheService.warmupProductCache();
            result.put("success", true);
            result.put("message", "预热商品缓存成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "预热商品缓存失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 删除指定用户积分缓存
     */
    @DeleteMapping("/user-points/{userId}")
    @ApiOperation("删除指定用户积分缓存")
    public Map<String, Object> evictUserPointsCache(@PathVariable Integer userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            userPointsCacheService.evictUserPointsCache(userId);
            result.put("success", true);
            result.put("message", "删除用户积分缓存成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "删除用户积分缓存失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 删除指定商品缓存
     */
    @DeleteMapping("/products/{productId}")
    @ApiOperation("删除指定商品缓存")
    public Map<String, Object> evictProductCache(@PathVariable Integer productId) {
        Map<String, Object> result = new HashMap<>();
        try {
            productCacheService.evictProductCache(productId);
            result.put("success", true);
            result.put("message", "删除商品缓存成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "删除商品缓存失败: " + e.getMessage());
        }
        return result;
    }
}
