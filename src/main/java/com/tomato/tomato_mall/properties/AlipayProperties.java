package com.tomato.tomato_mall.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 支付宝配置属性类
 * <p>
 * 该类用于从配置文件中读取支付宝相关配置
 * 包括应用ID、密钥、网关地址等重要参数
 * </p>
 *
 * @author Team CBDDL
 * @version 1.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "app.alipay")
public class AlipayProperties {

    /**
     * 支付宝应用ID
     */
    private String appId;
    
    /**
     * 商户私钥
     */
    private String merchantPrivateKey;
    
    /**
     * 支付宝公钥
     */
    private String alipayPublicKey;
    
    /**
     * 支付宝网关地址
     */
    private String gatewayUrl;
    
    /**
     * 支付结果异步通知地址
     */
    private String notifyUrl;
    
    /**
     * 支付完成后前端跳转地址
     */
    private String returnUrl;
    
    /**
     * 签名类型，默认RSA2
     */
    private String signType = "RSA2";
    
    /**
     * 字符编码
     */
    private String charset = "utf-8";
    
    /**
     * 数据格式
     */
    private String format = "json";
    
    /**
     * 支付超时时间，单位：分钟
     */
    private String timeoutExpress = "30m";
}