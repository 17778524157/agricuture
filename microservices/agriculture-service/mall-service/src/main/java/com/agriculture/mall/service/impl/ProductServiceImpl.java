package com.agriculture.mall.service.impl;

import com.agriculture.mall.entity.Product;
import com.agriculture.mall.mapper.ProductMapper;
import com.agriculture.mall.service.ProductService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 商品服务实现类
 */
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    @Override
    public Page<Product> getProductPage(Integer pageNum, Integer pageSize, String keyword, String category) {
        Page<Product> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        
        // 关键词搜索
        if (keyword != null && !keyword.trim().isEmpty()) {
            queryWrapper.and(wrapper -> wrapper
                .like("name", keyword)
                .or()
                .like("description", keyword)
            );
        }
        
        // 分类筛选
        if (category != null && !category.trim().isEmpty() && !"all".equals(category)) {
            queryWrapper.eq("category", category);
        }
        
        // 只显示上架商品
        queryWrapper.eq("status", 1);
        
        // 按创建时间倒序
        queryWrapper.orderByDesc("create_time");
        
        return this.page(page, queryWrapper);
    }

    @Override
    public List<Product> getAllProducts() {
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 1);
        queryWrapper.orderByDesc("create_time");
        return this.list(queryWrapper);
    }

    @Override
    public List<Product> getAllProductsForAdmin() {
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        // 管理端显示所有商品，不限制状态
        queryWrapper.orderByDesc("create_time");
        return this.list(queryWrapper);
    }

    @Override
    public List<Product> getProductsByCategory(String category) {
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("category", category);
        queryWrapper.eq("status", 1);
        queryWrapper.orderByDesc("create_time");
        return this.list(queryWrapper);
    }

    @Override
    public List<Product> searchProducts(String keyword) {
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        queryWrapper.and(wrapper -> wrapper
            .like("name", keyword)
            .or()
            .like("description", keyword)
        );
        queryWrapper.eq("status", 1);
        queryWrapper.orderByDesc("create_time");
        return this.list(queryWrapper);
    }

    @Override
    public List<Product> getNewProducts() {
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_new", true);
        queryWrapper.eq("status", 1);
        queryWrapper.orderByDesc("create_time");
        return this.list(queryWrapper);
    }

    @Override
    public List<Product> getLowStockProducts() {
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        queryWrapper.lt("stock", 10);
        queryWrapper.eq("status", 1);
        queryWrapper.orderByAsc("stock");
        return this.list(queryWrapper);
    }

    @Override
    public boolean updateStock(Long productId, Integer stock) {
        Product product = this.getById(productId);
        if (product != null) {
            product.setStock(stock);
            return this.updateById(product);
        }
        return false;
    }

    @Override
    public boolean decreaseStock(Long productId, Integer quantity) {
        Product product = this.getById(productId);
        if (product != null && product.getStock() >= quantity) {
            product.setStock(product.getStock() - quantity);
            return this.updateById(product);
        }
        return false;
    }

    @Override
    public boolean checkStock(Long productId, Integer quantity) {
        Product product = this.getById(productId);
        return product != null && product.getStock() >= quantity;
    }
}
