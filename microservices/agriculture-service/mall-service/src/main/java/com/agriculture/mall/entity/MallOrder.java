package com.agriculture.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 订单实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("mall_order")
public class MallOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String orderNo;

    private Integer userId;

    private Integer productId;

    private Integer quantity;

    private Integer totalPoints;
    
    private Integer pointsUsed;

    private String status;

    private String shippingAddress;

    private String contactPhone;
    
    private LocalDateTime payTime;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

}