package com.agriculture.farm.service.impl;

import com.agriculture.common.lock.DistributedLock;
import com.agriculture.farm.entity.FarmField;
import com.agriculture.farm.entity.FieldAllocation;
import com.agriculture.farm.mapper.FarmFieldMapper;
import com.agriculture.farm.mapper.FieldAllocationMapper;
import com.agriculture.farm.service.FieldAllocationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 农田分配服务实现类
 * 使用分布式锁解决农田资源竞争问题
 */
@Slf4j
@Service
public class FieldAllocationServiceImpl implements FieldAllocationService {

    @Autowired
    private FieldAllocationMapper allocationMapper;

    @Autowired
    private FarmFieldMapper fieldMapper;

    @Autowired
    private DistributedLock distributedLock;

    /**
     * 申请农田 - 使用分布式锁防止重复申请
     */
    @Override
    @Transactional
    public FieldAllocation applyField(Integer userId, Integer fieldId, LocalDateTime startDate, LocalDateTime endDate) {
        // 构建锁key：农田ID + 日期，防止同一农田被重复分配
        String lockKey = "farm:field:allocation:lock:" + fieldId + ":" + startDate.toLocalDate();
        
        return distributedLock.executeWithLock(lockKey, () -> {
            log.info("开始申请农田，用户ID: {}, 农田ID: {}, 开始日期: {}, 结束日期: {}", 
                userId, fieldId, startDate, endDate);
            
            // 1. 检查农田是否存在
            FarmField field = fieldMapper.selectById(fieldId);
            if (field == null) {
                throw new RuntimeException("农田不存在");
            }
            
            if (!"AVAILABLE".equals(field.getStatus())) {
                throw new RuntimeException("农田不可用，当前状态: " + field.getStatus());
            }
            
            // 2. 检查时间冲突
            List<FieldAllocation> existingAllocations = allocationMapper.findConflictingAllocations(
                fieldId, startDate, endDate);
            
            if (!existingAllocations.isEmpty()) {
                throw new RuntimeException("该时间段农田已被分配，冲突的分配记录: " + existingAllocations.size() + "条");
            }
            
            // 3. 创建分配记录
            FieldAllocation allocation = new FieldAllocation();
            allocation.setUserId(userId);
            allocation.setFieldId(fieldId);
            allocation.setStartDate(startDate);
            allocation.setEndDate(endDate);
            allocation.setStatus("PENDING");
            allocation.setCreateTime(LocalDateTime.now());
            
            allocationMapper.insert(allocation);
            
            // 4. 更新农田状态
            field.setStatus("ALLOCATED");
            field.setUpdateTime(LocalDateTime.now());
            fieldMapper.updateById(field);
            
            log.info("农田申请成功，分配ID: {}, 用户ID: {}, 农田ID: {}", 
                allocation.getId(), userId, fieldId);
            
            return allocation;
        });
    }

    /**
     * 确认农田分配 - 使用分布式锁防止重复确认
     */
    @Override
    @Transactional
    public boolean confirmAllocation(Integer userId, Integer allocationId) {
        String lockKey = "farm:allocation:confirm:lock:" + allocationId;
        
        return distributedLock.executeWithLock(lockKey, () -> {
            log.info("开始确认农田分配，用户ID: {}, 分配ID: {}", userId, allocationId);
            
            // 1. 查询分配记录
            FieldAllocation allocation = allocationMapper.selectById(allocationId);
            if (allocation == null || !allocation.getUserId().equals(userId)) {
                throw new RuntimeException("分配记录不存在或无权限操作");
            }
            
            if (!"PENDING".equals(allocation.getStatus())) {
                throw new RuntimeException("分配状态不允许确认，当前状态: " + allocation.getStatus());
            }
            
            // 2. 更新分配状态
            allocation.setStatus("CONFIRMED");
            allocation.setConfirmTime(LocalDateTime.now());
            allocation.setUpdateTime(LocalDateTime.now());
            allocationMapper.updateById(allocation);
            
            log.info("农田分配确认成功，分配ID: {}", allocationId);
            return true;
        });
    }

    /**
     * 释放农田 - 使用分布式锁防止重复释放
     */
    @Override
    @Transactional
    public boolean releaseField(Integer userId, Integer allocationId) {
        String lockKey = "farm:allocation:release:lock:" + allocationId;
        
        return distributedLock.executeWithLock(lockKey, () -> {
            log.info("开始释放农田，用户ID: {}, 分配ID: {}", userId, allocationId);
            
            // 1. 查询分配记录
            FieldAllocation allocation = allocationMapper.selectById(allocationId);
            if (allocation == null || !allocation.getUserId().equals(userId)) {
                throw new RuntimeException("分配记录不存在或无权限操作");
            }
            
            if (!"CONFIRMED".equals(allocation.getStatus())) {
                throw new RuntimeException("分配状态不允许释放，当前状态: " + allocation.getStatus());
            }
            
            // 2. 更新分配状态
            allocation.setStatus("RELEASED");
            allocation.setReleaseTime(LocalDateTime.now());
            allocation.setUpdateTime(LocalDateTime.now());
            allocationMapper.updateById(allocation);
            
            // 3. 更新农田状态
            FarmField field = fieldMapper.selectById(allocation.getFieldId());
            if (field != null) {
                field.setStatus("AVAILABLE");
                field.setUpdateTime(LocalDateTime.now());
                fieldMapper.updateById(field);
            }
            
            log.info("农田释放成功，分配ID: {}", allocationId);
            return true;
        });
    }

    /**
     * 获取用户农田分配记录
     */
    @Override
    public List<FieldAllocation> getUserAllocations(Integer userId) {
        return allocationMapper.findByUserId(userId);
    }

    /**
     * 获取农田分配历史
     */
    @Override
    public List<FieldAllocation> getFieldAllocationHistory(Integer fieldId) {
        return allocationMapper.findByFieldId(fieldId);
    }

    /**
     * 根据ID获取分配记录
     */
    @Override
    public FieldAllocation getAllocationById(Integer allocationId) {
        return allocationMapper.selectById(allocationId);
    }

    /**
     * 取消农田分配
     */
    @Override
    @Transactional
    public boolean cancelAllocation(Integer userId, Integer allocationId) {
        String lockKey = "farm:allocation:cancel:lock:" + allocationId;
        
        return distributedLock.executeWithLock(lockKey, () -> {
            log.info("开始取消农田分配，用户ID: {}, 分配ID: {}", userId, allocationId);
            
            // 1. 查询分配记录
            FieldAllocation allocation = allocationMapper.selectById(allocationId);
            if (allocation == null || !allocation.getUserId().equals(userId)) {
                throw new RuntimeException("分配记录不存在或无权限操作");
            }
            
            if (!"PENDING".equals(allocation.getStatus())) {
                throw new RuntimeException("分配状态不允许取消，当前状态: " + allocation.getStatus());
            }
            
            // 2. 更新分配状态
            allocation.setStatus("CANCELLED");
            allocation.setUpdateTime(LocalDateTime.now());
            allocationMapper.updateById(allocation);
            
            // 3. 更新农田状态
            FarmField field = fieldMapper.selectById(allocation.getFieldId());
            if (field != null) {
                field.setStatus("AVAILABLE");
                field.setUpdateTime(LocalDateTime.now());
                fieldMapper.updateById(field);
            }
            
            log.info("农田分配取消成功，分配ID: {}", allocationId);
            return true;
        });
    }
}
