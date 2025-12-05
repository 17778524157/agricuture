package com.agriculture.mall.service;

import com.agriculture.mall.entity.PointsRecord;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 积分记录服务接口
 */
public interface PointsRecordService extends IService<PointsRecord> {
    
    /**
     * 获取用户积分记录
     */
    List<PointsRecord> getUserPointsRecord(Integer userId);
    
    /**
     * 添加积分记录
     */
    void addPointsRecord(Integer userId, Integer points, String type, String source, String description);
    
    /**
     * 获取用户总积分获取和消耗
     */
    Integer getUserTotalPoints(Integer userId, String type);
    
    /**
     * 检查用户今日是否已签到
     */
    boolean hasCheckedInToday(Integer userId);
    
    /**
     * 计算签到积分（连续签到有额外奖励）
     */
    int calculateCheckinPoints(Integer userId);
    
    /**
     * 获取用户连续签到天数
     */
    int getConsecutiveCheckinDays(Integer userId);
    
    /**
     * 获取用户今日签到积分
     */
    int getTodayCheckinPoints(Integer userId);
    
    /**
     * 检查用户今日是否已完成指定任务
     */
    boolean hasCompletedTaskToday(Integer userId, String taskType);
    
    /**
     * 根据任务类型计算积分
     */
    int calculateTaskPoints(String taskType);
    
    /**
     * 获取用户今日任务完成状态
     */
    java.util.Map<String, Boolean> getTodayTaskStatus(Integer userId);
    
    /**
     * 获取任务积分映射
     */
    java.util.Map<String, Integer> getTaskPointsMap();
    
    /**
     * 验证任务是否真正完成
     */
    boolean verifyTaskCompletion(Integer userId, String taskType);
    
    /**
     * 获取用户积分记录（带分页和筛选）
     */
    java.util.Map<String, Object> getUserPointsRecordWithPage(
        Integer userId, Integer pageNum, Integer pageSize, 
        String type, String source, String startTime, String endTime);
    
}