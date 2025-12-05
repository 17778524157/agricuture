package com.agriculture.pest.service;

import com.agriculture.pest.entity.Pest;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface IPestService extends IService<Pest> {

    // 根据农场ID获取最近的病虫害数据
    List<Pest> getRecentByFarm(Integer farmId, Integer recentDays);

    // 获取所有病虫害数据
    List<Pest> getAllPests();

    // 根据ID获取病虫害详情
    Pest getPestById(Long id);

    // 添加新的病虫害记录
    boolean addPest(Pest pest);

    // 更新病虫害记录
    boolean updatePest(Pest pest);

    // 删除病虫害记录
    boolean deletePest(Long id);
}