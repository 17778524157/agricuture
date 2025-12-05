package com.agriculture.weather.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一响应实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Response<T> {

    private int code;
    private String message;
    private T data;
    private long timestamp;

    public Response(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    public static <T> Response<T> success(T data) {
        return new Response<>(200, "success", data);
    }

    public static <T> Response<T> success(String message, T data) {
        return new Response<>(200, message, data);
    }

    public static <T> Response<T> fail(int code, String message) {
        return new Response<>(code, message, null);
    }

    public static <T> Response<T> fail(String message) {
        return new Response<>(500, message, null);
    }

    public static <T> Response<T> badRequest(String message) {
        return new Response<>(400, message, null);
    }

    public static <T> Response<T> notFound(String message) {
        return new Response<>(404, message, null);
    }
}