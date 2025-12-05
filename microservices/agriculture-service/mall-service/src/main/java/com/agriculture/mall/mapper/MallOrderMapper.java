package com.agriculture.mall.mapper;

import com.agriculture.mall.entity.MallOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 订单Mapper接口
 */
public interface MallOrderMapper extends BaseMapper<MallOrder> {
    
    /**
     * 根据用户ID查询订单
     */
    List<MallOrder> selectByUserId(@Param("userId") Integer userId);
    
    /**
     * 根据订单状态查询
     */
    List<MallOrder> selectByStatus(@Param("status") String status);
    
    /**
     * 根据订单号查询
     */
    MallOrder selectByOrderNo(@Param("orderNo") String orderNo);
    
}