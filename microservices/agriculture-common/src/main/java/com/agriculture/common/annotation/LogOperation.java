package com.agriculture.common.annotation;

import java.lang.annotation.*;

/**
 * 日志操作注解
 * 用于标记需要记录日志的方法
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogOperation {
    
    /**
     * 操作描述
     */
    String value() default "";
    
    /**
     * 操作类型
     */
    String type() default "OPERATION";
}
