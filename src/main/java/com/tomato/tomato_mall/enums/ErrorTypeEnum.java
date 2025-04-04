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
    // 认证错误 401更好
    BAD_CREDENTIALS(HttpStatus.BAD_REQUEST, "%s"),

    // 权限错误
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "Forbidden: Access Denied"),

    // 用户名冲突错误
    USERNAME_ALREADY_EXISTS(HttpStatus.CONFLICT, "%s"),


    
    // 参数错误
    ILLEGAL_ARGUMENT(HttpStatus.BAD_REQUEST, "%s"),
    
    // 验证错误
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "Validation failed"),
    

    
    // 路径不存在错误
    PATH_NOT_FOUND(HttpStatus.NOT_FOUND, "The requested path '%s' does not exist"),
    
    // 资源不存在错误
    RESOURCE_FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "The requested resource '%s' does not exist"),

     // 资源不存在错误
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "%s"),   



    // 通用服务器错误
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");

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
