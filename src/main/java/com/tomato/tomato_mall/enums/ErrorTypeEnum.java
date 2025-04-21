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
    // 不存在 oly的版本中NoSuchElementException的http状态码是404，body中的code是400，所以我直接全部改成400了
    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "用户不存在"),

    ORDER_NOT_FOUND(HttpStatus.BAD_REQUEST, "订单不存在"),

    ORDER_ITEM_NOT_FOUND(HttpStatus.BAD_REQUEST, "订单项不存在"),

    STOCKPILE_NOT_FOUND(HttpStatus.BAD_REQUEST, "商品库存不存在"),

    CARTITEM_NOT_FOUND(HttpStatus.BAD_REQUEST, "购物车商品不存在"),

    PRODUCT_NOT_FOUND(HttpStatus.BAD_REQUEST, "商品不存在"),

    ADVERTISEMENT_NOT_FOUND(HttpStatus.BAD_REQUEST, "广告不存在"),

    SHIPPING_ADDRESS_NOT_FOUND(HttpStatus.BAD_REQUEST, "收货信息不存在"),

    LOGISTICS_NOT_FOUND(HttpStatus.BAD_REQUEST, "物流信息不存在"),

    CONVERSATION_NOT_FOUND(HttpStatus.BAD_REQUEST, "会话不存在"),


    // 不属于
    USER_NOT_BELONG_TO_USER(HttpStatus.BAD_REQUEST, "不能访问其他用户的信息"),

    CARTITEM_NOT_BELONG_TO_USER(HttpStatus.BAD_REQUEST, "购物车商品不属于当前用户"),

    ORDER_NOT_BELONG_TO_USER(HttpStatus.BAD_REQUEST, "订单不属于当前用户"),

    ORDER_ITEM_NOT_BELONG_TO_USER(HttpStatus.BAD_REQUEST, "订单项不属于当前用户"),

    SHIPPING_ADDRESS_NOT_BELONG_TO_USER(HttpStatus.BAD_REQUEST, "收货信息不属于当前用户"),

    CONVERSATION_NOT_BELONG_TO_USER(HttpStatus.BAD_REQUEST, "会话不属于当前用户"),


    // OSS
    INVALID_FILE_TYPE(HttpStatus.BAD_REQUEST, "无效的文件类型: %s"),
    
    OSS_TOKEN_GENERATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "OSS 凭证生成失败"),


    USERNAME_ALREADY_EXISTS(HttpStatus.CONFLICT, "用户名已存在"),

    INCORRECT_PASSWORD(HttpStatus.BAD_REQUEST, "用户密码错误"), // Bad practice


    STOCKPILE_NOT_ENOUGH(HttpStatus.BAD_REQUEST, "商品库存不足"),

    CARTITEM_STATUS_ERROR(HttpStatus.BAD_REQUEST, "购物车商品状态错误"),

    ORDER_STATUS_NOT_ALLOW_CANCEL(HttpStatus.BAD_REQUEST, "订单状态不允许取消"),
    
    ORDER_STATUS_NOT_ALLOW_PAY(HttpStatus.BAD_REQUEST, "订单状态不允许支付"),

    PRODUCT_OCCUPIED_BY_ORDER(HttpStatus.BAD_REQUEST, "商品已被订单占用，无法删除"),

    CREATE_PAY_FORM_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "创建支付表单失败"),

    ORDER_ITEM_STATUS_ERROR(HttpStatus.BAD_REQUEST, "订单商品状态错误"),

    LOGISTICS_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "物流信息已存在"),

    RESPONSE_FETCH_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "获取回复失败"),


    // NoResourceFoundException
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "请求的资源不存在: %s %s"),

    // NoHandlerFoundException
    HANDLER_NOT_FOUND(HttpStatus.NOT_FOUND, "请求的处理器不存在: %s %s"),

    // HttpRequestMethodNotSupportedException
    METHOD_NOT_SUPPORTED(HttpStatus.METHOD_NOT_ALLOWED, "请求方法不支持: %s"),

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
