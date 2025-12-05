package com.agriculture.common.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 通知服务Feign客户端
 */
@FeignClient(name = "notification-service", url = "http://localhost:8082", path = "/api/notification")
public interface NotificationServiceClient {

    /**
     * 发送订单通知
     */
    @PostMapping("/order")
    Result<String> sendOrderNotification(@RequestBody OrderNotificationRequest request);

    /**
     * 订单通知请求类
     */
    class OrderNotificationRequest {
        private String userId;
        private String orderId;
        private String orderStatus;
        private String message;
        private String notificationType;

        // Getters and Setters
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getOrderId() { return orderId; }
        public void setOrderId(String orderId) { this.orderId = orderId; }
        public String getOrderStatus() { return orderStatus; }
        public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public String getNotificationType() { return notificationType; }
        public void setNotificationType(String notificationType) { this.notificationType = notificationType; }
    }

    /**
     * 通用响应结果类
     */
    class Result<T> {
        private Integer code;
        private String message;
        private T data;

        // Getters and Setters
        public Integer getCode() { return code; }
        public void setCode(Integer code) { this.code = code; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public T getData() { return data; }
        public void setData(T data) { this.data = data; }
    }
}
