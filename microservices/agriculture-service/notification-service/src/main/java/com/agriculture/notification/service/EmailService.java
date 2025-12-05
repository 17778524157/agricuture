package com.agriculture.notification.service;

import javax.mail.MessagingException;
import java.util.Map;

/**
 * 邮件服务接口
 * 用于发送农田异常预警邮件通知
 */
public interface EmailService {
    
    /**
     * 发送简单文本邮件
     * @param to 收件人邮箱
     * @param subject 邮件主题
     * @param content 邮件内容
     */
    void sendSimpleMail(String to, String subject, String content);
    
    /**
     * 发送HTML格式邮件
     * @param to 收件人邮箱
     * @param subject 邮件主题
     * @param content HTML内容
     * @throws MessagingException 邮件发送异常
     */
    void sendHtmlMail(String to, String subject, String content) throws MessagingException;
    
    /**
     * 发送带附件的邮件
     * @param to 收件人邮箱
     * @param subject 邮件主题
     * @param content 邮件内容
     * @param filePath 附件路径
     * @throws MessagingException 邮件发送异常
     */
    void sendAttachmentsMail(String to, String subject, String content, String filePath) throws MessagingException;
    
    /**
     * 使用模板发送邮件
     * @param to 收件人邮箱
     * @param subject 邮件主题
     * @param templateName 模板名称
     * @param params 模板参数
     * @throws MessagingException 邮件发送异常
     */
    void sendTemplateMail(String to, String subject, String templateName, Map<String, Object> params) throws MessagingException;
}