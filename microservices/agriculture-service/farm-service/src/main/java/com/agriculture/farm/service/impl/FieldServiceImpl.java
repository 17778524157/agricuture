package com.agriculture.farm.service.impl;

import com.agriculture.common.client.WeatherServiceClient;
import com.agriculture.common.client.PestServiceClient;
import com.agriculture.farm.entity.Field;
import com.agriculture.farm.mapper.FieldMapper;
import com.agriculture.farm.service.FieldService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 农田服务实现类
 */
@Slf4j
@Service
public class FieldServiceImpl extends ServiceImpl<FieldMapper, Field> implements FieldService {

    @Autowired
    private FieldMapper fieldMapper;

    @Autowired
    private WeatherServiceClient weatherServiceClient;

    @Autowired
    private PestServiceClient pestServiceClient;

    @Override
    public List<Field> getFieldsByUserId(Integer userId) {
        try {
            log.info("根据userId查询农田，userId: {}", userId);
            // 由于数据库中field表没有user_id字段，这里返回所有农田数据
            // 尝试使用更简单的查询方式
            log.info("尝试使用selectList(new QueryWrapper<>())查询所有记录");
            List<Field> fields = fieldMapper.selectList(new QueryWrapper<>());
            log.info("查询结果数量: {}", fields != null ? fields.size() : 0);
            return fields;
        } catch (Exception e) {
            log.error("根据userId查询农田失败: {}", e.getMessage(), e);
            // 捕获异常后返回空列表而不是抛出，避免上层控制器也抛出异常
            log.warn("捕获到异常，返回空列表");
            return java.util.Collections.emptyList();
        }
    }
    
    /**
     * 重写父类的list方法，添加详细日志
     */
    @Override
    public List<Field> list() {
        try {
            log.info("执行list()方法查询所有农田");
            // 直接调用fieldMapper的selectList方法
            List<Field> fields = fieldMapper.selectList(new QueryWrapper<>());
            log.info("list()方法查询结果数量: {}", fields != null ? fields.size() : 0);
            return fields;
        } catch (Exception e) {
            log.error("list()方法查询失败: {}", e.getMessage(), e);
            // 捕获异常后返回空列表
            log.warn("list()方法捕获到异常，返回空列表");
            return java.util.Collections.emptyList();
        }
    }

    @Override
    public Object getFieldStatistics(Integer fieldId) {
        log.info("获取农田统计信息，fieldId: {}", fieldId);
        
        // 获取农田基本信息
        Field field = fieldMapper.selectById(fieldId);
        if (field == null) {
            log.warn("农田不存在，fieldId: {}", fieldId);
            return null;
        }
        
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("fieldId", fieldId);
        statistics.put("fieldName", field.getName());
        statistics.put("area", field.getArea());
        statistics.put("latitude", field.getLatitude());
        statistics.put("longitude", field.getLongitude());
        
        // 构建位置字符串用于远程调用
        String location = String.format("%.4f,%.4f", field.getLatitude(), field.getLongitude());
        statistics.put("location", location);
        
        // 获取天气信息（远程调用）
        try {
            WeatherServiceClient.Result<WeatherServiceClient.WeatherData> weatherResult = 
                weatherServiceClient.getCurrentWeather(location);
            if (weatherResult != null && weatherResult.getCode() == 200 && weatherResult.getData() != null) {
                WeatherServiceClient.WeatherData weatherData = weatherResult.getData();
                Map<String, Object> weatherInfo = new HashMap<>();
                weatherInfo.put("temperature", weatherData.getTemperature());
                weatherInfo.put("humidity", weatherData.getHumidity());
                weatherInfo.put("windSpeed", weatherData.getWindSpeed());
                weatherInfo.put("weatherCondition", weatherData.getWeatherCondition());
                weatherInfo.put("updateTime", weatherData.getUpdateTime());
                statistics.put("currentWeather", weatherInfo);
                log.info("获取天气信息成功，fieldId: {}, 温度: {}", fieldId, weatherData.getTemperature());
            }
        } catch (Exception e) {
            log.warn("获取天气信息失败，fieldId: {}, 错误: {}", fieldId, e.getMessage());
        }
        
        // 获取病虫害预警信息（远程调用）
        try {
            PestServiceClient.Result<PestServiceClient.PestAlert[]> pestResult = 
                pestServiceClient.getPestAlerts(location);
            if (pestResult != null && pestResult.getCode() == 200 && pestResult.getData() != null) {
                PestServiceClient.PestAlert[] pestAlerts = pestResult.getData();
                statistics.put("pestAlerts", pestAlerts);
                statistics.put("pestAlertCount", pestAlerts.length);
                log.info("获取病虫害预警成功，fieldId: {}, 预警数量: {}", fieldId, pestAlerts.length);
            }
        } catch (Exception e) {
            log.warn("获取病虫害预警失败，fieldId: {}, 错误: {}", fieldId, e.getMessage());
        }
        
        // 获取病虫害统计信息（远程调用）
        try {
            PestServiceClient.Result<PestServiceClient.PestStatistics> pestStatsResult = 
                pestServiceClient.getPestStatistics(location);
            if (pestStatsResult != null && pestStatsResult.getCode() == 200 && pestStatsResult.getData() != null) {
                PestServiceClient.PestStatistics pestStats = pestStatsResult.getData();
                Map<String, Object> pestStatistics = new HashMap<>();
                pestStatistics.put("totalAlerts", pestStats.getTotalAlerts());
                pestStatistics.put("highSeverityAlerts", pestStats.getHighSeverityAlerts());
                pestStatistics.put("mediumSeverityAlerts", pestStats.getMediumSeverityAlerts());
                pestStatistics.put("lowSeverityAlerts", pestStats.getLowSeverityAlerts());
                pestStatistics.put("topPests", pestStats.getTopPests());
                pestStatistics.put("statisticsPeriod", pestStats.getStatisticsPeriod());
                statistics.put("pestStatistics", pestStatistics);
                log.info("获取病虫害统计成功，fieldId: {}, 总预警数: {}", fieldId, pestStats.getTotalAlerts());
            }
        } catch (Exception e) {
            log.warn("获取病虫害统计失败，fieldId: {}, 错误: {}", fieldId, e.getMessage());
        }
        
        log.info("农田统计信息获取完成，fieldId: {}", fieldId);
        return statistics;
    }
}
