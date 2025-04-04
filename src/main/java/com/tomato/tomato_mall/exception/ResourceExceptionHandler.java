package com.tomato.tomato_mall.exception;

import com.tomato.tomato_mall.enums.ErrorTypeEnum;
import com.tomato.tomato_mall.vo.ResponseVO;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.NoSuchElementException;

/**
 * 资源相关异常处理器
 * <p>
 * 处理与资源访问、路径查找相关的异常。
 * </p>
 */
@RestControllerAdvice
@Order(1)
public class ResourceExceptionHandler extends BaseExceptionHandler {
    
    /**
     * 处理资源不存在异常
     */
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ResponseVO<Void>> handleNoSuchElementException(NoSuchElementException ex) {
        return buildErrorResponse(ErrorTypeEnum.RESOURCE_NOT_FOUND, ex.getMessage());
    }
    
    /**
     * 处理请求路径不存在异常
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ResponseVO<Void>> handleNoHandlerFoundException(NoHandlerFoundException ex) {
        return buildErrorResponse(ErrorTypeEnum.PATH_NOT_FOUND, ex.getRequestURL());
    }
    
    /**
     * 处理静态资源不存在异常
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ResponseVO<Void>> handleNoResourceFoundException(NoResourceFoundException ex) {
        return buildErrorResponse(ErrorTypeEnum.RESOURCE_FILE_NOT_FOUND, ex.getResourcePath());
    }
}