package com.agriculture.crop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@TableName("crop")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Crop {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name; // 作物名称
    
    private String variety; // 品种
    
    private String type; // 类型
    
    private String growthCycle; // 生长周期
    
    private String suitableTemperature; // 适宜温度
    
    private String suitableHumidity; // 适宜湿度
    
    private String suitableSoil; // 适宜土壤
    
    private String description; // 描述
    
    private String status; // 状态（种植中、已收获等）
    
    private LocalDateTime createTime; // 创建时间
    
    private LocalDateTime updateTime; // 更新时间
}