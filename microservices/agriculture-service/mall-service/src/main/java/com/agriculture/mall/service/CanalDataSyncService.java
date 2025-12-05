package com.agriculture.mall.service;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Canal数据同步服务
 * 监听MySQL binlog，实现数据同步到Redis缓存
 */
@Service
@Slf4j
public class CanalDataSyncService implements CommandLineRunner {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Value("${canal.server.host:192.168.10.128}")
    private String canalHost;

    @Value("${canal.server.port:11111}")
    private int canalPort;

    @Value("${canal.destination:example}")
    private String destination;

    @Value("${canal.username:}")
    private String username;

    @Value("${canal.password:}")
    private String password;

    @Value("${canal.batch-size:1000}")
    private int batchSize;

    @Value("${canal.timeout:10000}")
    private Long timeout;

    private CanalConnector connector;
    private volatile boolean running = false;

    @Override
    public void run(String... args) throws Exception {
        startCanalSync();
    }

    /**
     * 启动Canal数据同步
     */
    public void startCanalSync() {
        try {
            // 创建Canal连接
            connector = CanalConnectors.newSingleConnector(
                new InetSocketAddress(canalHost, canalPort),
                destination,
                username,
                password
            );

            // 连接Canal
            connector.connect();
            connector.subscribe("suanzao\\.sys_user"); // 订阅suanzao数据库的sys_user表
            connector.subscribe("suanzao\\.mall_product"); // 订阅suanzao数据库的mall_product表
            connector.subscribe("suanzao\\.points_record"); // 订阅suanzao数据库的points_record表
            connector.rollback();

            running = true;
            log.info("Canal数据同步服务启动成功，监听数据库: {}", destination);

            // 启动同步线程
            Thread syncThread = new Thread(this::syncData);
            syncThread.setName("canal-sync-thread");
            syncThread.setDaemon(true);
            syncThread.start();

        } catch (Exception e) {
            log.error("Canal数据同步服务启动失败", e);
        }
    }

    /**
     * 数据同步主循环
     */
    private void syncData() {
        while (running) {
            try {
                // 获取指定数量的数据
                Message message = connector.getWithoutAck(batchSize, timeout, TimeUnit.MILLISECONDS);
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
                connector.ack(batchId);
                log.debug("Canal数据同步处理完成，批次ID: {}, 数量: {}", batchId, size);

            } catch (Exception e) {
                log.error("Canal数据同步处理异常", e);
                try {
                    Thread.sleep(5000); // 异常时等待5秒
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
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

            log.debug("Canal数据变更: 数据库={}, 表={}, 事件类型={}", schemaName, tableName, eventType);

            // 根据表名处理不同的数据同步
            switch (tableName) {
                case "sys_user":
                    processUserDataChange(eventType, rowChange);
                    break;
                case "mall_product":
                    processProductDataChange(eventType, rowChange);
                    break;
                case "points_record":
                    processPointsRecordChange(eventType, rowChange);
                    break;
                default:
                    log.debug("未处理的表: {}", tableName);
                    break;
            }
        }
    }

    /**
     * 处理用户数据变更
     */
    private void processUserDataChange(CanalEntry.EventType eventType, CanalEntry.RowChange rowChange) {
        for (CanalEntry.RowData rowData : rowChange.getRowDatasList()) {
            try {
                if (eventType == CanalEntry.EventType.DELETE) {
                    // 删除用户积分缓存
                    String userId = getColumnValue(rowData.getBeforeColumnsList(), "id");
                    if (userId != null) {
                        String cacheKey = "mall:user:points:" + userId;
                        redisTemplate.delete(cacheKey);
                        log.info("Canal同步删除用户积分缓存: {}", userId);
                    }
                } else if (eventType == CanalEntry.EventType.UPDATE) {
                    // 更新用户积分缓存
                    String userId = getColumnValue(rowData.getAfterColumnsList(), "id");
                    String points = getColumnValue(rowData.getAfterColumnsList(), "points");
                    
                    if (userId != null && points != null) {
                        String cacheKey = "mall:user:points:" + userId;
                        redisTemplate.opsForValue().set(cacheKey, Integer.parseInt(points), 
                            java.time.Duration.ofMinutes(30));
                        log.info("Canal同步更新用户积分缓存: userId={}, points={}", userId, points);
                    }
                } else if (eventType == CanalEntry.EventType.INSERT) {
                    // 新增用户积分缓存
                    String userId = getColumnValue(rowData.getAfterColumnsList(), "id");
                    String points = getColumnValue(rowData.getAfterColumnsList(), "points");
                    
                    if (userId != null && points != null) {
                        String cacheKey = "mall:user:points:" + userId;
                        redisTemplate.opsForValue().set(cacheKey, Integer.parseInt(points), 
                            java.time.Duration.ofMinutes(30));
                        log.info("Canal同步新增用户积分缓存: userId={}, points={}", userId, points);
                    }
                }
            } catch (Exception e) {
                log.error("处理用户数据变更失败", e);
            }
        }
    }

    /**
     * 处理商品数据变更
     */
    private void processProductDataChange(CanalEntry.EventType eventType, CanalEntry.RowChange rowChange) {
        for (CanalEntry.RowData rowData : rowChange.getRowDatasList()) {
            try {
                if (eventType == CanalEntry.EventType.DELETE) {
                    // 删除商品缓存
                    String productId = getColumnValue(rowData.getBeforeColumnsList(), "id");
                    if (productId != null) {
                        String cacheKey = "mall:product:info:" + productId;
                        redisTemplate.delete(cacheKey);
                        log.info("Canal同步删除商品缓存: {}", productId);
                    }
                } else if (eventType == CanalEntry.EventType.UPDATE || eventType == CanalEntry.EventType.INSERT) {
                    // 更新或新增商品缓存
                    String productId = getColumnValue(rowData.getAfterColumnsList(), "id");
                    String name = getColumnValue(rowData.getAfterColumnsList(), "name");
                    String price = getColumnValue(rowData.getAfterColumnsList(), "price");
                    String stock = getColumnValue(rowData.getAfterColumnsList(), "stock");
                    
                    if (productId != null) {
                        // 构建商品信息Map
                        Map<String, Object> productInfo = Map.of(
                            "id", productId,
                            "name", name != null ? name : "",
                            "price", price != null ? price : "0",
                            "stock", stock != null ? stock : "0"
                        );
                        
                        String cacheKey = "mall:product:info:" + productId;
                        redisTemplate.opsForValue().set(cacheKey, productInfo, 
                            java.time.Duration.ofHours(1));
                        log.info("Canal同步更新商品缓存: productId={}, name={}", productId, name);
                    }
                }
                
                // 清除商品列表缓存
                clearProductListCache();
                
            } catch (Exception e) {
                log.error("处理商品数据变更失败", e);
            }
        }
    }

    /**
     * 处理积分记录变更
     */
    private void processPointsRecordChange(CanalEntry.EventType eventType, CanalEntry.RowChange rowChange) {
        for (CanalEntry.RowData rowData : rowChange.getRowDatasList()) {
            try {
                if (eventType == CanalEntry.EventType.INSERT) {
                    // 新增积分记录时，更新用户积分缓存
                    String userId = getColumnValue(rowData.getAfterColumnsList(), "user_id");
                    String points = getColumnValue(rowData.getAfterColumnsList(), "points");
                    
                    if (userId != null && points != null) {
                        String cacheKey = "mall:user:points:" + userId;
                        // 获取当前积分并累加
                        Integer currentPoints = (Integer) redisTemplate.opsForValue().get(cacheKey);
                        if (currentPoints != null) {
                            Integer newPoints = currentPoints + Integer.parseInt(points);
                            redisTemplate.opsForValue().set(cacheKey, newPoints, 
                                java.time.Duration.ofMinutes(30));
                            log.info("Canal同步更新用户积分缓存: userId={}, 增加积分={}, 新积分={}", 
                                userId, points, newPoints);
                        }
                    }
                }
            } catch (Exception e) {
                log.error("处理积分记录变更失败", e);
            }
        }
    }

    /**
     * 清除商品列表缓存
     */
    private void clearProductListCache() {
        try {
            redisTemplate.delete(redisTemplate.keys("mall:product:list:*"));
            log.debug("Canal同步清除商品列表缓存");
        } catch (Exception e) {
            log.error("清除商品列表缓存失败", e);
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
        if (connector != null) {
            connector.disconnect();
        }
        log.info("Canal数据同步服务已停止");
    }

    /**
     * 检查是否运行中
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * 手动同步用户积分到缓存
     * 当Canal不可用时，可以手动调用此方法
     */
    public void syncUserPointsToCache(Integer userId, Integer points) {
        try {
            String cacheKey = "mall:user:points:" + userId;
            redisTemplate.opsForValue().set(cacheKey, points, java.time.Duration.ofMinutes(30));
            log.info("手动同步用户积分到缓存: userId={}, points={}", userId, points);
        } catch (Exception e) {
            log.error("手动同步用户积分失败", e);
        }
    }

    /**
     * 手动同步商品信息到缓存
     */
    public void syncProductToCache(Integer productId, String name, String price, String stock) {
        try {
            String cacheKey = "mall:product:info:" + productId;
            java.util.Map<String, Object> productInfo = java.util.Map.of(
                "id", productId.toString(),
                "name", name != null ? name : "",
                "price", price != null ? price : "0",
                "stock", stock != null ? stock : "0"
            );
            redisTemplate.opsForValue().set(cacheKey, productInfo, java.time.Duration.ofHours(1));
            log.info("手动同步商品信息到缓存: productId={}, name={}", productId, name);
        } catch (Exception e) {
            log.error("手动同步商品信息失败", e);
        }
    }
}
