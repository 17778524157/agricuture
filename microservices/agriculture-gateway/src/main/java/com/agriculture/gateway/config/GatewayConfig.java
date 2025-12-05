package com.agriculture.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 网关路由配置
 */
@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // 用户服务路由
                .route("user-service", r -> r.path("/api/user/**")
                        .uri("lb://user-service"))
                // 菜单服务路由
                .route("menu-service", r -> r.path("/api/menu/**")
                        .uri("lb://user-service"))
                // 农田管理服务路由
                .route("farm-service", r -> r.path("/api/farm/**")
                        .uri("lb://farm-service"))
                // AI检测服务路由
                .route("ai-service", r -> r.path("/api/ai/**")
                        .uri("lb://ai-service"))
                // 通知公告服务路由
                .route("notification-service", r -> r.path("/api/notification/**", "/api/notice/**")
                        .uri("lb://notification-service"))
                // 积分商城服务路由
                .route("mall-service", r -> r.path("/api/mall/**")
                        .uri("lb://mall-service"))
                // 作物服务路由
                .route("crop-service", r -> r.path("/api/crop/**")
                        .uri("lb://crop-service"))
                // 田地服务路由
                .route("field-service", r -> r.path("/api/field/**")
                        .uri("lb://field-service"))
                // 病虫害服务路由
                .route("pest-service", r -> r.path("/api/pest/**")
                        .uri("lb://pest-service"))
                // 天气服务路由
                .route("weather-service", r -> r.path("/api/weather/**")
                        .uri("lb://weather-service"))
                // Python后端路由
                .route("python-backend", r -> r.path("/api/diagnose/**", "/api/diagnosis-history/**", "/api/ws/video/**")
                        .uri("http://localhost:8000"))
                // 单体应用路由
                .route("monolith-app", r -> r.path("/api/statistic/**", "/api/imserver/**")
                        .uri("http://localhost:9090"))
                // 文件服务路由
                .route("file-service", r -> r.path("/api/file/**")
                        .uri("lb://notification-service"))
                .build();
    }
}
