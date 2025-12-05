package com.agriculture.mall.service;

import com.agriculture.mall.entity.Product;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 商品服务接口
 */
public interface ProductService extends IService<Product> {
    
    /**
     * 分页查询商品
     */
    Page<Product> getProductPage(Integer pageNum, Integer pageSize, String keyword, String category);
    
    /**
     * 获取所有商品
     */
    List<Product> getAllProducts();
    
    /**
     * 获取所有商品（管理端，包括下架商品）
     */
    List<Product> getAllProductsForAdmin();
    
    /**
     * 根据分类获取商品
     */
    List<Product> getProductsByCategory(String category);
    
    /**
     * 搜索商品
     */
    List<Product> searchProducts(String keyword);
    
    /**
     * 获取新品
     */
    List<Product> getNewProducts();
    
    /**
     * 获取低库存商品
     */
    List<Product> getLowStockProducts();
    
    /**
     * 更新商品库存
     */
    boolean updateStock(Long productId, Integer stock);
    
    /**
     * 减少商品库存
     */
    boolean decreaseStock(Long productId, Integer quantity);
    
    /**
     * 检查商品库存是否充足
     */
    boolean checkStock(Long productId, Integer quantity);
}
