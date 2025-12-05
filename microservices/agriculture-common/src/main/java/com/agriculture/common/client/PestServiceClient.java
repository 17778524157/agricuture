package com.agriculture.common.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 病虫害服务Feign客户端
 */
@FeignClient(name = "pest-service", url = "http://localhost:8085", path = "/api/pest")
public interface PestServiceClient {

    /**
     * 获取病虫害预警信息
     */
    @GetMapping("/alerts/{location}")
    Result<PestAlert[]> getPestAlerts(@PathVariable("location") String location);

    /**
     * 获取病虫害统计信息
     */
    @GetMapping("/statistics/{location}")
    Result<PestStatistics> getPestStatistics(@PathVariable("location") String location);

    /**
     * 病虫害预警响应类
     */
    class PestAlert {
        private String alertId;
        private String pestName;
        private String severity;
        private String location;
        private String description;
        private String alertTime;
        private String[] preventionMeasures;

        // Getters and Setters
        public String getAlertId() { return alertId; }
        public void setAlertId(String alertId) { this.alertId = alertId; }
        public String getPestName() { return pestName; }
        public void setPestName(String pestName) { this.pestName = pestName; }
        public String getSeverity() { return severity; }
        public void setSeverity(String severity) { this.severity = severity; }
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getAlertTime() { return alertTime; }
        public void setAlertTime(String alertTime) { this.alertTime = alertTime; }
        public String[] getPreventionMeasures() { return preventionMeasures; }
        public void setPreventionMeasures(String[] preventionMeasures) { this.preventionMeasures = preventionMeasures; }
    }

    /**
     * 病虫害统计响应类
     */
    class PestStatistics {
        private String location;
        private Integer totalAlerts;
        private Integer highSeverityAlerts;
        private Integer mediumSeverityAlerts;
        private Integer lowSeverityAlerts;
        private String[] topPests;
        private String statisticsPeriod;

        // Getters and Setters
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        public Integer getTotalAlerts() { return totalAlerts; }
        public void setTotalAlerts(Integer totalAlerts) { this.totalAlerts = totalAlerts; }
        public Integer getHighSeverityAlerts() { return highSeverityAlerts; }
        public void setHighSeverityAlerts(Integer highSeverityAlerts) { this.highSeverityAlerts = highSeverityAlerts; }
        public Integer getMediumSeverityAlerts() { return mediumSeverityAlerts; }
        public void setMediumSeverityAlerts(Integer mediumSeverityAlerts) { this.mediumSeverityAlerts = mediumSeverityAlerts; }
        public Integer getLowSeverityAlerts() { return lowSeverityAlerts; }
        public void setLowSeverityAlerts(Integer lowSeverityAlerts) { this.lowSeverityAlerts = lowSeverityAlerts; }
        public String[] getTopPests() { return topPests; }
        public void setTopPests(String[] topPests) { this.topPests = topPests; }
        public String getStatisticsPeriod() { return statisticsPeriod; }
        public void setStatisticsPeriod(String statisticsPeriod) { this.statisticsPeriod = statisticsPeriod; }
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
