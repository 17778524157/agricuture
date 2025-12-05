package com.agriculture.mall.service;

import com.agriculture.mall.entity.Exchange;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 兑换记录服务接口
 */
public interface ExchangeService extends IService<Exchange> {
    
    /**
     * 分页查询兑换记录
     */
    Page<Exchange> getExchangePage(Integer pageNum, Integer pageSize, Long userId);
    
    /**
     * 获取用户兑换记录
     */
    List<Exchange> getUserExchanges(Long userId);
    
    /**
     * 获取所有兑换记录
     */
    List<Exchange> getAllExchanges();
    
    /**
     * 创建兑换记录
     */
    Exchange createExchange(Long userId, String userName, Long productId, String productName, 
                           String productImage, Integer points);
    
    /**
     * 更新兑换状态
     */
    boolean updateExchangeStatus(Long exchangeId, String status);
    
    /**
     * 删除兑换记录
     */
    boolean deleteExchange(Long exchangeId);
    
    /**
     * 获取兑换统计
     */
    Long getTotalExchanges();
    
    /**
     * 获取用户兑换统计
     */
    Long getUserExchangeCount(Long userId);
}
