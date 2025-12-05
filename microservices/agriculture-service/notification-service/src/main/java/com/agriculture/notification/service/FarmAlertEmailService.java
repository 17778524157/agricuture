package com.agriculture.notification.service;

import javax.mail.MessagingException;
import java.util.Map;

/**
 * 农田异常预警邮件服务接口
 * 专门用于发送农田异常预警邮件通知
 */
public interface FarmAlertEmailService {
    
    /**
     * 发送农田异常预警邮件
     * @param userEmail 接收预警的用户邮箱
     * @param alertType 预警类型（如：干旱、病虫害、水灾等）
     * @param farmName 农田名称
     * @param alertContent 预警详细内容
     * @param alertTime 预警时间
     * @throws MessagingException 邮件发送异常
     */
    void sendFarmAlertEmail(String userEmail, String alertType, String farmName, 
                           String alertContent, String alertTime) throws MessagingException;
    
    /**
     * 发送批量农田异常预警邮件
     * @param userEmails 接收预警的用户邮箱列表
     * @param alertType 预警类型
     * @param farmName 农田名称
     * @param alertContent 预警详细内容
     * @param alertTime 预警时间
     */
    void sendBatchFarmAlertEmail(String[] userEmails, String alertType, String farmName, 
                                String alertContent, String alertTime);
    
    /**
     * 发送带图表的农田异常预警邮件
     * @param userEmail 接收预警的用户邮箱
     * @param alertType 预警类型
     * @param farmName 农田名称
     * @param alertContent 预警详细内容
     * @param alertTime 预警时间
     * @param chartImagePath 图表图片路径
     * @throws MessagingException 邮件发送异常
     */
    void sendFarmAlertEmailWithChart(String userEmail, String alertType, String farmName, 
                                    String alertContent, String alertTime, String chartImagePath) throws MessagingException;
}