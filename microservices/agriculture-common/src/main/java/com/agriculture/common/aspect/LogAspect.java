package com.agriculture.common.aspect;

import com.agriculture.common.annotation.LogOperation;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 日志切面
 * 统一处理操作日志记录
 */
@Slf4j
@Aspect
@Component
public class LogAspect {

    @Autowired
    private ObjectMapper objectMapper;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 定义切点：所有带有 @LogOperation 注解的方法
     */
    @Pointcut("@annotation(com.agriculture.common.annotation.LogOperation)")
    public void logPointcut() {
    }

    /**
     * 环绕通知：在方法执行前后进行日志记录
     */
    @Around("logPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        
        // 获取请求信息
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes != null ? attributes.getRequest() : null;
        
        // 获取方法签名和注解信息
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        LogOperation logOperation = method.getAnnotation(LogOperation.class);
        
        // 获取类名、方法名
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = method.getName();
        
        // 获取请求参数
        Object[] args = joinPoint.getArgs();
        String params = getParams(args);
        
        // 获取用户信息（从请求头或token中获取）
        String username = getUsername(request);
        
        // 记录请求日志
        String logType = logOperation != null ? logOperation.type() : "OPERATION";
        String description = logOperation != null ? logOperation.value() : methodName;
        
        LogInfo logInfo = LogInfo.builder()
                .className(className)
                .methodName(methodName)
                .description(description)
                .type(logType)
                .requestParams(params)
                .username(username)
                .requestTime(DATE_FORMAT.format(new Date()))
                .ip(getIpAddress(request))
                .build();
        
        log.info("请求开始 - {}", logInfo);
        
        Object result = null;
        Exception exception = null;
        
        try {
            // 执行目标方法
            result = joinPoint.proceed();
            
            // 记录响应日志
            long endTime = System.currentTimeMillis();
            long costTime = endTime - startTime;
            
            String response = result != null ? objectMapper.writeValueAsString(result) : "null";
            
            logInfo.setResponseTime(DATE_FORMAT.format(new Date()));
            logInfo.setCostTime(costTime + "ms");
            logInfo.setResponse(response);
            logInfo.setStatus("SUCCESS");
            
            log.info("请求成功 - {}", logInfo);
            
            return result;
            
        } catch (Exception e) {
            // 记录异常日志
            long endTime = System.currentTimeMillis();
            long costTime = endTime - startTime;
            
            exception = e;
            logInfo.setResponseTime(DATE_FORMAT.format(new Date()));
            logInfo.setCostTime(costTime + "ms");
            logInfo.setStatus("FAILED");
            logInfo.setErrorMsg(e.getMessage());
            
            log.error("请求失败 - {}", logInfo, e);
            
            throw e;
        }
    }

    /**
     * 获取方法参数
     */
    private String getParams(Object[] args) {
        if (args == null || args.length == 0) {
            return "[]";
        }
        try {
            return objectMapper.writeValueAsString(args);
        } catch (Exception e) {
            return "参数序列化失败: " + e.getMessage();
        }
    }

    /**
     * 获取用户名（从请求头或token中获取）
     */
    private String getUsername(HttpServletRequest request) {
        if (request == null) {
            return "SYSTEM";
        }
        
        // 从请求头中获取用户名
        String username = request.getHeader("username");
        if (username != null && !username.isEmpty()) {
            return username;
        }
        
        // 从token中解析用户名（如果有的话）
        String token = request.getHeader("token");
        if (token != null && !token.isEmpty()) {
            // 这里可以解析token获取用户信息
            // 暂时返回TOKEN_USER
            return "TOKEN_USER";
        }
        
        return "ANONYMOUS";
    }

    /**
     * 获取IP地址
     */
    private String getIpAddress(HttpServletRequest request) {
        if (request == null) {
            return "UNKNOWN";
        }
        
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /**
     * 日志信息实体
     */
    @lombok.Data
    @lombok.Builder
    private static class LogInfo {
        private String className;
        private String methodName;
        private String description;
        private String type;
        private String requestParams;
        private String response;
        private String username;
        private String requestTime;
        private String responseTime;
        private String costTime;
        private String ip;
        private String status;
        private String errorMsg;

        @Override
        public String toString() {
            Map<String, Object> map = new HashMap<>();
            map.put("类名", className);
            map.put("方法名", methodName);
            map.put("操作描述", description);
            map.put("操作类型", type);
            map.put("请求参数", requestParams);
            map.put("响应结果", response);
            map.put("用户名", username);
            map.put("请求时间", requestTime);
            map.put("响应时间", responseTime);
            map.put("耗时", costTime);
            map.put("IP地址", ip);
            map.put("状态", status);
            if (errorMsg != null) {
                map.put("错误信息", errorMsg);
            }
            
            try {
                ObjectMapper mapper = new ObjectMapper();
                return mapper.writeValueAsString(map);
            } catch (Exception e) {
                return map.toString();
            }
        }
    }
}
