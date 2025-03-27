package com.tomato.tomato_mall.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一响应数据封装类
 * <p>
 * 该类提供了一种标准化的HTTP响应格式，用于所有REST API的返回值封装。
 * 包含状态码、消息和数据三个字段，便于前端统一处理响应结果。
 * </p>
 * <p>
 * 响应结构示例：
 * {
 *   "code": 200,
 *   "msg": "Success",
 *   "data": { ... }
 * }
 * </p>
 * <p>
 * 通过静态工厂方法可以快速创建成功和错误响应，简化控制器代码。
 * </p>
 *
 * @author Team CBDDL
 * @version 1.0
 * @param <T> 响应数据的类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseVO<T> {
    /**
     * 响应状态码
     * <p>
     * 常用状态码：
     * - 200：成功
     * - 400：请求参数错误
     * - 401：未授权
     * - 403：禁止访问
     * - 404：资源不存在
     * - 409：资源冲突
     * - 500：服务器内部错误
     * </p>
     */
    private String code;
    // private Integer code;

    /**
     * 响应消息
     * <p>
     * 对响应结果的文字描述，成功时通常为"Success"，
     * 失败时提供具体的错误信息。
     * </p>
     */
    private String msg;

    /**
     * 响应数据
     * <p>
     * 成功时返回的业务数据，类型由泛型T指定。
     * 失败时通常为null，但某些错误场景可能包含错误详情。
     * </p>
     */
    private T data;

    /**
     * 创建成功响应
     * <p>
     * 生成一个标准的成功响应对象，状态码固定为200，消息为"Success"。
     * </p>
     *
     * @param data 响应数据
     * @param <T> 数据类型
     * @return 包含状态码、成功消息和数据的响应对象
     */
    public static <T> ResponseVO<T> success(T data) {
        return new ResponseVO<>("200",null, data);
        // return new ResponseVO<>(200, "Success", data);
    }

    /**
     * 创建错误响应（无数据）
     * <p>
     * 生成一个标准的错误响应对象，包含自定义状态码和错误消息，数据为null。
     * </p>
     *
     * @param code 错误状态码
     * @param message 错误消息
     * @param <T> 数据类型
     * @return 包含错误状态码、错误消息的响应对象
     */
    public static <T> ResponseVO<T> error(Integer code, String message) {
        return new ResponseVO<>("400", message, null);
        // return new ResponseVO<>(code, message, null);
    }
    
    /**
     * 创建错误响应（包含错误数据）
     * <p>
     * 生成一个标准的错误响应对象，包含自定义状态码、错误消息和错误相关数据。
     * 常用于需要返回详细错误信息的场景，如表单验证错误。
     * </p>
     *
     * @param code 错误状态码
     * @param message 错误消息
     * @param data 错误相关数据
     * @param <T> 数据类型
     * @return 包含错误状态码、错误消息和错误数据的响应对象
     */
    public static <T> ResponseVO<T> error(Integer code, String message, T data) {
        return new ResponseVO<>("400", message, data);
        // return new ResponseVO<>(code, message, data);
    }
}