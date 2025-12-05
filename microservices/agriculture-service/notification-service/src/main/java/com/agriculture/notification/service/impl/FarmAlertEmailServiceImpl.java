package com.agriculture.notification.service.impl;

import com.agriculture.notification.service.EmailService;
import com.agriculture.notification.service.FarmAlertEmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 农田异常预警邮件服务实现类
 * 实现农田异常预警邮件的发送逻辑
 */
@Service
public class FarmAlertEmailServiceImpl implements FarmAlertEmailService {

    private static final Logger logger = LoggerFactory.getLogger(FarmAlertEmailServiceImpl.class);

    @Autowired
    private EmailService emailService;

    /**
     * 发送农田异常预警邮件
     */
    @Override
    public void sendFarmAlertEmail(String userEmail, String alertType, String farmName, 
                                  String alertContent, String alertTime) throws MessagingException {
        String subject = "【紧急预警】" + farmName + " - " + alertType;
        String htmlContent = generateAlertHtmlContent(farmName, alertType, alertContent, alertTime);
        
        emailService.sendHtmlMail(userEmail, subject, htmlContent);
    }

    /**
     * 发送批量农田异常预警邮件
     * 使用异步方式发送，避免阻塞主线程
     */
    @Override
    public void sendBatchFarmAlertEmail(String[] userEmails, String alertType, String farmName, 
                                      String alertContent, String alertTime) {
        String subject = "【紧急预警】" + farmName + " - " + alertType;
        String htmlContent = generateAlertHtmlContent(farmName, alertType, alertContent, alertTime);
        
        for (String email : userEmails) {
            CompletableFuture.runAsync(() -> {
                try {
                    emailService.sendHtmlMail(email, subject, htmlContent);
                } catch (MessagingException e) {
                    logger.error("批量发送预警邮件失败，收件人: {}", email, e);
                }
            });
        }
    }

    /**
     * 发送带图表的农田异常预警邮件
     */
    @Override
    public void sendFarmAlertEmailWithChart(String userEmail, String alertType, String farmName, 
                                          String alertContent, String alertTime, String chartImagePath) throws MessagingException {
        String subject = "【紧急预警】" + farmName + " - " + alertType;
        String htmlContent = generateAlertHtmlContent(farmName, alertType, alertContent, alertTime);
        
        emailService.sendAttachmentsMail(userEmail, subject, htmlContent, chartImagePath);
    }

    /**
     * 生成预警邮件的HTML内容
     */
    private String generateAlertHtmlContent(String farmName, String alertType, String alertContent, String alertTime) {
        StringBuilder htmlBuilder = new StringBuilder();
        
        htmlBuilder.append("<!DOCTYPE html>")
                 .append("<html>")
                 .append("<head>")
                 .append("<meta charset='UTF-8'>")
                 .append("<title>农田异常预警</title>")
                 .append("<style>")
                 .append("body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }")
                 .append(".container { max-width: 800px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 5px; }")
                 .append(".alert-header { background-color: #f44336; color: white; padding: 10px; text-align: center; border-radius: 5px 5px 0 0; }")
                 .append(".alert-content { padding: 20px; background-color: #fff3cd; }")
                 .append(".alert-info { background-color: white; padding: 15px; border-radius: 5px; margin: 10px 0; }")
                 .append(".alert-footer { text-align: center; color: #666; margin-top: 20px; font-size: 12px; }")
                 .append("</style>")
                 .append("</head>")
                 .append("<body>")
                 .append("<div class='container'>")
                 .append("<div class='alert-header'><h2>农田异常预警通知</h2></div>")
                 .append("<div class='alert-content'>")
                 .append("<div class='alert-info'><strong>农田名称：</strong>").append(farmName).append("</div>")
                 .append("<div class='alert-info'><strong>预警类型：</strong>").append(alertType).append("</div>")
                 .append("<div class='alert-info'><strong>预警时间：</strong>").append(alertTime).append("</div>")
                 .append("<div class='alert-info'><strong>预警详情：</strong><br>").append(alertContent.replace("\n", "<br>")).append("</div>")
                 .append("</div>")
                 .append("<div class='alert-footer'>")
                 .append("此邮件为系统自动发送，请及时处理农田异常情况。\n")
                 .append("农业算枣系统 - 农田智能监控平台")
                 .append("</div>")
                 .append("</div>")
                 .append("</body>")
                 .append("</html>");
        
        return htmlBuilder.toString();
    }
}