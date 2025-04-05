package com.tomato.tomato_mall.exception.handler;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.core.annotation.Order;
import com.tomato.tomato_mall.exception.BusinessException;
import com.tomato.tomato_mall.vo.ResponseVO;

@RestControllerAdvice
@Order(1)
public class BusinessExceptionHandler extends BaseExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ResponseVO<Void>> handleOtherException(BusinessException ex) {
        return buildErrorResponse(ex.getErrorType());
    }
    
}
