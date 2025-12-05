package com.agriculture.weather.service;

import com.agriculture.weather.entity.WeatherData;
import com.baomidou.mybatisplus.extension.service.IService;

import java.time.LocalDateTime;
import java.util.List;

public interface IWeatherDataService extends IService<WeatherData> {

    // 根据农场ID获取天气数据
    List<WeatherData> getWeatherDataByFarmId(Integer farmId);

    // 根据农场ID和时间范围获取天气数据
    List<WeatherData> getWeatherDataByFarmIdAndTimeRange(Integer farmId, LocalDateTime startTime, LocalDateTime endTime);

    // 获取最新的天气数据
    WeatherData getLatestWeatherData(Integer farmId);

    // 添加新的天气数据
    boolean addWeatherData(WeatherData weatherData);

    // 批量添加天气数据
    boolean batchAddWeatherData(List<WeatherData> weatherDataList);
}