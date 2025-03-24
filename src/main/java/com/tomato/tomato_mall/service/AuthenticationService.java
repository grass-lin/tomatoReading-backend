package com.tomato.tomato_mall.service;

import jakarta.servlet.http.HttpServletResponse;

/**
 * 认证服务接口
 * <p>
 * 该接口定义了与用户认证相关的辅助功能，主要处理HTTP响应中的认证信息。
 * 提供了JWT令牌的Cookie管理功能，包括设置认证Cookie和清除认证Cookie。
 * </p>
 * <p>
 * 接口的主要职责是管理认证会话状态，尤其是在基于Cookie的JWT认证机制中，
 * 负责令牌与客户端的安全交互。
 * </p>
 *
 * @author Team CBDDL
 * @version 1.0
 * @see com.tomato.tomato_mall.security.JwtAuthenticationFilter
 */
public interface AuthenticationService {

    /**
     * 设置认证Cookie
     * <p>
     * 将JWT令牌设置到HTTP响应的Cookie中，通常在用户登录成功后调用。
     * 设置的Cookie包含安全属性（HttpOnly等），以增强令牌的安全性。
     * </p>
     *
     * @param token JWT令牌字符串
     * @param response HTTP响应对象，用于添加Cookie
     */
    void setAuthenticationCookie(String token, HttpServletResponse response);

    /**
     * 清除认证Cookie
     * <p>
     * 从HTTP响应中移除认证Cookie，通常在用户登出时调用。
     * 该方法会创建一个同名但已过期的Cookie，覆盖客户端现有的认证Cookie。
     * </p>
     *
     * @param response HTTP响应对象，用于添加过期的Cookie
     */
    void clearAuthenticationCookie(HttpServletResponse response);
}