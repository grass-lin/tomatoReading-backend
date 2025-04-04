package com.tomato.tomato_mall.exception;

import com.tomato.tomato_mall.enums.ErrorTypeEnum;
import com.tomato.tomato_mall.vo.ResponseVO;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 通用异常处理器（兜底）
 * <p>
 * 处理所有其他处理器未捕获的异常，
 * 通过最低的优先级确保它在其他所有处理器之后执行。
 * </p>
 */
@RestControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
public class FallbackExceptionHandler extends BaseExceptionHandler {
    
    /**
     * 处理所有未明确捕获的其他异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseVO<Void>> handleGenericException(Exception ex) {
        System.err.println("An unexpected error occurred: " + ex.getMessage());
        return buildErrorResponse(ErrorTypeEnum.INTERNAL_SERVER_ERROR);
    }
}