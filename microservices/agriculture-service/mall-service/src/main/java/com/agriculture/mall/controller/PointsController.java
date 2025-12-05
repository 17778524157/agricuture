package com.agriculture.mall.controller;

import com.agriculture.common.result.Result;
import com.agriculture.mall.entity.PointsRecord;
import com.agriculture.mall.mapper.UserMapper;
import com.agriculture.mall.service.PointsRecordService;
import com.agriculture.mall.service.UserPointsCacheService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 积分控制器
 */
@RestController
@RequestMapping("/api/mall/points")
public class PointsController {

    @Autowired
    private PointsRecordService pointsRecordService;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private UserPointsCacheService userPointsCacheService;

    /**
     * 获取用户当前积分 - 使用缓存
     */
    @GetMapping("/current/{userId}")
    public Result<Integer> getCurrentPoints(@PathVariable Integer userId) {
        Integer points = userPointsCacheService.getUserPoints(userId);
        return Result.success(points);
    }

    /**
     * 获取用户积分记录
     */
    @GetMapping("/record/{userId}")
    public Result<List<PointsRecord>> getPointsRecord(@PathVariable Integer userId) {
        List<PointsRecord> records = pointsRecordService.getUserPointsRecord(userId);
        return Result.success(records);
    }
    
    /**
     * 获取用户积分记录（带分页和筛选）
     */
    @GetMapping("/record/{userId}/page")
    public Result<Object> getPointsRecordWithPage(
            @PathVariable Integer userId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String source,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        
        try {
            java.util.Map<String, Object> result = pointsRecordService.getUserPointsRecordWithPage(
                userId, pageNum, pageSize, type, source, startTime, endTime);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("获取积分记录失败：" + e.getMessage());
        }
    }

    /**
     * 获取用户总积分获取
     */
    @GetMapping("/total/gain/{userId}")
    public Result<Integer> getTotalGainPoints(@PathVariable Integer userId) {
        Integer total = pointsRecordService.getUserTotalPoints(userId, "gain");
        return Result.success(total);
    }

    /**
     * 获取用户总积分消耗
     */
    @GetMapping("/total/consume/{userId}")
    public Result<Integer> getTotalConsumePoints(@PathVariable Integer userId) {
        Integer total = pointsRecordService.getUserTotalPoints(userId, "consume");
        return Result.success(total);
    }
    
    /**
     * 扣减用户积分（用于商品兑换）
     */
    @PostMapping("/deduct/{userId}")
    public Result<String> deductPoints(@PathVariable Integer userId, @RequestParam Integer points, @RequestParam String reason) {
        try {
            // 检查用户当前积分
            Integer currentPoints = userMapper.getPoints(userId);
            if (currentPoints == null || currentPoints < points) {
                return Result.error("积分不足，当前积分：" + (currentPoints != null ? currentPoints : 0) + "，需要积分：" + points);
            }
            
            // 扣减积分
            int updatedRows = userMapper.updatePoints(userId, -points);
            if (updatedRows <= 0) {
                return Result.error("积分扣减失败");
            }
            
            // 添加积分消耗记录
            pointsRecordService.addPointsRecord(userId, -points, "consume", "exchange", reason);
            
            return Result.success("积分扣减成功");
        } catch (Exception e) {
            return Result.error("积分扣减失败：" + e.getMessage());
        }
    }
    
    /**
     * 签到获得积分
     */
    @PostMapping("/checkin/{userId}")
    public Result<Object> dailyCheckin(@PathVariable Integer userId) {
        try {
            // 检查今日是否已签到
            boolean hasCheckedIn = pointsRecordService.hasCheckedInToday(userId);
            if (hasCheckedIn) {
                return Result.error("今日已签到，请明天再来");
            }
            
            // 计算签到积分（连续签到有额外奖励）
            int checkinPoints = pointsRecordService.calculateCheckinPoints(userId);
            
            // 增加积分
            int updatedRows = userMapper.updatePoints(userId, checkinPoints);
            if (updatedRows <= 0) {
                return Result.error("签到失败");
            }
            
            // 添加积分获取记录
            pointsRecordService.addPointsRecord(userId, checkinPoints, "gain", "sign_in", "每日签到");
            
            // 返回签到结果
            java.util.Map<String, Object> result = new java.util.HashMap<>();
            result.put("points", checkinPoints);
            result.put("message", "签到成功，获得 " + checkinPoints + " 积分");
            result.put("consecutiveDays", pointsRecordService.getConsecutiveCheckinDays(userId));
            
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("签到失败：" + e.getMessage());
        }
    }
    
    /**
     * 完成任务获得积分
     */
    @PostMapping("/task/{userId}")
    public Result<Object> completeTask(@PathVariable Integer userId, @RequestParam String taskType, @RequestParam String taskName) {
        try {
            // 双重检查：确保任务今天没有完成过
            boolean hasCompleted = pointsRecordService.hasCompletedTaskToday(userId, taskType);
            if (hasCompleted) {
                return Result.error("今日该任务已完成，请明天再来");
            }
            
            // 验证任务是否真正完成
            boolean taskCompleted = pointsRecordService.verifyTaskCompletion(userId, taskType);
            if (!taskCompleted) {
                return Result.error("任务尚未完成，请先完成相应操作");
            }
            
            // 再次检查：防止并发情况下的重复提交
            boolean hasCompletedAgain = pointsRecordService.hasCompletedTaskToday(userId, taskType);
            if (hasCompletedAgain) {
                return Result.error("任务已完成，请勿重复提交");
            }
            
            // 根据任务类型计算积分
            int taskPoints = pointsRecordService.calculateTaskPoints(taskType);
            
            // 增加积分
            int updatedRows = userMapper.updatePoints(userId, taskPoints);
            if (updatedRows <= 0) {
                return Result.error("任务完成失败");
            }
            
            // 添加积分获取记录
            pointsRecordService.addPointsRecord(userId, taskPoints, "gain", "task", "完成任务：" + taskName);
            
            // 返回任务完成结果
            java.util.Map<String, Object> result = new java.util.HashMap<>();
            result.put("points", taskPoints);
            result.put("message", "任务完成，获得 " + taskPoints + " 积分");
            result.put("taskType", taskType);
            result.put("completedAt", java.time.LocalDateTime.now());
            
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("任务完成失败：" + e.getMessage());
        }
    }
    
    /**
     * 检查任务完成状态（不领取积分）
     */
    @GetMapping("/task/check/{userId}")
    public Result<Object> checkTaskStatus(@PathVariable Integer userId, @RequestParam String taskType) {
        try {
            boolean taskCompleted = pointsRecordService.verifyTaskCompletion(userId, taskType);
            boolean hasClaimed = pointsRecordService.hasCompletedTaskToday(userId, taskType);
            
            java.util.Map<String, Object> result = new java.util.HashMap<>();
            result.put("taskCompleted", taskCompleted);
            result.put("hasClaimed", hasClaimed);
            result.put("canClaim", taskCompleted && !hasClaimed);
            
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("检查任务状态失败：" + e.getMessage());
        }
    }
    
    /**
     * 手动重置用户今日任务状态（仅用于测试）
     */
    @PostMapping("/task/reset/{userId}")
    public Result<Object> resetUserTasks(@PathVariable Integer userId) {
        try {
            // 删除用户今日的任务完成记录
            QueryWrapper<PointsRecord> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId)
                       .eq("source", "task")
                       .eq("type", "gain")
                       .apply("DATE(create_time) = CURDATE()");
            
            int deletedCount = pointsRecordService.getBaseMapper().delete(queryWrapper);
            
            java.util.Map<String, Object> result = new java.util.HashMap<>();
            result.put("deletedCount", deletedCount);
            result.put("message", "已重置用户今日任务状态");
            
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("重置任务状态失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取任务完成统计信息
     */
    @GetMapping("/task/stats/{userId}")
    public Result<Object> getTaskStats(@PathVariable Integer userId) {
        try {
            java.util.Map<String, Object> stats = new java.util.HashMap<>();
            
            // 获取今日任务完成情况
            java.util.Map<String, Boolean> todayStatus = pointsRecordService.getTodayTaskStatus(userId);
            stats.put("todayStatus", todayStatus);
            
            // 获取本周任务完成统计
            QueryWrapper<PointsRecord> weekQuery = new QueryWrapper<>();
            weekQuery.eq("user_id", userId)
                    .eq("source", "task")
                    .eq("type", "gain")
                    .apply("DATE(create_time) >= DATE_SUB(CURDATE(), INTERVAL 7 DAY)");
            
            List<PointsRecord> weekRecords = pointsRecordService.list(weekQuery);
            stats.put("weekCompleted", weekRecords.size());
            
            // 获取本月任务完成统计
            QueryWrapper<PointsRecord> monthQuery = new QueryWrapper<>();
            monthQuery.eq("user_id", userId)
                     .eq("source", "task")
                     .eq("type", "gain")
                     .apply("DATE(create_time) >= DATE_SUB(CURDATE(), INTERVAL 30 DAY)");
            
            List<PointsRecord> monthRecords = pointsRecordService.list(monthQuery);
            stats.put("monthCompleted", monthRecords.size());
            
            return Result.success(stats);
        } catch (Exception e) {
            return Result.error("获取任务统计失败：" + e.getMessage());
        }
    }
    @GetMapping("/checkin/status/{userId}")
    public Result<Object> getCheckinStatus(@PathVariable Integer userId) {
        try {
            boolean hasCheckedIn = pointsRecordService.hasCheckedInToday(userId);
            int consecutiveDays = pointsRecordService.getConsecutiveCheckinDays(userId);
            int todayPoints = pointsRecordService.getTodayCheckinPoints(userId);
            
            java.util.Map<String, Object> result = new java.util.HashMap<>();
            result.put("hasCheckedIn", hasCheckedIn);
            result.put("consecutiveDays", consecutiveDays);
            result.put("todayPoints", todayPoints);
            
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("获取签到状态失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取用户任务完成状态
     */
    @GetMapping("/task/status/{userId}")
    public Result<Object> getTaskStatus(@PathVariable Integer userId) {
        try {
            java.util.Map<String, Boolean> taskStatus = pointsRecordService.getTodayTaskStatus(userId);
            java.util.Map<String, Integer> taskPoints = pointsRecordService.getTaskPointsMap();
            
            java.util.Map<String, Object> result = new java.util.HashMap<>();
            result.put("taskStatus", taskStatus);
            result.put("taskPoints", taskPoints);
            
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("获取任务状态失败：" + e.getMessage());
        }
    }
}