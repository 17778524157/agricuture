package com.agriculture.user.controller;

import com.agriculture.common.result.Result;
import com.agriculture.user.service.CanalToEsSyncService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Canal数据同步监控控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/canal/monitor")
@Api(tags = "Canal同步监控")
public class CanalSyncMonitorController {

    @Autowired
    private CanalToEsSyncService canalToEsSyncService;

    /**
     * 获取Canal同步状态
     */
    @GetMapping("/status")
    @ApiOperation("获取Canal同步状态")
    public Result<Map<String, Object>> getSyncStatus() {
        try {
            Map<String, Object> status = new HashMap<>();
            status.put("running", canalToEsSyncService.isRunning());
            status.put("connected", canalToEsSyncService.isConnected());
            status.put("reconnectAttempts", canalToEsSyncService.getReconnectAttempts());
            
            String statusMessage;
            if (!canalToEsSyncService.isRunning()) {
                statusMessage = "同步服务未运行";
            } else if (!canalToEsSyncService.isConnected()) {
                statusMessage = "Canal连接断开，正在尝试重连";
            } else {
                statusMessage = "正常运行中";
            }
            status.put("statusMessage", statusMessage);
            
            log.info("查询Canal同步状态: {}", statusMessage);
            return Result.success(status);
        } catch (Exception e) {
            log.error("查询Canal同步状态失败", e);
            return Result.fail("查询失败");
        }
    }

    /**
     * 启动Canal同步
     */
    @PostMapping("/start")
    @ApiOperation("启动Canal同步")
    public Result startSync() {
        try {
            if (canalToEsSyncService.isRunning()) {
                return Result.fail("同步服务已在运行中");
            }
            
            canalToEsSyncService.startCanalSync();
            log.info("Canal同步服务已启动");
            return Result.success("启动成功");
        } catch (Exception e) {
            log.error("启动Canal同步服务失败", e);
            return Result.fail("启动失败: " + e.getMessage());
        }
    }

    /**
     * 停止Canal同步
     */
    @PostMapping("/stop")
    @ApiOperation("停止Canal同步")
    public Result stopSync() {
        try {
            if (!canalToEsSyncService.isRunning()) {
                return Result.fail("同步服务未运行");
            }
            
            canalToEsSyncService.stopCanalSync();
            log.info("Canal同步服务已停止");
            return Result.success("停止成功");
        } catch (Exception e) {
            log.error("停止Canal同步服务失败", e);
            return Result.fail("停止失败: " + e.getMessage());
        }
    }
}
