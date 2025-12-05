package com.agriculture.farm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 农田实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("field")
public class Field implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String name;

    private Double latitude;
    
    private Double longitude;
    
    private Double area;

    private String crop;
    
    private Date plantDate;
    
    private Date harvestDate;
    
    private String manager;
    
    private String description;

    private String status;
}
