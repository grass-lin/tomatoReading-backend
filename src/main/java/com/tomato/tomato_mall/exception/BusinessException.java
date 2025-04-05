package com.tomato.tomato_mall.exception;

import org.springframework.http.HttpStatus;
import com.tomato.tomato_mall.enums.ErrorTypeEnum;

public class BusinessException extends RuntimeException {

    private final ErrorTypeEnum errorType;

    public BusinessException(ErrorTypeEnum errorType) {
        super(errorType.getMessage());
        this.errorType = errorType;
    }

    public BusinessException(ErrorTypeEnum errorType, Object... args) {
        super(errorType.getMessage(args));
        this.errorType = errorType;
    }

    public ErrorTypeEnum getErrorType() {
        return errorType;
    }

    public HttpStatus getStatus() {
        return errorType.getStatus();
    }
}
