package com.agriculture.crop.service.impl;

import com.agriculture.crop.entity.Crop;
import com.agriculture.crop.mapper.CropMapper;
import com.agriculture.crop.service.ICropService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CropServiceImpl extends ServiceImpl<CropMapper, Crop> implements ICropService {

    @Override
    public List<Crop> getAllCrops() {
        return list();
    }

    @Override
    public Crop getCropById(Long id) {
        return getById(id);
    }

    @Override
    public List<Crop> getCropsByName(String name) {
        QueryWrapper<Crop> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("name", name);
        return list(queryWrapper);
    }

    @Override
    public List<Crop> getCropsByType(String type) {
        QueryWrapper<Crop> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type", type);
        return list(queryWrapper);
    }

    @Override
    public boolean addCrop(Crop crop) {
        LocalDateTime now = LocalDateTime.now();
        crop.setCreateTime(now);
        crop.setUpdateTime(now);
        return save(crop);
    }

    @Override
    public boolean updateCrop(Crop crop) {
        crop.setUpdateTime(LocalDateTime.now());
        return updateById(crop);
    }

    @Override
    public boolean deleteCrop(Long id) {
        return removeById(id);
    }

    @Override
    public boolean batchDeleteCrops(List<Long> ids) {
        return removeByIds(ids);
    }
}