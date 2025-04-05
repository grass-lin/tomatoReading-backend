package com.tomato.tomato_mall.enums;

import java.util.IllegalFormatException;

import org.springframework.http.HttpStatus;

/**
 * 错误类型枚举
 * <p>
 * 定义系统中所有标准化的错误类型，包括HTTP状态码和错误消息模板。
 * 支持两种类型的错误消息：
 * 1. 固定消息：直接使用预定义的错误消息
 * 2. 模板消息：包含占位符的消息模板，可在运行时填充具体值
 * </p>
 */
public enum ErrorTypeEnum {

    // BusinessException 

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "用户不存在"),

    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "订单不存在"),

    STOCKPILE_NOT_FOUND(HttpStatus.NOT_FOUND, "商品库存不存在"),

    CARTITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "购物车商品不存在"),

    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "商品不存在"),

    USERNAME_ALREADY_EXISTS(HttpStatus.CONFLICT, "用户名已存在"),

    PRODUCT_TITLE_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "商品标题已存在"),

    STOCKPILE_NOT_ENOUGH(HttpStatus.BAD_REQUEST, "商品库存不足"),

    CARTITEM_NOT_BELONG_TO_USER(HttpStatus.BAD_REQUEST, "购物车商品不属于当前用户"),

    PAYMENT_AMOUNT_MISMATCH(HttpStatus.BAD_REQUEST, "支付金额不匹配"),

    STOCKPILE_AMOUNT_CANNOT_BE_LESS_THAN_FROZEN_AMOUNT(HttpStatus.BAD_REQUEST, "库存数量不能少于冻结数量"),

    INCORRECT_PASSWORD(HttpStatus.BAD_REQUEST, "用户密码错误"),

    ACCESS_DENIED(HttpStatus.FORBIDDEN, "没有权限访问该资源"), //但是我看代码中没有 throw 过




    // MethodArgumentNotValidException
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "字段不合法"),
    
    // Other
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "服务器内部错误"),;

    private final HttpStatus status;
    private final String messageTemplate;
    private final boolean requiresParameters;

    ErrorTypeEnum(HttpStatus status, String messageTemplate) {
        this.status = status;
        this.messageTemplate = messageTemplate;
        this.requiresParameters = messageTemplate.contains("%s");
    }

    /**
     * 获取HTTP状态码
     */
    public HttpStatus getStatus() {
        return status;
    }

    /**
     * 判断是否需要参数来格式化消息
     * 
     * @return 如果消息模板包含占位符需要参数，返回true；否则返回false
     */
    public boolean requiresParameters() {
        return requiresParameters;
    }
    
    /**
     * 获取固定的错误消息
     * 
     * @return 错误消息
     * @throws IllegalStateException 如果此错误类型需要参数但未提供
     */
    public String getMessage() {
        if (requiresParameters) {
            throw new IllegalStateException("此错误类型 (" + this.name() + ") 需要参数！请使用 getMessage(Object...) 方法并提供所需参数。");
        }
        return messageTemplate;
    }

    /**
     * 获取格式化后的错误消息
     * 
     * @param args 要填充到消息模板中的参数
     * @return 格式化后的错误消息
     * @throws IllegalStateException 如果格式化消息失败
     */
    public String getMessage(Object... args) {
        try {
            return String.format(messageTemplate, args);
        } catch (IllegalFormatException e) {
            throw new IllegalStateException("消息格式化错误: " + messageTemplate, e);
        }
    }
}
