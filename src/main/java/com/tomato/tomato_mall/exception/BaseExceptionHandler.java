package com.tomato.tomato_mall.exception;

import com.tomato.tomato_mall.enums.ErrorTypeEnum;
import com.tomato.tomato_mall.vo.ResponseVO;
import org.springframework.http.ResponseEntity;

/**
 * 异常处理器基类
 * <p>
 * 提供通用的错误响应构建方法，供各子类异常处理器使用。
 * </p>
 */
public abstract class BaseExceptionHandler {

    /**
     * 构建标准错误响应
     *
     * @param errorType 错误类型
     * @param args 错误消息格式化参数
     * @return 错误响应实体
     */
    protected ResponseEntity<ResponseVO<Void>> buildErrorResponse(ErrorTypeEnum errorType, Object... args) {
        String message = errorType.requiresParameters() ? 
                         errorType.getMessage(args) : errorType.getMessage();
        
        return ResponseEntity
                .status(errorType.getStatus())
                .body(ResponseVO.error(errorType.getStatus().value(), message));
    }
    
    /**
     * 构建带数据的错误响应
     *
     * @param <T> 数据类型
     * @param errorType 错误类型
     * @param data 错误详情数据
     * @param args 错误消息格式化参数
     * @return 带数据的错误响应实体
     */
    protected <T> ResponseEntity<ResponseVO<T>> buildErrorResponseWithData(
            ErrorTypeEnum errorType, T data, Object... args) {
        
        String message = errorType.requiresParameters() ? 
                         errorType.getMessage(args) : errorType.getMessage();
        
        return ResponseEntity
                .status(errorType.getStatus())
                .body(ResponseVO.error(errorType.getStatus().value(), message, data));
    }
}