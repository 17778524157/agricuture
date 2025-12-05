package com.agriculture.farm.service;

import com.agriculture.farm.entity.Field;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 农田服务接口
 */
public interface FieldService extends IService<Field> {
    
    /**
     * 根据用户ID获取农田列表
     */
    List<Field> getFieldsByUserId(Integer userId);
    
    /**
     * 获取农田统计信息
     */
    Object getFieldStatistics(Integer fieldId);
}
