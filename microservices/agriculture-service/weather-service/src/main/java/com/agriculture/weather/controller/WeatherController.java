package com.agriculture.weather.controller;

import com.agriculture.weather.entity.WeatherData;
import com.agriculture.weather.service.IWeatherDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/weather")
@Api(tags = "天气数据管理")
public class WeatherController {

    @Autowired
    private IWeatherDataService weatherDataService;

    @ApiOperation("根据农场ID获取天气数据")
    @GetMapping("/farm/{farmId}")
    public ResponseEntity<List<WeatherData>> getWeatherDataByFarmId(@PathVariable Integer farmId) {
        List<WeatherData> weatherDataList = weatherDataService.getWeatherDataByFarmId(farmId);
        return ResponseEntity.ok(weatherDataList);
    }

    @ApiOperation("根据农场ID和时间范围获取天气数据")
    @GetMapping("/farm/{farmId}/range")
    public ResponseEntity<List<WeatherData>> getWeatherDataByFarmIdAndTimeRange(
            @PathVariable Integer farmId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        List<WeatherData> weatherDataList = weatherDataService.getWeatherDataByFarmIdAndTimeRange(farmId, startTime, endTime);
        return ResponseEntity.ok(weatherDataList);
    }

    @ApiOperation("获取最新的天气数据")
    @GetMapping("/farm/{farmId}/latest")
    public ResponseEntity<WeatherData> getLatestWeatherData(@PathVariable Integer farmId) {
        WeatherData weatherData = weatherDataService.getLatestWeatherData(farmId);
        return ResponseEntity.ok(weatherData);
    }

    @ApiOperation("添加新的天气数据")
    @PostMapping
    public ResponseEntity<Boolean> addWeatherData(@RequestBody WeatherData weatherData) {
        boolean result = weatherDataService.addWeatherData(weatherData);
        return ResponseEntity.ok(result);
    }

    @ApiOperation("批量添加天气数据")
    @PostMapping("/batch")
    public ResponseEntity<Boolean> batchAddWeatherData(@RequestBody List<WeatherData> weatherDataList) {
        boolean result = weatherDataService.batchAddWeatherData(weatherDataList);
        return ResponseEntity.ok(result);
    }

    @ApiOperation("删除天气数据")
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteWeatherData(@PathVariable Long id) {
        boolean result = weatherDataService.removeById(id);
        return ResponseEntity.ok(result);
    }

    @ApiOperation("更新天气数据")
    @PutMapping
    public ResponseEntity<Boolean> updateWeatherData(@RequestBody WeatherData weatherData) {
        weatherData.setUpdateTime(LocalDateTime.now());
        boolean result = weatherDataService.updateById(weatherData);
        return ResponseEntity.ok(result);
    }

    @ApiOperation("获取所有天气数据")
    @GetMapping("/all")
    public ResponseEntity<List<WeatherData>> getAllWeatherData() {
        List<WeatherData> weatherDataList = weatherDataService.list();
        return ResponseEntity.ok(weatherDataList);
    }
}