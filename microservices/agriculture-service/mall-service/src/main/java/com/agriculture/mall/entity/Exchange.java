package com.agriculture.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 兑换记录实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("mall_exchange")
public class Exchange implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("user_name")
    private String userName;

    @TableField("product_id")
    private Long productId;

    @TableField("product_name")
    private String productName;

    @TableField("product_image")
    private String productImage;

    @TableField("points")
    private Integer points;

    @TableField("exchange_time")
    private String exchangeTime;

    @TableField("status")
    private String status; // pending-待处理 shipped-已发货 delivered-已送达 completed-已完成

    @TableField("remark")
    private String remark;

    @TableField("create_time")
    private String createTime;

    @TableField("update_time")
    private String updateTime;
}
