package com.agriculture.mall.controller;

import com.agriculture.mall.entity.Exchange;
import com.agriculture.mall.entity.Product;
import com.agriculture.mall.mapper.UserMapper;
import com.agriculture.mall.service.ExchangeService;
import com.agriculture.mall.service.ProductService;
import com.agriculture.mall.service.PointsRecordService;
import com.agriculture.mall.service.UserPointsCacheService;
import com.agriculture.mall.service.ProductCacheService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 兑换记录控制器
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/mall/exchange")
@Api(tags = "兑换记录管理")
public class ExchangeController {

    private static final Logger logger = LoggerFactory.getLogger(ExchangeController.class);

    @Autowired
    private ExchangeService exchangeService;
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private PointsRecordService pointsRecordService;
    
    @Autowired
    private UserPointsCacheService userPointsCacheService;
    
    @Autowired
    private ProductCacheService productCacheService;

    @GetMapping("/page")
    @ApiOperation("分页查询兑换记录")
    public Map<String, Object> getExchangePage(
            @RequestParam(required = false, defaultValue = "1") Integer pageNum,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long userId) {
        
        logger.info("分页查询兑换记录 - pageNum: {}, pageSize: {}, userId: {}", 
                   pageNum, pageSize, userId);
        
        Page<Exchange> page = exchangeService.getExchangePage(pageNum, pageSize, userId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("records", page.getRecords());
        result.put("total", page.getTotal());
        result.put("current", page.getCurrent());
        result.put("size", page.getSize());
        result.put("pages", page.getPages());
        
        return result;
    }

    @GetMapping("/user/{userId}")
    @ApiOperation("获取用户兑换记录")
    public List<Exchange> getUserExchanges(@PathVariable Long userId) {
        return exchangeService.getUserExchanges(userId);
    }

    @GetMapping("/all")
    @ApiOperation("获取所有兑换记录")
    public List<Exchange> getAllExchanges() {
        return exchangeService.getAllExchanges();
    }

    @GetMapping("/{id}")
    @ApiOperation("根据ID获取兑换记录")
    public Exchange getExchangeById(@PathVariable Long id) {
        return exchangeService.getById(id);
    }

    @PostMapping
    @ApiOperation("创建兑换记录")
    public Map<String, Object> createExchange(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        try {
            Long userId = Long.valueOf(params.get("userId").toString());
            String userName = params.get("userName").toString();
            Long productId = Long.valueOf(params.get("productId").toString());
            
            // 获取商品信息 - 使用缓存
            Product product = productService.getById(productId);
            if (product == null) {
                result.put("success", false);
                result.put("message", "商品不存在");
                return result;
            }
            
            // 检查库存 - 使用缓存
            if (!productService.checkStock(productId, 1)) {
                result.put("success", false);
                result.put("message", "商品库存不足");
                return result;
            }
            
            // 检查用户积分是否足够 - 使用缓存
            Integer currentPoints = userPointsCacheService.getUserPoints(userId.intValue());
            if (currentPoints < product.getPoints()) {
                result.put("success", false);
                result.put("message", "积分不足，当前积分：" + currentPoints + "，需要积分：" + product.getPoints());
                return result;
            }
            
            // 扣减用户积分 - 使用缓存服务
            try {
                userPointsCacheService.updateUserPoints(userId.intValue(), -product.getPoints());
            } catch (Exception e) {
                result.put("success", false);
                result.put("message", "积分扣减失败：" + e.getMessage());
                return result;
            }
            
            // 添加积分消耗记录
            pointsRecordService.addPointsRecord(
                userId.intValue(), 
                -product.getPoints(), 
                "consume", 
                "商品兑换", 
                "兑换商品：" + product.getName()
            );
            
            // 创建兑换记录
            Exchange exchange = exchangeService.createExchange(
                userId, userName, productId, product.getName(), 
                product.getImageUrl(), product.getPoints()
            );
            
            // 减少库存
            productService.decreaseStock(productId, 1);
            
            // 获取用户的最新积分 - 使用缓存
            Integer latestPoints = userPointsCacheService.getUserPoints(userId.intValue());
            
            result.put("success", true);
            result.put("message", "兑换成功");
            result.put("data", exchange);
            result.put("userPoints", latestPoints);
            
        } catch (Exception e) {
            logger.error("创建兑换记录失败: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("message", "兑换失败: " + e.getMessage());
        }
        return result;
    }

    @PutMapping("/{id}/status")
    @ApiOperation("更新兑换状态")
    public Map<String, Object> updateExchangeStatus(@PathVariable Long id, @RequestParam String status) {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean success = exchangeService.updateExchangeStatus(id, status);
            if (success) {
                result.put("success", true);
                result.put("message", "状态更新成功");
            } else {
                result.put("success", false);
                result.put("message", "状态更新失败");
            }
        } catch (Exception e) {
            logger.error("更新兑换状态失败: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("message", "状态更新失败: " + e.getMessage());
        }
        return result;
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除兑换记录")
    public Map<String, Object> deleteExchange(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean success = exchangeService.deleteExchange(id);
            if (success) {
                result.put("success", true);
                result.put("message", "兑换记录删除成功");
            } else {
                result.put("success", false);
                result.put("message", "兑换记录删除失败");
            }
        } catch (Exception e) {
            logger.error("删除兑换记录失败: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("message", "删除兑换记录失败: " + e.getMessage());
        }
        return result;
    }

    @GetMapping("/stats")
    @ApiOperation("获取兑换统计")
    public Map<String, Object> getExchangeStats(@RequestParam(required = false) Long userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            Long totalExchanges = exchangeService.getTotalExchanges();
            Long userExchanges = userId != null ? exchangeService.getUserExchangeCount(userId) : 0L;
            
            result.put("totalExchanges", totalExchanges);
            result.put("userExchanges", userExchanges);
            result.put("success", true);
            
        } catch (Exception e) {
            logger.error("获取兑换统计失败: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("message", "获取统计失败: " + e.getMessage());
        }
        return result;
    }
}
