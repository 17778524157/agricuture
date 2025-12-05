package com.agriculture.common.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 天气服务Feign客户端
 */
@FeignClient(name = "weather-service", url = "http://localhost:8084", path = "/api/weather")
public interface WeatherServiceClient {

    /**
     * 获取当前天气信息
     */
    @GetMapping("/current/{location}")
    Result<WeatherData> getCurrentWeather(@PathVariable("location") String location);

    /**
     * 获取天气预报
     */
    @GetMapping("/forecast/{location}")
    Result<WeatherForecast> getWeatherForecast(@PathVariable("location") String location);

    /**
     * 获取历史天气数据
     */
    @GetMapping("/history/{location}")
    Result<WeatherHistory> getWeatherHistory(@PathVariable("location") String location, 
                                           @RequestParam("days") Integer days);

    /**
     * 天气数据响应类
     */
    class WeatherData {
        private String location;
        private Double temperature;
        private Integer humidity;
        private Double windSpeed;
        private String weatherCondition;
        private String updateTime;

        // Getters and Setters
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        public Double getTemperature() { return temperature; }
        public void setTemperature(Double temperature) { this.temperature = temperature; }
        public Integer getHumidity() { return humidity; }
        public void setHumidity(Integer humidity) { this.humidity = humidity; }
        public Double getWindSpeed() { return windSpeed; }
        public void setWindSpeed(Double windSpeed) { this.windSpeed = windSpeed; }
        public String getWeatherCondition() { return weatherCondition; }
        public void setWeatherCondition(String weatherCondition) { this.weatherCondition = weatherCondition; }
        public String getUpdateTime() { return updateTime; }
        public void setUpdateTime(String updateTime) { this.updateTime = updateTime; }
    }

    /**
     * 天气预报响应类
     */
    class WeatherForecast {
        private String location;
        private WeatherData[] forecastData;
        private String forecastPeriod;

        // Getters and Setters
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        public WeatherData[] getForecastData() { return forecastData; }
        public void setForecastData(WeatherData[] forecastData) { this.forecastData = forecastData; }
        public String getForecastPeriod() { return forecastPeriod; }
        public void setForecastPeriod(String forecastPeriod) { this.forecastPeriod = forecastPeriod; }
    }

    /**
     * 历史天气数据响应类
     */
    class WeatherHistory {
        private String location;
        private WeatherData[] historyData;
        private String period;

        // Getters and Setters
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        public WeatherData[] getHistoryData() { return historyData; }
        public void setHistoryData(WeatherData[] historyData) { this.historyData = historyData; }
        public String getPeriod() { return period; }
        public void setPeriod(String period) { this.period = period; }
    }

    /**
     * 通用响应结果类
     */
    class Result<T> {
        private Integer code;
        private String message;
        private T data;

        // Getters and Setters
        public Integer getCode() { return code; }
        public void setCode(Integer code) { this.code = code; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public T getData() { return data; }
        public void setData(T data) { this.data = data; }
    }
}
