package com.agriculture.field.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;

@Data
@TableName("field")
public class Field {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;        // 农田名称
    private Double latitude;    // 纬度
    private Double longitude;   // 经度
    private Double area;        // 面积（亩）
    private String crop;        // 作物类型
    private LocalDate plantDate;    // 种植时间
    private LocalDate harvestDate;  // 预计收获时间
    private String manager;     // 负责人
    private String description; // 描述
    private String status;      // 状态（种植中、已收获等）
}