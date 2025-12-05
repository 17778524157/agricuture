package com.agriculture.mall.service;

import com.agriculture.mall.entity.MallOrder;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 订单服务接口
 */
public interface MallOrderService extends IService<MallOrder> {
    
    /**
     * 创建订单（积分兑换商品）
     */
    MallOrder createOrder(Integer userId, Integer productId, Integer quantity, String shippingAddress, String contactPhone);
    
    /**
     * 获取用户订单列表
     */
    List<MallOrder> getUserOrders(Integer userId);
    
    /**
     * 获取订单详情
     */
    MallOrder getOrderDetail(Integer orderId, Integer userId);
    
    /**
     * 取消订单
     */
    boolean cancelOrder(Integer orderId, Integer userId);
    
    /**
     * 完成订单
     */
    boolean completeOrder(Integer orderId);
    
}