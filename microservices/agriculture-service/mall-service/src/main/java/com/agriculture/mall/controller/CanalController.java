package com.agriculture.mall.controller;

import com.agriculture.common.result.Result;
import com.agriculture.mall.service.CanalDataSyncService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Canal数据同步控制器
 */
@RestController
@RequestMapping("/api/mall/canal")
@Api(tags = "Canal数据同步管理")
@Slf4j
public class CanalController {

    @Autowired
    private CanalDataSyncService canalDataSyncService;

    /**
     * 启动Canal数据同步
     */
    @PostMapping("/start")
    @ApiOperation("启动Canal数据同步")
    public Result<String> startCanalSync() {
        try {
            canalDataSyncService.startCanalSync();
            return Result.success("Canal数据同步启动成功");
        } catch (Exception e) {
            log.error("启动Canal数据同步失败", e);
            return Result.error("启动Canal数据同步失败: " + e.getMessage());
        }
    }

    /**
     * 停止Canal数据同步
     */
    @PostMapping("/stop")
    @ApiOperation("停止Canal数据同步")
    public Result<String> stopCanalSync() {
        try {
            canalDataSyncService.stopCanalSync();
            return Result.success("Canal数据同步停止成功");
        } catch (Exception e) {
            log.error("停止Canal数据同步失败", e);
            return Result.error("停止Canal数据同步失败: " + e.getMessage());
        }
    }

    /**
     * 获取Canal同步状态
     */
    @GetMapping("/status")
    @ApiOperation("获取Canal同步状态")
    public Result<String> getCanalStatus() {
        try {
            // 这里可以添加状态检查逻辑
            return Result.success("Canal数据同步服务运行中");
        } catch (Exception e) {
            log.error("获取Canal同步状态失败", e);
            return Result.error("获取Canal同步状态失败: " + e.getMessage());
        }
    }
}
