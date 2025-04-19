package com.tomato.tomato_mall.exception.handler;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.validation.FieldError;
import com.tomato.tomato_mall.exception.BusinessException;
import com.tomato.tomato_mall.enums.ErrorTypeEnum;
import com.tomato.tomato_mall.vo.ResponseVO;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler extends BaseExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ResponseVO<Void>> handleBusinessException(BusinessException ex) {
        if (ex.getArgs() != null) {
            return buildErrorResponse(ex.getErrorType(), ex.getArgs());
        }
        return buildErrorResponse(ex.getErrorType());
    }
       
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseVO<Map<String, String>>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        return buildErrorResponseWithData(ErrorTypeEnum.VALIDATION_ERROR, errors);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ResponseVO<Void>> handleNoResourceFoundException(NoResourceFoundException ex) {
        return buildErrorResponse(ErrorTypeEnum.RESOURCE_NOT_FOUND, ex.getHttpMethod(), ex.getResourcePath());
    }

    // 由 https://www.cnblogs.com/wang-yaz/p/13225830.html，默认的静态资源映射会有 /** 通配符，就不会抛出这个错误
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ResponseVO<Void>> handleNoHandlerFoundException(NoHandlerFoundException ex) {
        return buildErrorResponse(ErrorTypeEnum.HANDLER_NOT_FOUND, ex.getHttpMethod(), ex.getRequestURL());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ResponseVO<Void>> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException ex) {
        return buildErrorResponse(ErrorTypeEnum.METHOD_NOT_SUPPORTED, ex.getMethod());
    }
       
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseVO<Void>> handleGenericException(Exception ex) {
        System.err.println("An unexpected error " + "[" + ex + "]" + " occurred: " + ex.getMessage());
        return buildErrorResponse(ErrorTypeEnum.INTERNAL_SERVER_ERROR);
    }
}
