package com.agriculture.field.service;

import com.agriculture.field.entity.Field;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface IFieldService extends IService<Field> {

    // 获取所有田地信息
    List<Field> getAllFields();

    // 根据ID获取田地详情
    Field getFieldById(Integer id);

    // 添加新的田地信息
    boolean addField(Field field);

    // 更新田地信息
    boolean updateField(Field field);

    // 删除田地信息
    boolean deleteField(Integer id);

    // 根据作物类型获取田地
    List<Field> getFieldsByCrop(String crop);

    // 根据状态获取田地
    List<Field> getFieldsByStatus(String status);
}