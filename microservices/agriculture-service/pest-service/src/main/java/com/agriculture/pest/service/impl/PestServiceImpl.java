package com.agriculture.pest.service.impl;

import com.agriculture.pest.entity.Pest;
import com.agriculture.pest.mapper.PestMapper;
import com.agriculture.pest.service.IPestService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class PestServiceImpl extends ServiceImpl<PestMapper, Pest> implements IPestService {

    @Override
    public List<Pest> getRecentByFarm(Integer farmId, Integer recentDays) {
        QueryWrapper<Pest> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("farm_id", farmId)
                .eq("is_deleted", 0)
                .ge("create_time", LocalDateTime.now().minus(recentDays, ChronoUnit.DAYS))
                .orderByDesc("create_time");
        return baseMapper.selectList(queryWrapper);
    }

    @Override
    public List<Pest> getAllPests() {
        QueryWrapper<Pest> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_deleted", 0)
                .orderByDesc("create_time");
        return baseMapper.selectList(queryWrapper);
    }

    @Override
    public Pest getPestById(Long id) {
        QueryWrapper<Pest> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id)
                .eq("is_deleted", 0);
        return baseMapper.selectOne(queryWrapper);
    }

    @Override
    public boolean addPest(Pest pest) {
        pest.setCreateTime(LocalDateTime.now());
        pest.setUpdateTime(LocalDateTime.now());
        pest.setIsDeleted(0);
        return save(pest);
    }

    @Override
    public boolean updatePest(Pest pest) {
        pest.setUpdateTime(LocalDateTime.now());
        return updateById(pest);
    }

    @Override
    public boolean deletePest(Long id) {
        Pest pest = new Pest();
        pest.setId(id);
        pest.setIsDeleted(1);
        pest.setUpdateTime(LocalDateTime.now());
        return updateById(pest);
    }
}