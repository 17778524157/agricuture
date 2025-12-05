package com.agriculture.mall.service.impl;

import com.agriculture.common.client.UserServiceClient;
import com.agriculture.common.client.NotificationServiceClient;
import com.agriculture.common.lock.DistributedLock;
import com.agriculture.mall.entity.MallOrder;
import com.agriculture.mall.entity.MallProduct;
import com.agriculture.mall.mapper.MallOrderMapper;
import com.agriculture.mall.service.MallOrderService;
import com.agriculture.mall.service.ProductCacheService;
import com.agriculture.mall.service.UserPointsCacheService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 商城订单服务实现类
 * 使用分布式锁解决高并发下单问题
 */
@Slf4j
@Service
public class OrderServiceImpl extends ServiceImpl<MallOrderMapper, MallOrder> implements MallOrderService {

    @Autowired
    private ProductCacheService productCacheService;
    
    @Autowired
    private UserPointsCacheService userPointsCacheService;

    @Autowired
    private DistributedLock distributedLock;

    @Autowired
    private UserServiceClient userServiceClient;

    @Autowired
    private NotificationServiceClient notificationServiceClient;

    /**
     * 创建订单 - 使用分布式锁防止重复下单和超卖
     */
    @Override
    @Transactional
    public MallOrder createOrder(Integer userId, Integer productId, Integer quantity, String shippingAddress, String contactPhone) {
        // 构建锁key：用户ID + 商品ID，防止同一用户重复下单同一商品
        String lockKey = "mall:order:create:lock:" + userId + ":" + productId;
        
        return distributedLock.executeWithLock(lockKey, () -> {
            log.info("开始创建订单，用户ID: {}, 商品ID: {}, 数量: {}, 收货地址: {}, 联系电话: {}", 
                userId, productId, quantity, shippingAddress, contactPhone);
            
            // 1. 检查商品库存
            MallProduct product = productCacheService.getProductById(productId);
            if (product == null) {
                throw new RuntimeException("商品不存在");
            }
            
            if (product.getStock() < quantity) {
                throw new RuntimeException("商品库存不足，当前库存: " + product.getStock());
            }
            
            // 2. 计算所需积分（商品单价 * 数量）
            Integer totalPoints = product.getPoints() * quantity;
            
            // 3. 检查用户积分
            Integer userPoints = userPointsCacheService.getUserPoints(userId);
            if (userPoints < totalPoints) {
                throw new RuntimeException("用户积分不足，当前积分: " + userPoints + ", 需要积分: " + totalPoints);
            }
            
            // 4. 扣减商品库存
            boolean stockDeducted = productCacheService.deductProductStock(productId, quantity);
            if (!stockDeducted) {
                throw new RuntimeException("商品库存扣减失败");
            }
            
            // 5. 扣减用户积分
            boolean pointsDeducted = userPointsCacheService.deductUserPoints(userId, totalPoints);
            if (!pointsDeducted) {
                // 回滚库存
                productCacheService.addProductStock(productId, quantity);
                throw new RuntimeException("用户积分扣减失败");
            }
            
            // 6. 获取用户信息（远程调用）
            UserServiceClient.User userInfo = null;
            try {
                UserServiceClient.Result<UserServiceClient.User> userResult = 
                    userServiceClient.getUserById(String.valueOf(userId));
                if (userResult != null && userResult.getCode() == 200 && userResult.getData() != null) {
                    userInfo = userResult.getData();
                    log.info("获取用户信息成功，用户ID: {}, 用户名: {}", userId, userInfo.getUsername());
                }
            } catch (Exception e) {
                log.warn("获取用户信息失败，用户ID: {}, 错误: {}", userId, e.getMessage());
            }
            
            // 7. 创建订单
            MallOrder order = new MallOrder();
            order.setOrderNo(generateOrderNo());
            order.setUserId(userId);
            order.setProductId(productId);
            order.setQuantity(quantity);
            order.setTotalPoints(totalPoints);
            order.setPointsUsed(totalPoints);
            order.setStatus("PENDING");
            order.setShippingAddress(shippingAddress);
            order.setContactPhone(contactPhone);
            order.setCreateTime(LocalDateTime.now());
            order.setUpdateTime(LocalDateTime.now());
            
            baseMapper.insert(order);
            
            log.info("订单创建成功，订单号: {}, 用户ID: {}, 商品ID: {}", 
                order.getOrderNo(), userId, productId);
            
            // 8. 发送订单通知（远程调用）
            sendOrderNotification(order, userInfo);
            
            return order;
        });
    }

    /**
     * 取消订单 - 使用分布式锁防止重复取消
     */
    @Override
    @Transactional
    public boolean cancelOrder(Integer orderId, Integer userId) {
        String lockKey = "mall:order:cancel:lock:" + userId + ":" + orderId;
        
        return distributedLock.executeWithLock(lockKey, () -> {
            log.info("开始取消订单，用户ID: {}, 订单ID: {}", userId, orderId);
            
            // 1. 查询订单
            MallOrder order = baseMapper.selectById(orderId);
            if (order == null || !order.getUserId().equals(userId)) {
                throw new RuntimeException("订单不存在或无权限操作");
            }
            
            if (!"PENDING".equals(order.getStatus())) {
                throw new RuntimeException("订单状态不允许取消");
            }
            
            // 2. 恢复商品库存
            productCacheService.addProductStock(order.getProductId(), order.getQuantity());
            
            // 3. 恢复用户积分
            if (order.getPointsUsed() > 0) {
                userPointsCacheService.addUserPoints(order.getUserId(), order.getPointsUsed());
            }
            
            // 4. 更新订单状态
            order.setStatus("CANCELLED");
            order.setUpdateTime(LocalDateTime.now());
            baseMapper.updateById(order);
            
            log.info("订单取消成功，订单ID: {}", orderId);
            return true;
        });
    }


    /**
     * 获取用户订单列表
     */
    @Override
    public List<MallOrder> getUserOrders(Integer userId) {
        log.info("获取用户订单列表，用户ID: {}", userId);
        return baseMapper.selectByUserId(userId);
    }
    
    /**
     * 获取订单详情
     */
    @Override
    public MallOrder getOrderDetail(Integer orderId, Integer userId) {
        log.info("获取订单详情，订单ID: {}, 用户ID: {}", orderId, userId);
        
        MallOrder order = baseMapper.selectById(orderId);
        if (order == null) {
            log.warn("订单不存在，订单ID: {}", orderId);
            return null;
        }
        
        if (!order.getUserId().equals(userId)) {
            log.warn("无权限查看此订单，订单ID: {}, 用户ID: {}", orderId, userId);
            return null;
        }
        
        return order;
    }
    
    /**
     * 完成订单
     */
    @Override
    public boolean completeOrder(Integer orderId) {
        log.info("完成订单，订单ID: {}", orderId);
        
        MallOrder order = baseMapper.selectById(orderId);
        if (order == null) {
            log.warn("订单不存在，订单ID: {}", orderId);
            return false;
        }
        
        if (!"PAID".equals(order.getStatus())) {
            log.warn("订单状态不允许完成，当前状态: {}", order.getStatus());
            return false;
        }
        
        order.setStatus("COMPLETED");
        order.setUpdateTime(LocalDateTime.now());
        
        int result = baseMapper.updateById(order);
        if (result > 0) {
            log.info("订单完成成功，订单ID: {}", orderId);
            return true;
        } else {
            log.error("订单完成失败，订单ID: {}", orderId);
            return false;
        }
    }
    
    /**
     * 发送订单通知
     */
    private void sendOrderNotification(MallOrder order, UserServiceClient.User userInfo) {
        try {
            NotificationServiceClient.OrderNotificationRequest request = 
                new NotificationServiceClient.OrderNotificationRequest();
            request.setUserId(String.valueOf(order.getUserId()));
            request.setOrderId(order.getOrderNo());
            request.setOrderStatus(order.getStatus());
            request.setMessage("您的订单 " + order.getOrderNo() + " 已创建，请及时处理");
            request.setNotificationType("ORDER_CREATED");
            
            NotificationServiceClient.Result<String> result = 
                notificationServiceClient.sendOrderNotification(request);
            
            if (result != null && result.getCode() == 200) {
                log.info("订单通知发送成功，订单号: {}, 用户ID: {}", order.getOrderNo(), order.getUserId());
            } else {
                log.warn("订单通知发送失败，订单号: {}, 用户ID: {}", order.getOrderNo(), order.getUserId());
            }
        } catch (Exception e) {
            log.error("发送订单通知异常，订单号: {}, 用户ID: {}, 错误: {}", 
                order.getOrderNo(), order.getUserId(), e.getMessage());
        }
    }

    /**
     * 生成订单号
     */
    private String generateOrderNo() {
        return "ORD" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
