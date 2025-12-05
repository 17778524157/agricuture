# Canal连接问题排查指南

## 问题描述
```
CanalClientException: java.io.IOException: Connection reset by peer
```

**错误含义**: Canal客户端与Canal Server之间的TCP连接被意外断开

---

## 快速解决方案

### 1. 检查Canal Server状态

**检查Canal Server是否运行**:
```bash
# 检查Canal进程
ps -ef | grep canal

# 检查Canal Server日志
tail -f canal-server/logs/canal/canal.log
tail -f canal-server/logs/example/example.log
```

**启动Canal Server**:
```bash
cd canal-server
sh bin/startup.sh
```

### 2. 检查网络连接

**测试连接**:
```bash
# 测试Canal Server端口是否可访问
telnet 192.168.10.128 11111

# 或使用nc命令
nc -zv 192.168.10.128 11111
```

### 3. 查看服务状态

**通过监控接口查看**:
```bash
# 获取Canal同步状态
curl http://localhost:8081/api/canal/monitor/status
```

**返回示例**:
```json
{
  "code": 200,
  "data": {
    "running": true,
    "connected": false,
    "reconnectAttempts": 3,
    "statusMessage": "Canal连接断开，正在尝试重连"
  }
}
```

### 4. 重启同步服务（如果需要）

```bash
# 停止同步
curl -X POST http://localhost:8081/api/canal/monitor/stop

# 启动同步
curl -X POST http://localhost:8081/api/canal/monitor/start
```

---

## 优化后的功能特性

### 1. 自动重连机制

**配置参数** (`application.yml`):
```yaml
canal:
  reconnect:
    max-attempts: 5      # 最大重连尝试次数
    interval: 5000       # 重连间隔时间（毫秒）
```

**重连策略**:
- 检测到连接断开时自动触发重连
- 最多尝试5次重连
- 每次重连间隔5秒
- 达到重连上限后等待10秒，重置重试计数
- 在后台持续尝试重连，不影响服务运行

### 2. 连接状态管理

**状态标识**:
- `running`: 同步服务是否运行
- `connected`: Canal连接是否正常
- `reconnectAttempts`: 当前重连尝试次数

**状态转换**:
```
初始状态 → 尝试连接 → 连接成功 → 正常运行
                   ↓
                连接失败 → 自动重连 → 连接成功
                   ↓                    ↑
              达到重连上限 → 重置计数 ───┘
```

### 3. 异常分类处理

**Canal客户端异常**:
- 检测到 `CanalClientException`
- 自动标记为未连接状态
- 触发重连机制
- 特殊处理 `Connection reset` 错误

**其他异常**:
- 记录错误日志
- 等待5秒后继续
- 不影响重连机制

### 4. 监控接口

**可用接口**:
- `GET /api/canal/monitor/status` - 查看同步状态
- `POST /api/canal/monitor/start` - 启动同步服务
- `POST /api/canal/monitor/stop` - 停止同步服务

---

## 常见问题诊断

### 问题1: Canal Server未启动

**症状**:
```
Canal连接失败: Connection refused
reconnectAttempts 持续增加
```

**解决方案**:
```bash
# 1. 启动Canal Server
cd canal-server
sh bin/startup.sh

# 2. 检查启动日志
tail -f logs/canal/canal.log

# 3. 确认端口监听
netstat -lntp | grep 11111
```

### 问题2: 网络防火墙阻止

**症状**:
```
Canal连接失败: Connection timed out
长时间无法连接
```

**解决方案**:
```bash
# 1. 检查防火墙规则
sudo iptables -L -n | grep 11111

# 2. 开放端口（如需要）
sudo firewall-cmd --zone=public --add-port=11111/tcp --permanent
sudo firewall-cmd --reload

# 3. 测试连接
telnet 192.168.10.128 11111
```

### 问题3: Canal配置错误

**症状**:
```
Canal连接失败: Authentication failed
无法订阅数据
```

**解决方案**:
1. 检查 `application.yml` 中的Canal配置
```yaml
canal:
  server:
    host: 192.168.10.128  # 确保IP正确
    port: 11111           # 确保端口正确
  destination: example    # 确保destination存在
  username: canal         # 确保用户名正确
  password: canal         # 确保密码正确
```

2. 检查Canal Server的 `instance.properties`:
```properties
canal.instance.mysql.slaveId=1234
canal.instance.master.address=127.0.0.1:3306
canal.instance.dbUsername=canal
canal.instance.dbPassword=canal
```

### 问题4: MySQL Binlog未开启

**症状**:
```
Canal连接成功但无法获取数据
日志显示 binlog is not open
```

**解决方案**:
```bash
# 1. 检查MySQL binlog状态
mysql -u root -p
mysql> SHOW VARIABLES LIKE 'log_bin';

# 2. 开启binlog（编辑my.cnf）
[mysqld]
log-bin=mysql-bin
binlog-format=ROW
server-id=1

# 3. 重启MySQL
sudo systemctl restart mysqld

# 4. 验证
mysql> SHOW VARIABLES LIKE 'log_bin';
mysql> SHOW MASTER STATUS;
```

### 问题5: 连接频繁中断

**症状**:
```
Connection reset by peer 频繁出现
reconnectAttempts 反复重置
```

**可能原因**:
1. Canal Server负载过高
2. 网络不稳定
3. MySQL主库压力大
4. Binlog文件过大

**解决方案**:
```yaml
# 优化Canal配置
canal:
  batch-size: 500        # 减小批次大小
  timeout: 15000         # 增加超时时间
  reconnect:
    max-attempts: 10     # 增加重试次数
    interval: 3000       # 减少重连间隔
```

---

## 日志分析

### 正常日志
```
[INFO] 正在连接Canal Server...
[INFO] Canal连接成功，开始监听数据变更
[DEBUG] Canal到ES数据同步处理完成，批次ID: 12345, 数量: 10
```

### 连接失败日志
```
[ERROR] Canal连接失败: Connection refused
[WARN] 尝试重新连接Canal Server，第1次尝试
```

### 重连成功日志
```
[WARN] 尝试重新连接Canal Server，第3次尝试
[INFO] 正在连接Canal Server...
[INFO] Canal连接成功，开始监听数据变更
```

### 重连达到上限日志
```
[ERROR] Canal重连失败次数达到上限(5次)，等待5秒后重置重试计数
[WARN] 尝试重新连接Canal Server，第1次尝试
```

---

## 性能优化建议

### 1. 批次大小优化
```yaml
canal:
  batch-size: 1000  # 根据实际数据量调整
```
- 数据量大：增加batch-size（1000-5000）
- 数据量小：减少batch-size（100-500）

### 2. 超时时间优化
```yaml
canal:
  timeout: 10000  # 毫秒
```
- 网络稳定：减少timeout（5000-10000）
- 网络不稳定：增加timeout（15000-30000）

### 3. 重连策略优化
```yaml
canal:
  reconnect:
    max-attempts: 5   # 重试次数
    interval: 5000    # 重连间隔
```
- 快速恢复：减少interval（2000-3000）
- 避免频繁重试：增加interval（10000-15000）

---

## 监控最佳实践

### 1. 定期检查连接状态
```bash
# 设置定时任务
*/5 * * * * curl http://localhost:8081/api/canal/monitor/status >> /var/log/canal-monitor.log
```

### 2. 配置告警
当 `connected: false` 且 `reconnectAttempts > 3` 时发送告警

### 3. 日志级别调整
```yaml
logging:
  level:
    com.agriculture.user.service.CanalToEsSyncService: DEBUG
```

---

## 应急处理流程

### 步骤1: 确认问题
```bash
# 查看同步状态
curl http://localhost:8081/api/canal/monitor/status
```

### 步骤2: 检查依赖服务
```bash
# 检查Canal Server
ps -ef | grep canal

# 检查MySQL
systemctl status mysqld

# 检查网络
ping 192.168.10.128
telnet 192.168.10.128 11111
```

### 步骤3: 重启服务（按需）
```bash
# 重启Canal Server
cd canal-server
sh bin/stop.sh
sh bin/startup.sh

# 重启User Service
# 或通过API重启同步
curl -X POST http://localhost:8081/api/canal/monitor/stop
sleep 5
curl -X POST http://localhost:8081/api/canal/monitor/start
```

### 步骤4: 验证恢复
```bash
# 查看状态
curl http://localhost:8081/api/canal/monitor/status

# 查看日志
tail -f logs/user-service.log | grep Canal
```

---

## 预防措施

### 1. 稳定的基础设施
- 使用稳定的网络环境
- 确保Canal Server和MySQL的资源充足
- 定期维护和监控

### 2. 合理的配置
- 根据实际情况调整batch-size和timeout
- 设置合适的重连策略
- 开启日志监控

### 3. 容灾方案
- Canal Server主备部署
- 数据同步失败时使用MQ重试机制
- 定期备份数据

---

## 技术支持

如果以上方法都无法解决问题，请联系技术团队并提供：

1. **错误日志**:
   - User Service日志
   - Canal Server日志
   - MySQL错误日志

2. **配置信息**:
   - application.yml配置
   - Canal Server配置
   - MySQL配置

3. **环境信息**:
   - 服务器版本
   - 网络拓扑
   - 数据量统计

---

**文档版本**: v1.0  
**更新日期**: 2025-11-18  
**适用版本**: User Service v1.0
