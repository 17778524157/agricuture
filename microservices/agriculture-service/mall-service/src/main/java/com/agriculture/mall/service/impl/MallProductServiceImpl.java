package com.agriculture.mall.service.impl;

import com.agriculture.mall.entity.MallProduct;
import com.agriculture.mall.mapper.MallProductMapper;
import com.agriculture.mall.service.MallProductService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 商品服务实现类
 */
@Service
public class MallProductServiceImpl extends ServiceImpl<MallProductMapper, MallProduct> implements MallProductService {

/**
 * 获取所有在售商品的列表
 * 该方法会查询状态为"ON_SALE"的商品，并按照创建时间降序排列
 *
 * @return 返回在售商品的列表，如果没有找到则返回空列表
 */
    @Override
    public List<MallProduct> getOnSaleProducts() {
    // 创建查询条件构造器
        QueryWrapper<MallProduct> queryWrapper = new QueryWrapper<>();
    // 设置查询条件：商品状态为在售
        queryWrapper.eq("status", "ON_SALE")
               // 设置排序条件：按创建时间降序排列
                   .orderByDesc("create_time");
    // 执行查询并返回结果
        return baseMapper.selectList(queryWrapper);
    }
    
    @Override
    public List<MallProduct> getOnSaleProducts(Integer pageNum, Integer pageSize, String keyword, String category, String sortBy) {
        QueryWrapper<MallProduct> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", "ON_SALE");
        
        // 添加关键词搜索条件
        if (keyword != null && !keyword.isEmpty()) {
            queryWrapper.and(wrapper -> wrapper.like("name", keyword).or().like("description", keyword));
        }
        
        // 添加分类筛选条件
        if (category != null && !category.isEmpty()) {
            queryWrapper.eq("category", category);
        }
        
        // 添加排序条件
        if (sortBy != null && !sortBy.isEmpty()) {
            switch (sortBy) {
                case "points_asc":
                    queryWrapper.orderByAsc("points");
                    break;
                case "points_desc":
                    queryWrapper.orderByDesc("points");
                    break;
                case "newest":
                    queryWrapper.orderByDesc("create_time");
                    break;
                default:
                    queryWrapper.orderByDesc("create_time");
                    break;
            }
        } else {
            queryWrapper.orderByDesc("create_time");
        }
        
        // 分页查询
        Page<MallProduct> page = new Page<>(pageNum, pageSize);
        return baseMapper.selectPage(page, queryWrapper).getRecords();
    }
    
    @Override
    public long getOnSaleProductsCount(String keyword, String category) {
        QueryWrapper<MallProduct> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", "ON_SALE");
        
        // 添加关键词搜索条件
        if (keyword != null && !keyword.isEmpty()) {
            queryWrapper.and(wrapper -> wrapper.like("name", keyword).or().like("description", keyword));
        }
        
        // 添加分类筛选条件
        if (category != null && !category.isEmpty()) {
            queryWrapper.eq("category", category);
        }
        
        return baseMapper.selectCount(queryWrapper);
    }

    @Override
    public List<MallProduct> getProductsByCategory(String category) {
        return baseMapper.selectByCategory(category);
    }

    @Override
    public List<MallProduct> searchProducts(String keyword) {
        return baseMapper.searchProducts(keyword);
    }

    @Transactional
    @Override
    public boolean deductStock(Integer productId, Integer quantity) {
        // 先查询商品库存
        MallProduct product = baseMapper.selectById(productId);
        if (product == null || product.getStock() < quantity) {
            return false;
        }
        
        // 更新库存
        product.setStock(product.getStock() - quantity);
        int result = baseMapper.updateById(product);
        return result > 0;
    }

    @Transactional
    @Override
    public void addStock(Integer productId, Integer quantity) {
        MallProduct product = baseMapper.selectById(productId);
        if (product != null) {
            product.setStock(product.getStock() + quantity);
            baseMapper.updateById(product);
        }
    }
}