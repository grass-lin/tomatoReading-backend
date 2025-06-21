package com.tomato.tomato_mall.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT配置属性类
 * <p>
 * 该类用于从配置文件中读取JWT相关配置
 * </p>
 */
@Component
@ConfigurationProperties(prefix = "app.jwt")
@Data
public class JwtProperties {

    /**
     * JWT签名密钥
     */
    private String secret;

    /**
     * JWT过期时间，单位：毫秒
     */
    private long expiration;
}