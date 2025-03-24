package com.tomato.tomato_mall.security;

import com.tomato.tomato_mall.util.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

/**
 * JWT认证过滤器
 * <p>
 * 该过滤器负责从HTTP请求中提取JWT令牌，验证其有效性，并设置Spring Security上下文中的认证信息。
 * 过滤器会按照以下顺序尝试获取令牌：
 * 1. 首先从名为"token"的Cookie中获取
 * 2. 其次从请求头的Authorization字段中获取（Bearer格式）
 * </p>
 * <p>
 * 过滤器继承自{@link OncePerRequestFilter}，确保每个请求只处理一次。这对于在多个过滤器链中
 * 避免重复处理特别重要。
 * </p>
 *
 * @author Team CBDDL
 * @version 1.0
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    /**
     * 构造函数
     * <p>
     * 通过依赖注入初始化JWT工具类，该工具类用于令牌验证和认证信息提取。
     * </p>
     *
     * @param jwtUtils JWT工具类，提供令牌验证和解析功能
     */
    public JwtAuthenticationFilter(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    /**
     * 过滤器核心处理逻辑
     * <p>
     * 该方法从请求中提取JWT令牌，验证其有效性，并在令牌有效的情况下设置认证上下文。
     * 无论令牌是否存在或有效，都会继续执行过滤器链，确保请求能够正常处理。
     * </p>
     * <p>
     * 认证流程：
     * 1. 从请求中提取令牌
     * 2. 验证令牌的合法性
     * 3. 如果令牌有效，从令牌中提取认证信息并设置到SecurityContext中
     * 4. 继续过滤器链的执行
     * </p>
     *
     * @param request HTTP请求对象
     * @param response HTTP响应对象
     * @param filterChain 过滤器链，用于继续请求的处理
     * @throws ServletException 如果处理过程中发生Servlet异常
     * @throws IOException 如果处理过程中发生I/O异常
     */
    @SuppressWarnings("null")
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        // 获取JWT令牌
        String token = getTokenFromRequest(request);
        
        // 验证令牌
        if (token != null && jwtUtils.validateToken(token)) {
            Authentication authentication = jwtUtils.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        
        filterChain.doFilter(request, response);
    }
    
    /**
     * 从HTTP请求中提取JWT令牌
     * <p>
     * 该方法按照优先级尝试从多个位置获取令牌：
     * 1. 首先检查名为"token"的Cookie
     * 2. 然后检查Authorization请求头，格式为"Bearer [token]"
     * </p>
     * <p>
     * 这种多重提取机制提供了更大的灵活性，同时支持前端和API客户端的不同认证方式。
     * </p>
     *
     * @param request HTTP请求对象
     * @return 提取的JWT令牌字符串；如果未找到则返回null
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        // 首先从Cookie中获取
        if (request.getCookies() != null) {
            Optional<Cookie> tokenCookie = Arrays.stream(request.getCookies())
                    .filter(cookie -> "token".equals(cookie.getName()))
                    .findFirst();
            
            if (tokenCookie.isPresent()) {
                return tokenCookie.get().getValue();
            }
        }
        
        // 其次从Authorization头获取
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        
        return null;
    }
}