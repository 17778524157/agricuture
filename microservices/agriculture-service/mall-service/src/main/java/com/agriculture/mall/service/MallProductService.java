package com.agriculture.mall.service;

import com.agriculture.mall.entity.MallProduct;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 商品服务接口
 */
public interface MallProductService extends IService<MallProduct> {
    
    /**
     * 获取上架商品列表（支持分页）
     */
    List<MallProduct> getOnSaleProducts();
    
    /**
     * 获取上架商品列表（支持分页、搜索、分类和排序）
     */
    List<MallProduct> getOnSaleProducts(Integer pageNum, Integer pageSize, String keyword, String category, String sortBy);
    
    /**
     * 获取上架商品总数
     */
    long getOnSaleProductsCount(String keyword, String category);
    
    /**
     * 根据分类获取商品
     */
    List<MallProduct> getProductsByCategory(String category);
    
    /**
     * 搜索商品
     */
    List<MallProduct> searchProducts(String keyword);
    
    /**
     * 扣除商品库存
     */
    boolean deductStock(Integer productId, Integer quantity);
    
    /**
     * 增加商品库存
     */
    void addStock(Integer productId, Integer quantity);
    
}