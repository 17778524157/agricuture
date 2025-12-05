package com.agriculture.common.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 业务异常
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ServiceException extends RuntimeException {
    
    private String code;
    private String message;
    
    public ServiceException(String message) {
        super(message);
        this.code = "500";
        this.message = message;
    }
    
    public ServiceException(Integer code, String message) {
        super(message);
        this.code = String.valueOf(code);
        this.message = message;
    }
    
    public ServiceException(String code, String message) {
        super(message);
        this.code = "500";
        this.message = message;
    }
}

