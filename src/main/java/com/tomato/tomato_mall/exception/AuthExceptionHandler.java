package com.tomato.tomato_mall.exception;

import com.tomato.tomato_mall.enums.ErrorTypeEnum;
import com.tomato.tomato_mall.vo.ResponseVO;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 认证与授权相关异常处理器
 * <p>
 * 处理与用户认证、授权和权限相关的异常。
 * </p>
 */
@RestControllerAdvice
@Order(1) 
public class AuthExceptionHandler extends BaseExceptionHandler {

    /**
     * 处理认证凭据错误异常
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ResponseVO<Void>> handleBadCredentialsException(BadCredentialsException ex) {
        return buildErrorResponse(ErrorTypeEnum.BAD_CREDENTIALS, ex.getMessage());
    }
    
    /**
     * 处理用户名已存在异常
     */
    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<ResponseVO<Void>> handleUsernameAlreadyExistsException(
            UsernameAlreadyExistsException ex) {
        return buildErrorResponse(ErrorTypeEnum.USERNAME_ALREADY_EXISTS, ex.getMessage());
    }
    
    /**
     * 处理权限不足异常
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ResponseVO<Void>> handleAccessDeniedException(AccessDeniedException ex) {
        return buildErrorResponse(ErrorTypeEnum.ACCESS_DENIED);
    }
}