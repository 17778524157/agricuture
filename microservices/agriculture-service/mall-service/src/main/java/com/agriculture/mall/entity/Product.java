package com.agriculture.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 商品实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("mall_product")
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("name")
    private String name;

    @TableField("description")
    private String description;

    @TableField("points")
    private Integer points;

    @TableField("stock")
    private Integer stock;

    @TableField("category")
    private String category;

    @TableField("image_url")
    private String imageUrl;

    @TableField("is_new")
    private Boolean isNew;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;

    @TableField("status")
    private String status; // PENDING-待审核 ACTIVE-上架 INACTIVE-下架
}
