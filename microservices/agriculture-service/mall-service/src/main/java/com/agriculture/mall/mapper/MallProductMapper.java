package com.agriculture.mall.mapper;

import com.agriculture.mall.entity.MallProduct;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品Mapper接口
 */
public interface MallProductMapper extends BaseMapper<MallProduct> {
    
    /**
     * 根据分类查询商品
     */
    List<MallProduct> selectByCategory(@Param("category") String category);
    
    /**
     * 根据分类分页查询商品
     */
    Page<MallProduct> selectPageByCategory(Page<MallProduct> page, @Param("category") String category);
    
    /**
     * 分页查询所有上架商品
     */
    Page<MallProduct> selectPageOnSale(Page<MallProduct> page);
    
    /**
     * 搜索商品
     */
    List<MallProduct> searchProducts(@Param("keyword") String keyword);
    
    /**
     * 分页搜索商品
     */
    Page<MallProduct> searchProductsPage(Page<MallProduct> page, @Param("keyword") String keyword);
    
}