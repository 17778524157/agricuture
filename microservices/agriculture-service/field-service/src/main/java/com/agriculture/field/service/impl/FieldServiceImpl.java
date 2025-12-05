package com.agriculture.field.service.impl;

import com.agriculture.field.entity.Field;
import com.agriculture.field.mapper.FieldMapper;
import com.agriculture.field.service.IFieldService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FieldServiceImpl extends ServiceImpl<FieldMapper, Field> implements IFieldService {

    @Override
    public List<Field> getAllFields() {
        return baseMapper.selectList(null);
    }

    @Override
    public Field getFieldById(Integer id) {
        return baseMapper.selectById(id);
    }

    @Override
    public boolean addField(Field field) {
        return save(field);
    }

    @Override
    public boolean updateField(Field field) {
        return updateById(field);
    }

    @Override
    public boolean deleteField(Integer id) {
        return removeById(id);
    }

    @Override
    public List<Field> getFieldsByCrop(String crop) {
        QueryWrapper<Field> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("crop", crop);
        return baseMapper.selectList(queryWrapper);
    }

    @Override
    public List<Field> getFieldsByStatus(String status) {
        QueryWrapper<Field> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", status);
        return baseMapper.selectList(queryWrapper);
    }
}