package com.agriculture.gateway.filter;

import com.agriculture.gateway.service.GatewayRedisLoginService;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 认证全局过滤器
 */
@Slf4j
@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    private static final String SECRET_KEY = "agriculture-secret-key";
    private static final String TOKEN_HEADER = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";

    @Autowired
    private GatewayRedisLoginService redisLoginService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        String method = request.getMethod().name();
        
        // 跳过OPTIONS请求（CORS预检请求）
        if ("OPTIONS".equals(method)) {
            return chain.filter(exchange);
        }
        
        // 跳过认证的路径
        if (isSkipAuth(path)) {
            return chain.filter(exchange);
        }
        
        String token = getToken(request);
        if (!StringUtils.hasText(token)) {
            return unauthorized(exchange, "Token不能为空");
        }
        
        try {
            // 1. 检查Token是否在黑名单中
            if (redisLoginService.isTokenInBlacklist(token)) {
                log.warn("Token在黑名单中: {}", token);
                return unauthorized(exchange, "Token已失效，请重新登录");
            }
            
            // 2. 验证JWT Token
            Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
            JWTVerifier verifier = JWT.require(algorithm).build();
            verifier.verify(token);
            
            // 3. 检查用户会话是否存在
            String userSession = redisLoginService.getUserSession(token);
            if (userSession == null) {
                log.warn("用户会话不存在: {}", token);
                return unauthorized(exchange, "会话已过期，请重新登录");
            }
            
            // 4. 刷新用户会话过期时间
            redisLoginService.refreshUserSession(token);
            
            // Token验证通过，继续执行
            return chain.filter(exchange);
        } catch (JWTVerificationException e) {
            log.error("Token验证失败: {}", e.getMessage());
            return unauthorized(exchange, "Token无效或已过期");
        }
    }

    @Override
    public int getOrder() {
        return -100; // 优先级最高
    }

    /**
     * 判断是否跳过认证
     */
    private boolean isSkipAuth(String path) {
        return path.startsWith("/api/user/login") || 
               path.startsWith("/api/user/register") ||
               path.startsWith("/api/user/sendCode") ||  // 发送验证码
               path.startsWith("/api/user/phoneLogin") || // 手机号登录
               path.startsWith("/api/user/checkUsername") || // 检查用户名
               path.startsWith("/api/user/checkPhone") || // 检查手机号
               path.startsWith("/api/user/checkEmail") || // 检查邮箱
               path.startsWith("/api/user/personalInfo") || // 获取个人信息
               path.startsWith("/api/notification/public") ||
               path.startsWith("/api/notice/upload") || // 公告图片上传
               path.startsWith("/api/notice/page") || // 公告分页查询
               path.startsWith("/api/mall/") || // 商城API
               path.startsWith("/api/file/"); // 文件访问（图片、文档等）
    }

    /**
     * 获取Token
     */
    private String getToken(ServerHttpRequest request) {
        String authHeader = request.getHeaders().getFirst(TOKEN_HEADER);
        if (StringUtils.hasText(authHeader) && authHeader.startsWith(TOKEN_PREFIX)) {
            return authHeader.substring(TOKEN_PREFIX.length());
        }
        return null;
    }

    /**
     * 返回未授权响应
     */
    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 401);
        result.put("message", message);
        result.put("data", null);
        
        ObjectMapper mapper = new ObjectMapper();
        try {
            String body = mapper.writeValueAsString(result);
            DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
            return response.writeWith(Mono.just(buffer));
        } catch (Exception e) {
            log.error("序列化响应失败", e);
            return response.setComplete();
        }
    }
}
