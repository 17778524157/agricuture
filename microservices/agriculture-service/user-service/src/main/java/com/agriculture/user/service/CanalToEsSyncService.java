package com.agriculture.user.service;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import com.agriculture.user.config.RabbitMQRetryConfig;
import com.agriculture.user.dto.EsSyncMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Canal到ES数据同步服务
 * 监听MySQL binlog，同步数据到ES
 */
@Slf4j
@Service
public class CanalToEsSyncService implements CommandLineRunner {
    
    @Autowired
    private CanalConnector canalConnector;
    
    @Autowired
    private ElasticsearchService elasticsearchService;
    
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    @Value("${canal.batch-size:1000}")
    private int batchSize;
    
    @Value("${canal.timeout:10000}")
    private Long timeout;
    
    @Value("${canal.sync.enabled:true}")
    private boolean syncEnabled;
    
    @Value("${canal.reconnect.max-attempts:5}")
    private int maxReconnectAttempts;
    
    @Value("${canal.reconnect.interval:5000}")
    private long reconnectInterval;
    
    private volatile boolean running = false;
    private volatile boolean connected = false;
    private int reconnectAttempts = 0;
    
    // 需要同步的表
    private static final Set<String> SYNC_TABLES = new HashSet<>(Arrays.asList(
        "sys_user"
    ));
    
    @Override
    public void run(String... args) throws Exception {
        if (syncEnabled) {
            startCanalSync();
        } else {
            log.info("Canal到ES同步服务已禁用");
        }
    }
    
    /**
     * 启动Canal数据同步
     */
    public void startCanalSync() {
        running = true;
        log.info("准备启动Canal到ES数据同步服务");
        
        // 启动同步线程
        Thread syncThread = new Thread(this::syncData);
        syncThread.setName("canal-es-sync-thread");
        syncThread.setDaemon(true);
        syncThread.start();
    }
    
    /**
     * 连接到Canal Server
     */
    private boolean connectToCanal() {
        try {
            log.info("正在连接Canal Server...");
            canalConnector.connect();
            canalConnector.subscribe("suanzao\\.sys_user");
            canalConnector.rollback();
            connected = true;
            reconnectAttempts = 0;
            log.info("Canal连接成功，开始监听数据变更");
            return true;
        } catch (Exception e) {
            connected = false;
            log.error("Canal连接失败: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 重连到Canal Server
     */
    private boolean reconnectToCanal() {
        reconnectAttempts++;
        log.warn("尝试重新连接Canal Server，第{}次尝试", reconnectAttempts);
        
        try {
            // 先断开旧连接
            try {
                canalConnector.disconnect();
            } catch (Exception e) {
                log.debug("断开旧连接时发生异常（可忽略）: {}", e.getMessage());
            }
            
            // 等待一段时间后重连
            Thread.sleep(reconnectInterval);
            
            // 尝试重新连接
            return connectToCanal();
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("重连过程被中断");
            return false;
        }
    }
    
    /**
     * 数据同步主循环
     */
    private void syncData() {
        // 首次连接
        if (!connectToCanal()) {
            log.error("初始连接Canal失败，同步服务将在后台持续尝试重连");
        }
        
        while (running) {
            try {
                // 检查连接状态
                if (!connected) {
                    // 尝试重连
                    if (reconnectAttempts >= maxReconnectAttempts) {
                        log.error("Canal重连失败次数达到上限({}次)，等待{}秒后重置重试计数", 
                            maxReconnectAttempts, reconnectInterval / 1000);
                        Thread.sleep(reconnectInterval * 2);
                        reconnectAttempts = 0;
                    }
                    
                    if (!reconnectToCanal()) {
                        continue; // 重连失败，继续下一轮循环
                    }
                }
                
                // 获取指定数量的数据
                Message message = canalConnector.getWithoutAck(batchSize, timeout, TimeUnit.MILLISECONDS);
                long batchId = message.getId();
                int size = message.getEntries().size();
                
                if (batchId == -1 || size == 0) {
                    // 没有数据，等待一段时间
                    Thread.sleep(1000);
                    continue;
                }
                
                // 处理数据变更
                processEntries(message.getEntries());
                
                // 确认消息
                canalConnector.ack(batchId);
                log.debug("Canal到ES数据同步处理完成，批次ID: {}, 数量: {}", batchId, size);
                
            } catch (com.alibaba.otter.canal.protocol.exception.CanalClientException e) {
                // Canal连接异常
                connected = false;
                log.error("Canal连接异常，标记为未连接状态: {}", e.getMessage());
                
                // 检查是否是连接重置错误
                if (e.getMessage() != null && e.getMessage().contains("Connection reset")) {
                    log.warn("检测到连接重置，将尝试重新连接");
                }
                
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.info("数据同步线程被中断");
                break;
            } catch (Exception e) {
                log.error("Canal到ES数据同步处理异常（未知错误）", e);
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
        
        log.info("数据同步循环结束");
    }
    
    /**
     * 处理数据变更条目
     */
    private void processEntries(List<CanalEntry.Entry> entries) {
        for (CanalEntry.Entry entry : entries) {
            if (entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONBEGIN ||
                entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONEND) {
                continue;
            }
            
            CanalEntry.RowChange rowChange;
            try {
                rowChange = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
            } catch (Exception e) {
                log.error("解析Canal数据失败", e);
                continue;
            }
            
            CanalEntry.EventType eventType = rowChange.getEventType();
            String tableName = entry.getHeader().getTableName();
            String schemaName = entry.getHeader().getSchemaName();
            
            // 只处理需要同步的表
            if (!SYNC_TABLES.contains(tableName)) {
                continue;
            }
            
            log.debug("Canal数据变更: 数据库={}, 表={}, 事件类型={}", schemaName, tableName, eventType);
            
            // 处理用户数据变更
            if ("sys_user".equals(tableName)) {
                processUserDataChange(eventType, rowChange, schemaName);
            }
        }
    }
    
    /**
     * 处理用户数据变更
     */
    private void processUserDataChange(CanalEntry.EventType eventType, 
                                       CanalEntry.RowChange rowChange, 
                                       String schemaName) {
        for (CanalEntry.RowData rowData : rowChange.getRowDatasList()) {
            try {
                Map<String, Object> dataMap = new HashMap<>();
                String userId = null;
                
                if (eventType == CanalEntry.EventType.DELETE) {
                    // 删除操作，使用变更前的数据
                    userId = getColumnValue(rowData.getBeforeColumnsList(), "id");
                    for (CanalEntry.Column column : rowData.getBeforeColumnsList()) {
                        dataMap.put(column.getName(), column.getValue());
                    }
                } else {
                    // INSERT或UPDATE操作，使用变更后的数据
                    userId = getColumnValue(rowData.getAfterColumnsList(), "id");
                    for (CanalEntry.Column column : rowData.getAfterColumnsList()) {
                        dataMap.put(column.getName(), column.getValue());
                    }
                }
                
                if (userId == null) {
                    log.warn("用户ID为空，跳过同步");
                    continue;
                }
                
                // 构建同步消息
                EsSyncMessage syncMessage = new EsSyncMessage();
                syncMessage.setOperationType(eventType.name());
                syncMessage.setTableName("sys_user");
                syncMessage.setDatabaseName(schemaName);
                syncMessage.setId(userId);
                syncMessage.setData(dataMap);
                
                // 尝试直接同步到ES
                try {
                    syncToEs(syncMessage);
                    log.info("用户数据同步到ES成功: userId={}, operation={}", userId, eventType);
                } catch (Exception e) {
                    log.error("用户数据同步到ES失败，发送到MQ重试队列: userId={}", userId, e);
                    // 同步失败，发送到MQ重试队列
                    sendToRetryQueue(syncMessage);
                }
                
            } catch (Exception e) {
                log.error("处理用户数据变更失败", e);
            }
        }
    }
    
    /**
     * 同步数据到ES
     */
    private void syncToEs(EsSyncMessage syncMessage) {
        String operationType = syncMessage.getOperationType();
        Map<String, Object> data = syncMessage.getData();
        String userId = syncMessage.getId();
        
        switch (operationType) {
            case "INSERT":
            case "UPDATE":
                elasticsearchService.syncUserToEs(data);
                break;
            case "DELETE":
                elasticsearchService.deleteUserFromEs(userId);
                break;
            default:
                log.warn("未知的操作类型: {}", operationType);
        }
    }
    
    /**
     * 发送到重试队列
     */
    private void sendToRetryQueue(EsSyncMessage syncMessage) {
        try {
            rabbitTemplate.convertAndSend(
                RabbitMQRetryConfig.ES_SYNC_QUEUE,
                syncMessage
            );
            log.info("同步消息已发送到重试队列: userId={}, operation={}", 
                syncMessage.getId(), syncMessage.getOperationType());
        } catch (Exception e) {
            log.error("发送同步消息到重试队列失败", e);
        }
    }
    
    /**
     * 获取列值
     */
    private String getColumnValue(List<CanalEntry.Column> columns, String columnName) {
        for (CanalEntry.Column column : columns) {
            if (columnName.equals(column.getName())) {
                return column.getValue();
            }
        }
        return null;
    }
    
    /**
     * 停止Canal同步
     */
    public void stopCanalSync() {
        running = false;
        connected = false;
        
        if (canalConnector != null) {
            try {
                canalConnector.disconnect();
                log.info("Canal连接已断开");
            } catch (Exception e) {
                log.warn("断开Canal连接时发生异常: {}", e.getMessage());
            }
        }
        
        log.info("Canal到ES数据同步服务已停止");
    }
    
    /**
     * 获取连接状态
     */
    public boolean isConnected() {
        return connected;
    }
    
    /**
     * 获取运行状态
     */
    public boolean isRunning() {
        return running;
    }
    
    /**
     * 获取重连尝试次数
     */
    public int getReconnectAttempts() {
        return reconnectAttempts;
    }
}













