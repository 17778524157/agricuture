package com.agriculture.crop.service;

import com.agriculture.crop.entity.Crop;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface ICropService extends IService<Crop> {

    // 获取所有作物信息
    List<Crop> getAllCrops();

    // 根据ID获取作物信息
    Crop getCropById(Long id);

    // 根据作物名称查询
    List<Crop> getCropsByName(String name);

    // 根据作物类型查询
    List<Crop> getCropsByType(String type);

    // 添加新作物
    boolean addCrop(Crop crop);

    // 更新作物信息
    boolean updateCrop(Crop crop);

    // 删除作物
    boolean deleteCrop(Long id);

    // 批量删除作物
    boolean batchDeleteCrops(List<Long> ids);
}