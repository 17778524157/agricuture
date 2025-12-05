package com.agriculture.notification.controller;

import com.agriculture.notification.entity.FarmAlertMessage;
import com.agriculture.notification.mq.FarmAlertMessageProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 农田异常预警控制器
 * 用于接收来自其他服务的农田异常预警请求
 */
@RestController
@RequestMapping("/api/farm-alert")
public class FarmAlertController {
    
    private static final Logger logger = LoggerFactory.getLogger(FarmAlertController.class);
    
    @Autowired
    private FarmAlertMessageProducer farmAlertMessageProducer;
    
    /**
     * 发送农田异常预警通知
     * @param alertMessage 预警消息对象
     * @return 处理结果
     */
    @PostMapping("/send")
    public Result sendFarmAlert(@RequestBody FarmAlertMessage alertMessage) {
        logger.info("接收到农田异常预警请求: {}", alertMessage);
        
        try {
            // 补全预警时间
            if (alertMessage.getAlertTime() == null || alertMessage.getAlertTime().isEmpty()) {
                String currentTime = LocalDateTime.now().format(
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                alertMessage.setAlertTime(currentTime);
            }
            
            // 发送预警消息到RabbitMQ
            farmAlertMessageProducer.sendFarmAlertMessage(alertMessage);
            
            return Result.success("农田异常预警通知已发送");
        } catch (Exception e) {
            logger.error("处理农田异常预警请求时发生异常: {}", e.getMessage(), e);
            return Result.error("发送农田异常预警通知失败: " + e.getMessage());
        }
    }
    
    /**
     * 批量发送农田异常预警通知
     * @param alertMessages 预警消息列表
     * @return 处理结果
     */
    @PostMapping("/batch-send")
    public Result batchSendFarmAlert(@RequestBody FarmAlertMessage[] alertMessages) {
        logger.info("接收到批量农田异常预警请求，共{}条消息", alertMessages.length);
        
        try {
            for (FarmAlertMessage alertMessage : alertMessages) {
                // 补全预警时间
                if (alertMessage.getAlertTime() == null || alertMessage.getAlertTime().isEmpty()) {
                    String currentTime = LocalDateTime.now().format(
                            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    alertMessage.setAlertTime(currentTime);
                }
                
                // 发送预警消息到RabbitMQ
                farmAlertMessageProducer.sendFarmAlertMessage(alertMessage);
            }
            
            return Result.success("批量农田异常预警通知已发送");
        } catch (Exception e) {
            logger.error("处理批量农田异常预警请求时发生异常: {}", e.getMessage(), e);
            return Result.error("发送批量农田异常预警通知失败: " + e.getMessage());
        }
    }
    
    /**
     * 简单的结果封装类
     * 用于统一控制器返回格式
     */
    public static class Result {
        private int code;
        private String message;
        private Object data;
        
        public Result(int code, String message, Object data) {
            this.code = code;
            this.message = message;
            this.data = data;
        }
        
        public static Result success(Object data) {
            return new Result(200, "success", data);
        }
        
        public static Result error(String message) {
            return new Result(500, message, null);
        }
        
        // Getters and setters
        public int getCode() {
            return code;
        }
        
        public void setCode(int code) {
            this.code = code;
        }
        
        public String getMessage() {
            return message;
        }
        
        public void setMessage(String message) {
            this.message = message;
        }
        
        public Object getData() {
            return data;
        }
        
        public void setData(Object data) {
            this.data = data;
        }
    }
}