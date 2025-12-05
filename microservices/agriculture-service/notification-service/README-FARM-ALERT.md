# 农田异常预警通知系统

## 1. 系统概述

农田异常预警通知系统是基于poste.io自建邮局和SpringMail实现的邮件通知服务，用于在农田出现异常情况时（如干旱、病虫害、水灾等）及时向相关管理人员发送邮件预警通知，确保异常情况能够被及时发现和处理。

### 1.1 工作原理

该系统采用微服务架构和消息队列机制，主要工作流程如下：

1. **数据采集层**：通过传感器、监控设备或人工上报获取农田异常数据
2. **预警触发层**：相关业务服务检测到异常后，调用通知服务的API发送预警请求
3. **消息队列层**：通知服务将预警消息发送到RabbitMQ队列，实现系统解耦
4. **消息消费层**：通知服务的消息消费者监听队列，接收预警消息
5. **邮件发送层**：调用SpringMail组件，通过poste.io自建邮局发送预警邮件
6. **通知接收层**：相关管理人员收到邮件预警通知

## 2. 技术栈

- **Spring Boot 2.5.9**：基础框架
- **Spring Cloud 2020.0.5**：微服务框架
- **Spring Mail**：邮件发送组件
- **poste.io**：自建邮件服务器
- **RabbitMQ**：消息队列，用于系统解耦
- **MySQL 8.0**：数据存储
- **Nacos**：服务注册与发现
- **FreeMarker**：邮件模板引擎（可选）

## 3. 配置说明

### 3.1 邮件服务器配置

在`notification-service/src/main/resources/application.yml`文件中配置poste.io邮件服务器信息：

```yaml
spring:
  mail:
    host: localhost  # poste.io服务器地址
    port: 25         # SMTP端口（poste.io默认不加密端口）
    username: alert@yourdomain.com  # 发件人邮箱
    password: 123456  # 发件人密码
    default-encoding: UTF-8
    properties:
      mail.smtp.auth: true
      mail.smtp.starttls.enable: false  # poste.io默认不使用TLS
      mail.smtp.timeout: 5000
```

### 3.2 RabbitMQ配置

在`notification-service/src/main/resources/application.yml`文件中配置RabbitMQ连接信息：

```yaml
spring:
  rabbitmq:
    host: 192.168.10.128  # RabbitMQ服务器地址
    port: 5672       # RabbitMQ默认端口
    username: admin  # 默认用户名
    password: 123456 # 默认密码
    virtual-host: /  # 默认虚拟主机
```

## 4. 系统架构

### 4.1 核心组件

1. **EmailService**：基础邮件服务接口，定义邮件发送的基本方法
2. **FarmAlertEmailService**：农田异常预警邮件服务接口，专门处理农田异常预警邮件
3. **FarmAlertMessageProducer**：消息生产者，负责向RabbitMQ发送预警消息
4. **FarmAlertMessageConsumer**：消息消费者，负责从RabbitMQ接收预警消息并发送邮件
5. **FarmAlertController**：REST API控制器，提供外部调用接口

### 4.2 目录结构

```
notification-service/
├── src/main/java/com/agriculture/notification/
│   ├── config/                   # 配置类
│   │   ├── FarmAlertRabbitMQConfig.java  # 预警消息队列配置
│   │   └── RabbitMQConfig.java           # 通用消息队列配置
│   ├── controller/               # 控制器
│   │   ├── FarmAlertController.java      # 预警通知控制器
│   │   └── NotificationController.java   # 通用通知控制器
│   ├── entity/                   # 实体类
│   │   ├── FarmAlertMessage.java         # 预警消息实体
│   │   └── Notice.java                   # 通知实体
│   ├── mq/                       # 消息队列相关
│   │   ├── FarmAlertMessageConsumer.java # 预警消息消费者
│   │   └── FarmAlertMessageProducer.java # 预警消息生产者
│   ├── service/                  # 服务接口
│   │   ├── EmailService.java             # 邮件服务接口
│   │   ├── FarmAlertEmailService.java    # 预警邮件服务接口
│   │   └── NotificationService.java      # 通知服务接口
│   └── service/impl/             # 服务实现
│       ├── EmailServiceImpl.java         # 邮件服务实现
│       ├── FarmAlertEmailServiceImpl.java # 预警邮件服务实现
│       └── NotificationServiceImpl.java   # 通知服务实现
└── src/main/resources/
    └── application.yml           # 配置文件
```

## 5. 使用指南

### 5.1 发送预警通知API

系统提供REST API接口，其他服务可以通过HTTP请求发送预警通知：

#### 5.1.1 发送单个预警通知

**请求URL**：`/api/farm-alert/send`

**请求方法**：POST

**请求体**：

```json
{
  "alertType": "干旱预警",
  "farmName": "试验田A区",
  "farmLocation": "北纬36.5度，东经114.3度",
  "alertContent": "根据监测数据，试验田A区土壤湿度已低于预警阈值\n当前湿度: 15%\n建议: 立即进行灌溉",
  "userEmail": "manager@example.com",
  "alertLevel": "高",
  "suggestedActions": "立即灌溉，保持土壤湿度在25%-35%之间"
}
```

**响应**：

```json
{
  "code": 200,
  "message": "success",
  "data": "农田异常预警通知已发送"
}
```

#### 5.1.2 批量发送预警通知

**请求URL**：`/api/farm-alert/batch-send`

**请求方法**：POST

**请求体**：预警消息对象数组

```json
[
  {
    "alertType": "干旱预警",
    "farmName": "试验田A区",
    "userEmail": "manager1@example.com"
  },
  {
    "alertType": "病虫害预警",
    "farmName": "试验田B区",
    "userEmail": "manager2@example.com"
  }
]
```

### 5.2 测试预警通知

系统提供了测试类`FarmAlertEmailServiceTest`，可以用于测试预警通知功能：

1. 直接测试邮件发送：`testSendFarmAlertEmail()`
2. 测试通过消息队列发送：`testSendFarmAlertViaMQ()`

## 6. 部署注意事项

1. **poste.io服务器配置**：
   - 确保poste.io服务器已正确安装并配置
   - 在poste.io中创建专门的预警通知邮箱账号
   - 配置允许SMTP服务，并根据需要设置安全选项

2. **RabbitMQ配置**：
   - 确保RabbitMQ服务正常运行
   - 创建相应的用户和权限
   - 配置队列持久化，避免消息丢失

3. **微服务集成**：
   - 在Nacos中注册notification-service服务
   - 确保其他微服务能够正确调用预警通知API

4. **安全考虑**：
   - 邮件服务器密码应加密存储或使用配置中心管理
   - API接口应添加适当的权限验证
   - 敏感信息不应包含在邮件内容中

5. **监控与日志**：
   - 配置适当的日志级别，记录邮件发送情况
   - 设置监控告警，及时发现邮件发送失败的情况

## 7. 扩展功能

系统可以根据需要进行以下扩展：

1. **支持多渠道通知**：除邮件外，可集成短信、APP推送等多种通知方式
2. **邮件模板管理**：使用FreeMarker等模板引擎，实现可配置的邮件模板
3. **预警规则配置**：实现可配置的预警规则，灵活定义预警条件和通知方式
4. **通知记录管理**：记录所有通知历史，支持查询和统计
5. **用户订阅管理**：允许用户订阅感兴趣的预警类型和农田区域