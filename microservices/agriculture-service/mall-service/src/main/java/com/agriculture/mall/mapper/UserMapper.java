package com.agriculture.mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户Mapper接口（用于积分相关操作）
 */
public interface UserMapper extends BaseMapper<Object> {
    
    /**
     * 更新用户积分
     */
    int updatePoints(@Param("userId") Integer userId, @Param("points") Integer points);
    
    /**
     * 查询用户积分
     */
    Integer getPoints(@Param("userId") Integer userId);
    
}