package com.agriculture.farm.mapper;

import com.agriculture.farm.entity.WeatherData;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 天气数据Mapper
 */
@Mapper
public interface WeatherDataMapper extends BaseMapper<WeatherData> {
}
