package com.agriculture.notification.service;

import com.agriculture.notification.entity.FarmAlertMessage;
import com.agriculture.notification.mq.FarmAlertMessageProducer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.mail.MessagingException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 农田异常预警邮件服务测试类
 * 用于测试农田异常预警邮件发送功能
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class FarmAlertEmailServiceTest {
    
    private static final Logger logger = LoggerFactory.getLogger(FarmAlertEmailServiceTest.class);
    
    @Autowired
    private FarmAlertEmailService farmAlertEmailService;
    
    @Autowired
    private FarmAlertMessageProducer farmAlertMessageProducer;
    
    /**
     * 测试直接发送农田异常预警邮件
     */
    @Test
    public void testSendFarmAlertEmail() throws MessagingException {
        logger.info("开始测试直接发送农田异常预警邮件");
        
        String userEmail = "test@example.com"; // 测试邮箱
        String alertType = "干旱预警";        
        String farmName = "试验田A区";        
        String alertContent = "根据监测数据，试验田A区土壤湿度已低于预警阈值\n" +
                              "当前湿度: 15%\n" +
                              "建议: 立即进行灌溉，保持土壤湿度在25%-35%之间";        
        String alertTime = LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        
        farmAlertEmailService.sendFarmAlertEmail(userEmail, alertType, farmName, alertContent, alertTime);
        
        logger.info("农田异常预警邮件发送测试完成");
    }
    
    /**
     * 测试通过消息队列发送农田异常预警邮件
     */
    @Test
    public void testSendFarmAlertViaMQ() {
        logger.info("开始测试通过消息队列发送农田异常预警邮件");
        
        FarmAlertMessage alertMessage = new FarmAlertMessage();
        alertMessage.setUserEmail("test@example.com"); // 测试邮箱
        alertMessage.setAlertType("病虫害预警");
        alertMessage.setFarmName("试验田B区");
        alertMessage.setFarmLocation("北纬36.5度，东经114.3度");
        alertMessage.setAlertContent("监测到试验田B区出现蚜虫聚集现象\n" +
                                    "当前受灾程度: 中等\n" +
                                    "建议: 及时喷洒低毒农药，每5-7天一次，连续2-3次");
        alertMessage.setAlertTime(LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        alertMessage.setAlertLevel("中等");
        alertMessage.setSuggestedActions("喷洒吡虫啉1000倍液");
        
        farmAlertMessageProducer.sendFarmAlertMessage(alertMessage);
        
        logger.info("通过消息队列发送农田异常预警邮件测试完成");
        // 等待消息处理完成
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}