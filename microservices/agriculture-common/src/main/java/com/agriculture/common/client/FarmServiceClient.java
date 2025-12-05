package com.agriculture.common.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 农场服务Feign客户端
 */
@FeignClient(name = "farm-service", url = "http://localhost:8083", path = "/api/farm")
public interface FarmServiceClient {

    /**
     * 根据农场ID获取农场信息
     */
    @GetMapping("/{farmId}")
    Result<Farm> getFarmById(@PathVariable("farmId") String farmId);

    /**
     * 获取附近农场信息
     */
    @GetMapping("/nearby/{location}")
    Result<Farm[]> getNearbyFarms(@PathVariable("location") String location, 
                                @RequestParam("radius") Double radius);

    /**
     * 农场信息响应类
     */
    class Farm {
        private String farmId;
        private String farmName;
        private String ownerId;
        private String location;
        private Double latitude;
        private Double longitude;
        private Double area;
        private String[] crops;
        private String status;
        private String createTime;

        // Getters and Setters
        public String getFarmId() { return farmId; }
        public void setFarmId(String farmId) { this.farmId = farmId; }
        public String getFarmName() { return farmName; }
        public void setFarmName(String farmName) { this.farmName = farmName; }
        public String getOwnerId() { return ownerId; }
        public void setOwnerId(String ownerId) { this.ownerId = ownerId; }
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        public Double getLatitude() { return latitude; }
        public void setLatitude(Double latitude) { this.latitude = latitude; }
        public Double getLongitude() { return longitude; }
        public void setLongitude(Double longitude) { this.longitude = longitude; }
        public Double getArea() { return area; }
        public void setArea(Double area) { this.area = area; }
        public String[] getCrops() { return crops; }
        public void setCrops(String[] crops) { this.crops = crops; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getCreateTime() { return createTime; }
        public void setCreateTime(String createTime) { this.createTime = createTime; }
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
