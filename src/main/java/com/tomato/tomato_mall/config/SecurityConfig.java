package com.tomato.tomato_mall.config;

import com.tomato.tomato_mall.security.JwtAuthenticationEntryPoint;
import com.tomato.tomato_mall.security.JwtAuthenticationFilter;
import com.tomato.tomato_mall.util.JwtUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 安全配置类
 * <p>
 * 该类负责配置应用程序的安全相关设置，包括：
 * - 认证和授权规则
 * - JWT令牌过滤器集成
 * - 会话管理策略
 * - 安全异常处理
 * - 密码加密方式
 * </p>
 * <p>
 * 配置采用了基于JWT的无状态认证机制，适用于前后端分离的REST API架构。
 * </p>
 *
 * @author Team CBDDL
 * @version 1.0
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

  private final JwtUtils jwtUtils;
  private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

  /**
   * 构造函数，通过依赖注入初始化安全组件
   *
   * @param jwtUtils                    JWT工具类，用于令牌的生成、验证和解析
   * @param jwtAuthenticationEntryPoint JWT认证入口点，处理未认证请求的响应
   */
  public SecurityConfig(JwtUtils jwtUtils, JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint) {
    this.jwtUtils = jwtUtils;
    this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
  }

  /**
   * 配置安全过滤器链
   * <p>
   * 定义HTTP安全规则：
   * - 禁用CSRF保护（因为使用无状态JWT认证）
   * - 配置公开端点和受保护端点
   * - 设置无状态会话管理
   * - 添加JWT认证过滤器
   * - 定制认证异常响应机制
   * </p>
   *
   * @param http HttpSecurity对象，用于构建安全配置
   * @return 配置完成的安全过滤器链
   * @throws Exception 如果安全配置过程中发生错误
   */
  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .cors(cors -> cors.configure(http))
        .csrf(csrf -> csrf.disable())
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(authorize -> authorize
            // 公开访问的端点
            .requestMatchers("/api/accounts", "/api/accounts/login", "/api/oss/avatar").permitAll()
            // 所有其他请求需要认证
            .anyRequest().authenticated())
        // 不使用session
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        // 添加JWT过滤器
        .addFilterBefore(new JwtAuthenticationFilter(jwtUtils), UsernamePasswordAuthenticationFilter.class)
        // 配置异常处理
        .exceptionHandling(exceptions -> exceptions
            .authenticationEntryPoint(jwtAuthenticationEntryPoint));

    return http.build();
  }

  /**
   * 配置密码编码器
   * <p>
   * 提供BCrypt密码散列算法实现，用于安全地存储和验证用户密码。
   * BCrypt自动包含随机盐值并支持适应性强度，能够有效防御彩虹表攻击和暴力破解。
   * </p>
   *
   * @return BCrypt密码编码器实例
   */
  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}