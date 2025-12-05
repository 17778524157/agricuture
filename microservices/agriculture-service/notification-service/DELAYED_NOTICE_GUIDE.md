# 定时发布公告功能说明

## 功能概述
使用RabbitMQ的死信交换机（Dead Letter Exchange）和延迟队列实现公告的定时发布功能。支持两种发布方式：
1. **延迟发布**：设置延迟N分钟后发布
2. **指定时间发布**：设置具体的发布日期和时间

## 技术实现

### 1. RabbitMQ延迟队列原理
- **延迟队列（delayed.notice.queue）**：消息先进入此队列，设置TTL（Time To Live）
- **死信交换机（delayed.notice.dlx.exchange）**：消息过期后转发到此交换机
- **死信队列（delayed.notice.dlx.queue）**：消费者监听此队列，接收过期消息并发布公告

### 2. 核心组件

#### 配置类
- `DelayedNoticeQueueConfig.java`：配置延迟队列、死信交换机和死信队列

#### 服务类
- `DelayedNoticeService.java`：定时发布服务接口
- `DelayedNoticeServiceImpl.java`：发送延迟消息到RabbitMQ
- `DelayedNoticeConsumer.java`：消费死信队列消息，执行实际发布

#### 控制器
- `DelayedNoticeController.java`：提供REST API接口

#### 实体类
- `DelayedNoticeMessage.java`：延迟发布消息实体

### 3. API接口

#### 创建延迟发布任务（按延迟时间）
```
POST /api/notification/delayed/create
Content-Type: application/json

{
  "title": "公告标题",
  "content": "公告内容",
  "user": "发布人",
  "img": "图片URL",
  "delayMinutes": 30
}
```

#### 创建定时发布任务（按指定时间）
```
POST /api/notification/delayed/create-at-time
Content-Type: application/json

{
  "title": "公告标题",
  "content": "公告内容",
  "user": "发布人",
  "img": "图片URL",
  "scheduledPublishTime": 1699000000000  // 时间戳（毫秒）
}
```

#### 取消定时发布任务
```
POST /api/notification/delayed/cancel/{noticeId}
```

## 前端功能

### 使用方式
1. 在公告管理页面点击"定时发布"按钮
2. 填写公告信息（标题、内容、发布人等）
3. 选择发布方式：
   - **延迟发布**：设置延迟分钟数（1-10080分钟，即最多7天）
   - **指定时间发布**：选择具体的发布日期和时间
4. 点击"确定"创建定时发布任务

### 前端页面
- 文件：`vue/src/views/Notice.vue`
- 新增功能：
  - 定时发布按钮
  - 定时发布对话框
  - 发布方式选择（延迟/指定时间）
  - 时间选择器

## 使用示例

### 示例1：30分钟后发布公告
```javascript
// 前端调用
this.request.post("/api/notification/delayed/create", {
  title: "系统维护通知",
  content: "系统将于30分钟后进行维护，请提前保存工作。",
  user: "管理员",
  delayMinutes: 30
})
```

### 示例2：明天上午9点发布公告
```javascript
// 前端调用（时间戳）
const tomorrow9am = new Date().setHours(9, 0, 0, 0) + 24 * 60 * 60 * 1000;
this.request.post("/api/notification/delayed/create-at-time", {
  title: "重要通知",
  content: "明天上午9点发布重要通知。",
  user: "管理员",
  scheduledPublishTime: tomorrow9am
})
```

## 注意事项

1. **RabbitMQ配置**：
   - 确保RabbitMQ服务正常运行（默认地址：192.168.10.128:5672）
   - 管理界面：http://192.168.10.128:15672/
   - 用户名：admin，密码：123456

2. **时间限制**：
   - 延迟发布：1分钟 - 10080分钟（7天）
   - 指定时间发布：不能选择过去的时间

3. **消息持久化**：
   - 所有队列都已设置为持久化，确保消息不丢失
   - 建议定期检查RabbitMQ队列状态

4. **性能考虑**：
   - 大量延迟消息可能占用RabbitMQ内存
   - 建议单个任务的延迟时间不超过7天

5. **取消任务**：
   - 由于RabbitMQ的限制，已发送的消息无法直接从队列中取消
   - 取消功能需要在消费时判断（可通过数据库标记实现）

## 数据库要求

确保公告表（notice）存在以下字段：
- id：主键
- name：标题
- content：内容
- user：发布人
- img：图片URL
- time：发布时间

## 测试步骤

1. **启动服务**：
   - 确保RabbitMQ服务运行
   - 启动notification-service微服务

2. **测试延迟发布**：
   - 创建30分钟延迟的公告
   - 等待30分钟后检查公告是否自动发布

3. **测试指定时间发布**：
   - 创建明天上午9点的定时发布任务
   - 等待到指定时间检查公告是否自动发布

4. **查看日志**：
   - 检查微服务日志，确认消息发送和接收正常
   - 检查RabbitMQ管理界面，查看队列状态

## 故障排查

1. **消息未发送**：
   - 检查RabbitMQ连接配置
   - 查看服务日志中的错误信息

2. **消息未消费**：
   - 检查DelayedNoticeConsumer是否正常运行
   - 查看RabbitMQ管理界面中的队列消息数量

3. **时间不准确**：
   - 确保服务器时间正确
   - 检查时区设置

4. **公告未发布**：
   - 检查数据库连接
   - 查看NotificationService是否正常工作
