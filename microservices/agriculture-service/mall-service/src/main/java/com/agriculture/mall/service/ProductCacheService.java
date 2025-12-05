package com.agriculture.mall.service;

import com.agriculture.mall.entity.MallProduct;
import com.agriculture.mall.mapper.MallProductMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.Set;

/**
 * 商品信息缓存服务
 * 实现Canal双写一致性：
 * 1. 写操作：只写数据库，不写缓存
 * 2. Canal监听binlog，自动同步到Redis缓存
 * 3. 读操作：优先从缓存读取，缓存未命中时查数据库并写入缓存
 */
@Slf4j
@Service
public class ProductCacheService {

    @Autowired
    private MallProductMapper productMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private CanalDataSyncService canalDataSyncService;

    // 缓存key前缀
    private static final String PRODUCT_KEY = "mall:product:info:";
    private static final String PRODUCT_LIST_KEY = "mall:product:list:";
    private static final String PRODUCT_STOCK_LOCK_KEY = "mall:product:stock:lock:";
    
    // 缓存过期时间
    private static final Duration CACHE_TTL = Duration.ofHours(1);

    /**
     * 获取商品信息 - Cache-Aside模式
     */
    public MallProduct getProductById(Integer productId) {
        String cacheKey = PRODUCT_KEY + productId;
        
        try {
            // 1. 先查缓存
            MallProduct product = (MallProduct) redisTemplate.opsForValue().get(cacheKey);
            if (product != null) {
                log.debug("商品缓存命中，商品ID: {}", productId);
                return product;
            }
            
            // 2. 缓存未命中，查数据库
            log.debug("商品缓存未命中，查询数据库，商品ID: {}", productId);
            product = productMapper.selectById(productId);
            
            // 3. 写入缓存
            if (product != null) {
                redisTemplate.opsForValue().set(cacheKey, product, CACHE_TTL);
                log.debug("商品写入缓存成功，商品ID: {}", productId);
            }
            
            return product;
            
        } catch (Exception e) {
            log.error("获取商品信息失败，商品ID: {}", productId, e);
            // 缓存异常时，直接查数据库
            return productMapper.selectById(productId);
        }
    }

    /**
     * 更新商品信息 - Canal双写一致性
     * 1. 只更新数据库
     * 2. Canal监听binlog自动同步到缓存
     * 3. 如果Canal不可用，手动同步到缓存
     */
    @Transactional
    public void updateProduct(MallProduct product) {
        try {
            // 1. 更新数据库
            int updatedRows = productMapper.updateById(product);
            if (updatedRows <= 0) {
                throw new RuntimeException("商品更新失败，商品ID: " + product.getId());
            }

            // 2. Canal双写一致性：优先使用Canal自动同步
            if (canalDataSyncService.isRunning()) {
                log.debug("商品更新成功，商品ID: {} (Canal将自动同步缓存)", product.getId());
            } else {
                // Canal不可用时，手动同步到缓存
                canalDataSyncService.syncProductToCache(
                    product.getId(), 
                    product.getName(), 
                    product.getPoints().toString(), 
                    product.getStock().toString()
                );
                log.debug("商品更新成功，商品ID: {} (手动同步缓存)", product.getId());
            }
            
            // 3. 清除相关列表缓存
            clearProductListCache();
            
        } catch (Exception e) {
            log.error("更新商品信息失败，商品ID: {}", product.getId(), e);
            throw e;
        }
    }

    /**
     * 增加商品库存 - 用于订单取消时恢复库存
     */
    @Transactional
    public boolean addProductStock(Integer productId, Integer quantity) {
        String lockKey = PRODUCT_STOCK_LOCK_KEY + productId;
        
        // 获取分布式锁
        Boolean lockAcquired = redisTemplate.opsForValue()
            .setIfAbsent(lockKey, "locked", Duration.ofSeconds(10));
        
        if (!lockAcquired) {
            log.warn("获取商品库存锁失败，商品ID: {}", productId);
            return false;
        }
        
        try {
            // 1. 查询当前库存
            MallProduct product = getProductById(productId);
            if (product == null) {
                log.warn("商品不存在，商品ID: {}", productId);
                return false;
            }
            
            // 2. 增加库存
            product.setStock(product.getStock() + quantity);
            updateProduct(product);
            
            log.info("商品库存增加成功，商品ID: {}, 增加数量: {}, 当前库存: {}", 
                productId, quantity, product.getStock());
            
            return true;
            
        } finally {
            // 释放锁
            redisTemplate.delete(lockKey);
        }
    }

    /**
     * 商品库存扣减 - 高并发场景
     * 使用分布式锁防止超卖
     */
    @Transactional
    public boolean deductProductStock(Integer productId, Integer quantity) {
        String lockKey = PRODUCT_STOCK_LOCK_KEY + productId;
        
        // 获取分布式锁
        Boolean lockAcquired = redisTemplate.opsForValue()
            .setIfAbsent(lockKey, "locked", Duration.ofSeconds(10));
        
        if (!lockAcquired) {
            log.warn("获取商品库存锁失败，商品ID: {}", productId);
            return false;
        }
        
        try {
            // 1. 查询当前库存
            MallProduct product = getProductById(productId);
            if (product == null) {
                log.warn("商品不存在，商品ID: {}", productId);
                return false;
            }
            
            if (product.getStock() < quantity) {
                log.warn("商品库存不足，商品ID: {}, 当前库存: {}, 需要扣减: {}", 
                    productId, product.getStock(), quantity);
                return false;
            }
            
            // 2. 扣减库存
            product.setStock(product.getStock() - quantity);
            updateProduct(product);
            
            log.info("商品库存扣减成功，商品ID: {}, 扣减数量: {}, 剩余库存: {}", 
                productId, quantity, product.getStock());
            
            return true;
            
        } finally {
            // 释放锁
            redisTemplate.delete(lockKey);
        }
    }

    /**
     * 搜索商品 - 带缓存
     */
    public List<MallProduct> searchProducts(String keyword, Integer page, Integer size) {
        String cacheKey = PRODUCT_LIST_KEY + "search:" + keyword + ":" + page + ":" + size;
        
        try {
            // 1. 先查缓存
            @SuppressWarnings("unchecked")
            List<MallProduct> products = (List<MallProduct>) redisTemplate.opsForValue().get(cacheKey);
            if (products != null) {
                log.debug("商品搜索缓存命中，关键词: {}, 页码: {}", keyword, page);
                return products;
            }
            
            // 2. 缓存未命中，查数据库
            log.debug("商品搜索缓存未命中，查询数据库，关键词: {}, 页码: {}", keyword, page);
            
            // 创建分页对象
            Page<MallProduct> pageInfo = new Page<>(page, size);
            
            // 搜索商品
            Page<MallProduct> result = productMapper.searchProductsPage(pageInfo, keyword);
            products = result.getRecords();
            
            // 3. 写入缓存
            if (products != null && !products.isEmpty()) {
                redisTemplate.opsForValue().set(cacheKey, products, Duration.ofMinutes(15)); // 搜索结果缓存时间短一些
                log.debug("商品搜索写入缓存成功，关键词: {}, 页码: {}, 数量: {}", keyword, page, products.size());
            }
            
            return products;
            
        } catch (Exception e) {
            log.error("搜索商品失败，关键词: {}, 页码: {}", keyword, page, e);
            return null;
        }
    }

    /**
     * 获取商品列表 - 带缓存
     */
    public List<MallProduct> getProductList(String category, Integer page, Integer size) {
        String cacheKey = PRODUCT_LIST_KEY + category + ":" + page + ":" + size;
        
        try {
            // 1. 先查缓存
            @SuppressWarnings("unchecked")
            List<MallProduct> products = (List<MallProduct>) redisTemplate.opsForValue().get(cacheKey);
            if (products != null) {
                log.debug("商品列表缓存命中，分类: {}, 页码: {}", category, page);
                return products;
            }
            
            // 2. 缓存未命中，查数据库
            log.debug("商品列表缓存未命中，查询数据库，分类: {}, 页码: {}", category, page);
            
            // 创建分页对象
            Page<MallProduct> pageInfo = new Page<>(page, size);
            
            // 根据分类查询商品
            if (category != null && !category.trim().isEmpty() && !"all".equals(category)) {
                // 按分类查询
                Page<MallProduct> result = productMapper.selectPageByCategory(pageInfo, category);
                products = result.getRecords();
            } else {
                // 查询所有上架商品
                Page<MallProduct> result = productMapper.selectPageOnSale(pageInfo);
                products = result.getRecords();
            }
            
            // 3. 写入缓存
            if (products != null && !products.isEmpty()) {
                redisTemplate.opsForValue().set(cacheKey, products, Duration.ofMinutes(30));
                log.debug("商品列表写入缓存成功，分类: {}, 页码: {}, 数量: {}", category, page, products.size());
            }
            
            return products;
            
        } catch (Exception e) {
            log.error("获取商品列表失败，分类: {}, 页码: {}", category, page, e);
            return null;
        }
    }

    /**
     * 删除商品缓存
     */
    public void evictProductCache(Integer productId) {
        String cacheKey = PRODUCT_KEY + productId;
        redisTemplate.delete(cacheKey);
        log.debug("删除商品缓存，商品ID: {}", productId);
    }

    /**
     * 清除商品列表缓存
     */
    private void clearProductListCache() {
        try {
            String pattern = PRODUCT_LIST_KEY + "*";
            Set<String> keys = redisTemplate.keys(pattern);
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
                log.debug("清除商品列表缓存，数量: {}", keys.size());
            }
        } catch (Exception e) {
            log.error("清除商品列表缓存失败", e);
        }
    }

    /**
     * 批量预热商品缓存
     */
    public void warmupProductCache() {
        try {
            // 预热所有上架商品（第一页）
            List<MallProduct> allProducts = getProductList("all", 1, 20);
            log.info("预热所有商品缓存完成，数量: {}", allProducts != null ? allProducts.size() : 0);
            
            // 预热各分类商品（第一页）
            String[] categories = {"种子", "肥料", "农药", "工具", "其他"};
            for (String category : categories) {
                List<MallProduct> categoryProducts = getProductList(category, 1, 20);
                log.info("预热分类商品缓存完成，分类: {}, 数量: {}", category, 
                    categoryProducts != null ? categoryProducts.size() : 0);
            }
            
            log.info("商品缓存预热完成");
        } catch (Exception e) {
            log.error("商品缓存预热失败", e);
        }
    }
}
