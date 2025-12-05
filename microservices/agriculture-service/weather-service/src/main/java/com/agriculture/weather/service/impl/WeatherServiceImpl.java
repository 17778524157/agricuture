package com.agriculture.weather.service.impl;

import com.agriculture.weather.entity.WeatherData;
import com.agriculture.weather.mapper.WeatherDataMapper;
import com.agriculture.weather.service.IWeatherDataService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class WeatherServiceImpl extends ServiceImpl<WeatherDataMapper, WeatherData> implements IWeatherDataService {

    @Override
    public List<WeatherData> getWeatherDataByFarmId(Integer farmId) {
        QueryWrapper<WeatherData> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("farm_id", farmId);
        queryWrapper.orderByDesc("record_time");
        return baseMapper.selectList(queryWrapper);
    }

    @Override
    public List<WeatherData> getWeatherDataByFarmIdAndTimeRange(Integer farmId, LocalDateTime startTime, LocalDateTime endTime) {
        QueryWrapper<WeatherData> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("farm_id", farmId);
        queryWrapper.between("record_time", startTime, endTime);
        queryWrapper.orderByDesc("record_time");
        return baseMapper.selectList(queryWrapper);
    }

    @Override
    public WeatherData getLatestWeatherData(Integer farmId) {
        QueryWrapper<WeatherData> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("farm_id", farmId);
        queryWrapper.orderByDesc("record_time");
        queryWrapper.last("LIMIT 1");
        return baseMapper.selectOne(queryWrapper);
    }

    @Override
    public boolean addWeatherData(WeatherData weatherData) {
        LocalDateTime now = LocalDateTime.now();
        weatherData.setCreateTime(now);
        weatherData.setUpdateTime(now);
        return save(weatherData);
    }

    @Override
    public boolean batchAddWeatherData(List<WeatherData> weatherDataList) {
        LocalDateTime now = LocalDateTime.now();
        for (WeatherData data : weatherDataList) {
            data.setCreateTime(now);
            data.setUpdateTime(now);
        }
        return saveBatch(weatherDataList);
    }
}