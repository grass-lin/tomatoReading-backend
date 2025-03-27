package com.tomato.tomato_mall.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tomato.tomato_mall.vo.ResponseVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * JWT认证入口点
 * <p>
 * 该类负责处理未经身份验证的用户访问受保护资源时的异常响应。当用户尝试访问需要认证的资源，
 * 但未提供有效的JWT令牌或令牌无效时，Spring Security会调用此组件处理认证失败的情况。
 * </p>
 * <p>
 * 该组件实现了Spring Security的{@link AuthenticationEntryPoint}接口，用于定制化认证失败
 * 的响应体格式，确保API返回一致的JSON格式错误信息，而非默认的重定向到登录页面。
 * </p>
 * <p>
 * 认证失败响应会包含401状态码和标准格式的错误信息，便于前端应用统一处理认证相关异常。
 * </p>
 *
 * @author Team CBDDL
 * @version 1.0
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * JSON对象映射器
     * <p>
     * 用于将错误响应对象转换为JSON字符串。
     * </p>
     */
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 处理认证入口点
     * <p>
     * 当未认证的用户尝试访问受保护资源时，该方法会被Spring Security框架调用。
     * 方法负责生成一个标准化的401错误响应，包含JSON格式的错误信息。
     * </p>
     * <p>
     * 该实现确保了所有认证失败的请求都能得到一致的响应格式，便于客户端处理。
     * </p>
     *
     * @param request HTTP请求对象
     * @param response HTTP响应对象，用于写入错误信息
     * @param authException 认证异常，包含认证失败的详细原因
     * @throws IOException 如果响应写入过程中发生I/O异常
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        // response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        
        ResponseVO<?> errorResponse = ResponseVO.error(401, "Unauthorized: " + authException.getMessage());
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}