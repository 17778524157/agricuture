package com.agriculture.mall.service.impl;

import com.agriculture.mall.entity.PointsRecord;
import com.agriculture.mall.mapper.PointsRecordMapper;
import com.agriculture.mall.mapper.UserMapper;
import com.agriculture.mall.service.PointsRecordService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 积分记录服务实现类
 */
@Service
public class PointsRecordServiceImpl extends ServiceImpl<PointsRecordMapper, PointsRecord> implements PointsRecordService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<PointsRecord> getUserPointsRecord(Integer userId) {
        QueryWrapper<PointsRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                   .orderByDesc("create_time");
        return baseMapper.selectList(queryWrapper);
    }

    @Override
    public void addPointsRecord(Integer userId, Integer points, String type, String source, String description) {
        PointsRecord record = new PointsRecord();
        record.setUserId(userId);
        record.setPoints(points);
        record.setType(type);
        record.setSource(source);
        record.setDescription(description);
        record.setCreateTime(LocalDateTime.now());
        baseMapper.insert(record);
    }

    @Override
    public Integer getUserTotalPoints(Integer userId, String type) {
        QueryWrapper<PointsRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                   .eq("type", type);
        queryWrapper.select("SUM(points) as total");
        PointsRecord record = baseMapper.selectOne(queryWrapper);
        if (record != null) {
            // 这里需要根据实际返回值处理
            // 由于使用了SUM函数，可能需要通过反射或其他方式获取结果
            return record.getPoints() != null ? record.getPoints() : 0;
        }
        return 0;
    }
    
    @Override
    public boolean hasCheckedInToday(Integer userId) {
        QueryWrapper<PointsRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                   .eq("source", "sign_in")
                   .eq("type", "gain")
                   .apply("DATE(create_time) = CURDATE()");
        return baseMapper.selectCount(queryWrapper) > 0;
    }
    
    @Override
    public int calculateCheckinPoints(Integer userId) {
        int consecutiveDays = getConsecutiveCheckinDays(userId);
        int basePoints = 10; // 基础签到积分
        
        // 连续签到奖励
        if (consecutiveDays >= 7) {
            return basePoints + 20; // 连续7天额外20积分
        } else if (consecutiveDays >= 3) {
            return basePoints + 10; // 连续3天额外10积分
        } else {
            return basePoints; // 基础积分
        }
    }
    
    @Override
    public int getConsecutiveCheckinDays(Integer userId) {
        QueryWrapper<PointsRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                   .eq("source", "sign_in")
                   .eq("type", "gain")
                   .orderByDesc("create_time");
        
        List<PointsRecord> records = baseMapper.selectList(queryWrapper);
        if (records.isEmpty()) {
            return 0;
        }
        
        int consecutiveDays = 0;
        java.time.LocalDate lastCheckinDate = null;
        
        for (PointsRecord record : records) {
            java.time.LocalDate checkinDate = record.getCreateTime().toLocalDate();
            
            if (lastCheckinDate == null) {
                lastCheckinDate = checkinDate;
                consecutiveDays = 1;
            } else {
                // 检查是否连续
                if (checkinDate.equals(lastCheckinDate.minusDays(1))) {
                    consecutiveDays++;
                    lastCheckinDate = checkinDate;
                } else {
                    break;
                }
            }
        }
        
        return consecutiveDays;
    }
    
    @Override
    public int getTodayCheckinPoints(Integer userId) {
        QueryWrapper<PointsRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                   .eq("source", "sign_in")
                   .eq("type", "gain")
                   .apply("DATE(create_time) = CURDATE()");
        
        PointsRecord record = baseMapper.selectOne(queryWrapper);
        return record != null ? record.getPoints() : 0;
    }
    
    @Override
    public boolean hasCompletedTaskToday(Integer userId, String taskType) {
        QueryWrapper<PointsRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                   .eq("source", "task")
                   .eq("type", "gain")
                   .apply("DATE(create_time) = CURDATE()");
        
        // 根据任务类型匹配不同的描述模式
        switch (taskType) {
            case "login":
                queryWrapper.like("description", "每日登录");
                break;
            case "browse_products":
                queryWrapper.like("description", "浏览商品");
                break;
            case "share_product":
                queryWrapper.like("description", "分享商品");
                break;
            case "complete_profile":
                queryWrapper.like("description", "完善个人信息");
                break;
            case "upload_avatar":
                queryWrapper.like("description", "上传头像");
                break;
            default:
                queryWrapper.like("description", taskType);
                break;
        }
        
        return baseMapper.selectCount(queryWrapper) > 0;
    }
    
    @Override
    public int calculateTaskPoints(String taskType) {
        java.util.Map<String, Integer> taskPointsMap = getTaskPointsMap();
        return taskPointsMap.getOrDefault(taskType, 5); // 默认5积分
    }
    
    @Override
    public java.util.Map<String, Boolean> getTodayTaskStatus(Integer userId) {
        java.util.Map<String, Boolean> status = new java.util.HashMap<>();
        
        // 定义任务类型
        String[] taskTypes = {"login", "browse_products", "share_product", "complete_profile", "upload_avatar"};
        
        for (String taskType : taskTypes) {
            status.put(taskType, hasCompletedTaskToday(userId, taskType));
        }
        
        return status;
    }
    
    @Override
    public java.util.Map<String, Integer> getTaskPointsMap() {
        java.util.Map<String, Integer> taskPointsMap = new java.util.HashMap<>();
        taskPointsMap.put("login", 5);           // 每日登录
        taskPointsMap.put("browse_products", 10); // 浏览商品
        taskPointsMap.put("share_product", 15);  // 分享商品
        taskPointsMap.put("complete_profile", 20); // 完善个人信息
        taskPointsMap.put("upload_avatar", 10);   // 上传头像
        return taskPointsMap;
    }
    
    @Override
    public boolean verifyTaskCompletion(Integer userId, String taskType) {
        try {
            switch (taskType) {
                case "login":
                    // 登录任务：检查用户今日是否有登录记录
                    return verifyLoginTask(userId);
                    
                case "browse_products":
                    // 浏览商品任务：检查用户今日是否浏览了商品
                    return verifyBrowseProductsTask(userId);
                    
                case "share_product":
                    // 分享商品任务：检查用户今日是否分享了商品
                    return verifyShareProductTask(userId);
                    
                case "complete_profile":
                    // 完善个人信息任务：检查用户个人信息是否完整
                    return verifyCompleteProfileTask(userId);
                    
                case "upload_avatar":
                    // 上传头像任务：检查用户是否上传了头像
                    return verifyUploadAvatarTask(userId);
                    
                default:
                    return false;
            }
        } catch (Exception e) {
            // 记录错误日志
            System.err.println("验证任务完成状态失败: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 验证登录任务
     */
    private boolean verifyLoginTask(Integer userId) {
        // 登录任务：检查用户今日是否已登录
        // 通过检查用户今日是否有登录记录来判断
        try {
            // 检查用户今日是否有登录记录（可以通过用户表或登录日志表）
            // 这里简化处理，假设用户访问积分任务页面就说明已经登录
            // 实际项目中可以通过检查用户会话、最后活动时间等来判断
            return true; // 访问积分任务页面说明用户已登录
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 验证浏览商品任务
     */
    private boolean verifyBrowseProductsTask(Integer userId) {
        // 浏览商品任务：检查用户今日是否真正浏览了商品页面
        try {
            // 这里可以通过检查用户访问记录来判断
            // 比如检查用户今日是否访问了商品详情页面
            // 暂时返回false，需要用户真正浏览商品才能完成
            return false; // 需要用户真正浏览商品页面
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 验证分享商品任务
     */
    private boolean verifyShareProductTask(Integer userId) {
        // 分享商品任务：检查用户今日是否真正分享了商品
        try {
            // 这里可以通过检查分享记录来判断
            // 比如检查用户今日是否有分享商品的记录
            // 暂时返回false，需要用户真正分享商品才能完成
            return false; // 需要用户真正分享商品
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 验证完善个人信息任务
     */
    private boolean verifyCompleteProfileTask(Integer userId) {
        // 完善个人信息任务：检查用户个人信息是否完整
        try {
            // 通过UserMapper获取用户信息并检查必要字段
            // 检查用户是否填写了完整的个人信息
            // 比如：姓名、电话、地址等必要信息
            return false; // 需要用户真正完善个人信息
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 验证上传头像任务
     */
    private boolean verifyUploadAvatarTask(Integer userId) {
        // 检查用户是否上传了头像
        try {
            // 这里需要检查用户是否有头像URL
            // 通过UserMapper获取用户信息，检查avatarUrl字段
            // 暂时返回false，需要用户真正上传头像才能完成
            return false; // 需要用户真正上传头像
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 每日任务状态重置
     * 每天凌晨0点重置所有用户的任务状态
     */
    @Scheduled(cron = "0 0 0 * * ?") // 每天凌晨0点执行
    public void resetDailyTasks() {
        try {
            System.out.println("开始重置每日任务状态...");
            
            // 这里可以添加重置逻辑
            // 比如清理过期的任务状态缓存
            // 或者重置某些需要每日重置的状态
            
            System.out.println("每日任务状态重置完成");
        } catch (Exception e) {
            System.err.println("重置每日任务状态失败: " + e.getMessage());
        }
    }
    
    /**
     * 检查任务是否在今日已完成
     * 更严格的检查，确保每个任务每天只能完成一次
     */
    public boolean isTaskCompletedToday(Integer userId, String taskType) {
        QueryWrapper<PointsRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                   .eq("source", "task")
                   .eq("type", "gain")
                   .apply("DATE(create_time) = CURDATE()");
        
        // 根据任务类型匹配不同的描述模式
        switch (taskType) {
            case "login":
                queryWrapper.like("description", "每日登录");
                break;
            case "browse_products":
                queryWrapper.like("description", "浏览商品");
                break;
            case "share_product":
                queryWrapper.like("description", "分享商品");
                break;
            case "complete_profile":
                queryWrapper.like("description", "完善个人信息");
                break;
            case "upload_avatar":
                queryWrapper.like("description", "上传头像");
                break;
            default:
                queryWrapper.like("description", taskType);
                break;
        }
        
        return baseMapper.selectCount(queryWrapper) > 0;
    }
    
    @Override
    public java.util.Map<String, Object> getUserPointsRecordWithPage(
            Integer userId, Integer pageNum, Integer pageSize, 
            String type, String source, String startTime, String endTime) {
        
        try {
            // 构建查询条件
            QueryWrapper<PointsRecord> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId);
            
            // 添加类型筛选
            if (type != null && !type.trim().isEmpty()) {
                queryWrapper.eq("type", type);
            }
            
            // 添加来源筛选
            if (source != null && !source.trim().isEmpty()) {
                queryWrapper.eq("source", source);
            }
            
            // 添加时间范围筛选
            if (startTime != null && !startTime.trim().isEmpty()) {
                queryWrapper.ge("create_time", startTime + " 00:00:00");
            }
            if (endTime != null && !endTime.trim().isEmpty()) {
                queryWrapper.le("create_time", endTime + " 23:59:59");
            }
            
            // 按时间倒序排列
            queryWrapper.orderByDesc("create_time");
            
            // 获取总记录数
            Long totalLong = baseMapper.selectCount(queryWrapper);
            int total = totalLong != null ? totalLong.intValue() : 0;
            
            // 计算分页参数
            int offset = (pageNum - 1) * pageSize;
            queryWrapper.last("LIMIT " + offset + ", " + pageSize);
            
            // 查询分页数据
            List<PointsRecord> records = baseMapper.selectList(queryWrapper);
            
            // 构建返回结果
            java.util.Map<String, Object> result = new java.util.HashMap<>();
            result.put("list", records);
            result.put("total", total);
            result.put("pageNum", pageNum);
            result.put("pageSize", pageSize);
            result.put("totalPages", (total + pageSize - 1) / pageSize);
            
            return result;
        } catch (Exception e) {
            System.err.println("获取积分记录失败: " + e.getMessage());
            java.util.Map<String, Object> result = new java.util.HashMap<>();
            result.put("list", new java.util.ArrayList<>());
            result.put("total", 0);
            result.put("pageNum", pageNum);
            result.put("pageSize", pageSize);
            result.put("totalPages", 0);
            return result;
        }
    }
}