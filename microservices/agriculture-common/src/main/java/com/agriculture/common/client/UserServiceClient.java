package com.agriculture.common.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 用户服务Feign客户端
 */
@FeignClient(name = "user-service", url = "http://localhost:8080", path = "/api/user")
public interface UserServiceClient {

    /**
     * 根据用户ID获取用户信息
     */
    @GetMapping("/{userId}")
    Result<User> getUserById(@PathVariable("userId") String userId);

    /**
     * 根据用户ID获取用户联系方式
     */
    @GetMapping("/contact/{userId}")
    Result<UserContact> getUserContact(@PathVariable("userId") String userId);

    /**
     * 根据用户ID获取用户偏好
     */
    @GetMapping("/preferences/{userId}")
    Result<UserPreferences> getUserPreferences(@PathVariable("userId") String userId);

    /**
     * 用户信息响应类
     */
    class User {
        private String id;
        private String username;
        private String email;
        private String phone;
        private String avatar;
        private String address;
        private String farmId;

        // Getters and Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getAvatar() { return avatar; }
        public void setAvatar(String avatar) { this.avatar = avatar; }
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
        public String getFarmId() { return farmId; }
        public void setFarmId(String farmId) { this.farmId = farmId; }
    }

    /**
     * 用户联系方式响应类
     */
    class UserContact {
        private String userId;
        private String email;
        private String phone;
        private String wechat;
        private String qq;

        // Getters and Setters
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getWechat() { return wechat; }
        public void setWechat(String wechat) { this.wechat = wechat; }
        public String getQq() { return qq; }
        public void setQq(String qq) { this.qq = qq; }
    }

    /**
     * 用户偏好响应类
     */
    class UserPreferences {
        private String userId;
        private String[] preferredCrops;
        private String[] preferredRegions;
        private String notificationType;
        private String language;

        // Getters and Setters
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String[] getPreferredCrops() { return preferredCrops; }
        public void setPreferredCrops(String[] preferredCrops) { this.preferredCrops = preferredCrops; }
        public String[] getPreferredRegions() { return preferredRegions; }
        public void setPreferredRegions(String[] preferredRegions) { this.preferredRegions = preferredRegions; }
        public String getNotificationType() { return notificationType; }
        public void setNotificationType(String notificationType) { this.notificationType = notificationType; }
        public String getLanguage() { return language; }
        public void setLanguage(String language) { this.language = language; }
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
