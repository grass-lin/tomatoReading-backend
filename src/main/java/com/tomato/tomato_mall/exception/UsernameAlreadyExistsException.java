package com.tomato.tomato_mall.exception;

/**
 * 用户名已存在异常
 * <p>
 * 该异常在用户注册或更新用户信息时，如果提交的用户名已被其他用户占用，则抛出此异常。
 * 作为业务逻辑异常，它表示了一种可预见的错误情况，通常在用户服务层({@code UserService})中抛出，
 * 并由全局异常处理器转换为适当的HTTP响应（通常是409 Conflict）。
 * </p>
 * <p>
 * 该异常继承自{@link RuntimeException}，属于非受检异常，调用者可以选择性地处理，
 * 也可以由全局异常处理机制统一捕获。
 * </p>
 *
 * @author Team CBDDL
 * @version 1.0
 * @see com.tomato.tomato_mall.service.UserService
 * @see com.tomato.tomato_mall.exception.GlobalExceptionHandler
 */
public class UsernameAlreadyExistsException extends RuntimeException {
    
    /**
     * 构造函数
     * <p>
     * 创建一个带有详细错误信息的用户名已存在异常。
     * 错误信息通常描述了冲突的具体情况，例如"用户名已被占用"。
     * </p>
     *
     * @param message 描述异常原因的详细信息
     */
    public UsernameAlreadyExistsException(String message) {
        super(message);
    }
}