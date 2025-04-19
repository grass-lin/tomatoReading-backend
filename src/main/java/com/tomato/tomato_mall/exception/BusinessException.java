package com.tomato.tomato_mall.exception;

import org.springframework.http.HttpStatus;
import com.tomato.tomato_mall.enums.ErrorTypeEnum;

public class BusinessException extends RuntimeException {

    private final ErrorTypeEnum errorType;
    private final Object[] args;

    public BusinessException(ErrorTypeEnum errorType) {
        this.args = null;
        this.errorType = errorType;
    }

    public BusinessException(ErrorTypeEnum errorType, Object... args) {
        this.args = args;
        this.errorType = errorType;
    }

    public ErrorTypeEnum getErrorType() {
        return errorType;
    }

    public HttpStatus getStatus() {
        return errorType.getStatus();
    }

    public Object[] getArgs() {
        return args;
    }
}
