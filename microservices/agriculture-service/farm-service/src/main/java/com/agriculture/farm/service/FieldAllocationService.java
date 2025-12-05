package com.agriculture.farm.service;

import com.agriculture.farm.entity.FieldAllocation;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 农田分配服务接口
 */
public interface FieldAllocationService {

    /**
     * 申请农田
     * @param userId 用户ID
     * @param fieldId 农田ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 分配记录
     */
    FieldAllocation applyField(Integer userId, Integer fieldId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 确认农田分配
     * @param adminId 管理员ID
     * @param allocationId 分配ID
     * @return 是否成功
     */
    boolean confirmAllocation(Integer adminId, Integer allocationId);

    /**
     * 释放农田
     * @param adminId 管理员ID
     * @param allocationId 分配ID
     * @return 是否成功
     */
    boolean releaseField(Integer adminId, Integer allocationId);

    /**
     * 获取用户农田分配记录
     * @param userId 用户ID
     * @return 分配记录列表
     */
    List<FieldAllocation> getUserAllocations(Integer userId);

    /**
     * 获取农田分配历史
     * @param fieldId 农田ID
     * @return 分配记录列表
     */
    List<FieldAllocation> getFieldAllocationHistory(Integer fieldId);

    /**
     * 根据ID获取分配记录
     * @param allocationId 分配ID
     * @return 分配记录
     */
    FieldAllocation getAllocationById(Integer allocationId);

    /**
     * 取消农田分配
     * @param userId 用户ID
     * @param allocationId 分配ID
     * @return 是否成功
     */
    boolean cancelAllocation(Integer userId, Integer allocationId);
}


