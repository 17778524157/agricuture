package com.agriculture.notification.service.impl;

import com.agriculture.notification.service.EmailService;
import freemarker.template.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.Map;

/**
 * 邮件服务实现类
 * 基于SpringMail实现邮件发送功能
 */
@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private Configuration configuration;

    @Value("${spring.mail.username}")
    private String from;

    /**
     * 发送简单文本邮件
     */
    @Override
    public void sendSimpleMail(String to, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);

        try {
            mailSender.send(message);
            logger.info("简单邮件发送成功，收件人: {}", to);
        } catch (Exception e) {
            logger.error("发送简单邮件时发生异常: {}", e.getMessage(), e);
        }
    }

    /**
     * 发送HTML格式邮件
     */
    @Override
    public void sendHtmlMail(String to, String subject, String content) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(content, true);

        try {
            mailSender.send(message);
            logger.info("HTML邮件发送成功，收件人: {}", to);
        } catch (Exception e) {
            logger.error("发送HTML邮件时发生异常: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 发送带附件的邮件
 * 该方法用于发送包含附件的电子邮件，支持HTML格式内容
 *
 * @param to 收件人邮箱地址
 * @param subject 邮件主题
 * @param content 邮件正文内容（支持HTML格式）
 * @param filePath 附件的文件路径
 * @throws MessagingException 当邮件发送过程中出现MessagingException时抛出
     */
    @Override
    public void sendAttachmentsMail(String to, String subject, String content, String filePath) throws MessagingException {
    // 创建MimeMessage对象，用于构建复杂邮件消息
        MimeMessage message = mailSender.createMimeMessage();
    // 创建MimeMessageHelper对象，并设置支持多部分消息（true表示支持附件）
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
    // 设置发件人
        helper.setFrom(from);
    // 设置收件人
        helper.setTo(to);
    // 设置邮件主题
        helper.setSubject(subject);
    // 设置邮件正文内容，并启用HTML格式（true表示HTML格式）
        helper.setText(content, true);

    // 创建文件系统资源对象，指向附件文件
        FileSystemResource file = new FileSystemResource(new File(filePath));
    // 从文件路径中提取文件名
        String fileName = filePath.substring(filePath.lastIndexOf(File.separator) + 1);
    // 添加附件到邮件中
        helper.addAttachment(fileName, file);

        try {
        // 发送邮件
            mailSender.send(message);
        // 记录邮件发送成功的日志
            logger.info("带附件的邮件发送成功，收件人: {}", to);
        } catch (Exception e) {
        // 记录邮件发送失败的日志，包括异常堆栈信息
            logger.error("发送带附件的邮件时发生异常: {}", e.getMessage(), e);
        // 重新抛出异常，由调用者处理
            throw e;
        }
    }

    /**
     * 使用模板发送邮件
     */
    @Override
    public void sendTemplateMail(String to, String subject, String templateName, Map<String, Object> params) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject(subject);

        try {
            // 生成模板内容
            String content = FreeMarkerTemplateUtils.processTemplateIntoString(
                    configuration.getTemplate(templateName), params);
            helper.setText(content, true);
            
            mailSender.send(message);
            logger.info("模板邮件发送成功，收件人: {}, 模板: {}", to, templateName);
        } catch (Exception e) {
            logger.error("发送模板邮件时发生异常: {}", e.getMessage(), e);
            throw new MessagingException("模板邮件发送失败", e);
        }
    }
}