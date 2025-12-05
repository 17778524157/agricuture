package com.agriculture.mall.controller;

import com.agriculture.common.result.Result;
import com.agriculture.mall.entity.MallOrder;
import com.agriculture.mall.service.MallOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 订单控制器
 */
@RestController
@RequestMapping("/api/mall/orders")
public class OrderController {

    @Autowired
    private MallOrderService mallOrderService;

    /**
     * 创建订单
     */
    @PostMapping
    public Result<MallOrder> createOrder(
            @RequestParam Integer userId,
            @RequestParam Integer productId,
            @RequestParam Integer quantity,
            @RequestParam String shippingAddress,
            @RequestParam String contactPhone) {
        
        MallOrder order = mallOrderService.createOrder(userId, productId, quantity, shippingAddress, contactPhone);
        return Result.success(order);
    }

    /**
     * 获取用户订单列表
     */
    @GetMapping("/user/{userId}")
    public Result<List<MallOrder>> getUserOrders(@PathVariable Integer userId) {
        List<MallOrder> orders = mallOrderService.getUserOrders(userId);
        return Result.success(orders);
    }

    /**
     * 获取订单详情
     */
    @GetMapping("/{orderId}/user/{userId}")
    public Result<MallOrder> getOrderDetail(@PathVariable Integer orderId, @PathVariable Integer userId) {
        MallOrder order = mallOrderService.getOrderDetail(orderId, userId);
        if (order != null) {
            return Result.success(order);
        }
        return Result.fail("订单不存在");
    }

    /**
     * 取消订单
     */
    @PutMapping("/{orderId}/cancel")
    public Result<Boolean> cancelOrder(@PathVariable Integer orderId, @RequestParam Integer userId) {
        boolean result = mallOrderService.cancelOrder(orderId, userId);
        return result ? Result.success(true) : Result.fail("取消订单失败");
    }

    /**
     * 完成订单（管理员接口）
     */
    @PutMapping("/{orderId}/complete")
    public Result<Boolean> completeOrder(@PathVariable Integer orderId) {
        boolean result = mallOrderService.completeOrder(orderId);
        return result ? Result.success(true) : Result.fail("完成订单失败");
    }
}