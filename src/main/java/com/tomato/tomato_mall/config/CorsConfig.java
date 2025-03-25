package com.tomato.tomato_mall.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * 跨域资源共享(CORS)配置类
 * <p>
 * 该类配置全局的CORS策略，允许所有来源的跨域请求访问后端API，
 * 适用于前后端分离架构中的跨域通信需求。
 * </p>
 * <p>
 * 当前配置允许：
 * - 所有来源/IP地址的请求
 * - 所有常见的HTTP方法（GET, POST, PUT, DELETE等）
 * - 所有常见的请求头
 * - 凭证信息（如Cookies）的跨域传递
 * </p>
 * 
 * @author Team CBDDL
 * @version 1.0
 */
@Configuration
public class CorsConfig {

  /**
   * 创建CORS过滤器
   * <p>
   * 该方法创建并配置一个CORS过滤器，用于处理所有跨域请求。
   * 过滤器应用于所有API路径，并允许来自任何源的请求。
   * </p>
   * 
   * @return 配置好的CORS过滤器
   */
  @Bean
  public CorsFilter corsFilter() {
    // 创建CORS配置
    CorsConfiguration config = new CorsConfiguration();

    // 允许来自任何域的请求
    config.addAllowedOriginPattern("*");

    // 允许携带凭证信息（如Cookies）
    config.setAllowCredentials(true);

    // 允许所有头信息
    config.addAllowedHeader("*");

    // 允许所有HTTP方法
    config.addAllowedMethod("*");

    // 预检请求的有效期，单位秒
    config.setMaxAge(3600L);

    // 应用CORS配置到所有路径
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);

    return new CorsFilter(source);
  }
}