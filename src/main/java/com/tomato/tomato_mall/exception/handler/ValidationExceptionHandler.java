package com.tomato.tomato_mall.exception.handler;

import com.tomato.tomato_mall.enums.ErrorTypeEnum;
import com.tomato.tomato_mall.vo.ResponseVO;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * 验证相关异常处理器
 * <p>
 * 处理与数据验证和参数校验相关的异常。
 * </p>
 */
@RestControllerAdvice
@Order(1)
public class ValidationExceptionHandler extends BaseExceptionHandler {

    /**
     * 处理请求参数验证失败异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseVO<Map<String, String>>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        
        // 收集所有字段验证错误
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        return buildErrorResponseWithData(ErrorTypeEnum.VALIDATION_ERROR, errors);
    }
    
}