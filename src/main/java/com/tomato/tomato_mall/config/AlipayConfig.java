package com.tomato.tomato_mall.config;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 支付宝配置类
 * <p>
 * 该类负责创建支付宝API调用所需的核心组件。
 * 通过Spring配置管理，确保AlipayClient实例只被创建一次并在整个应用中共享，
 * 符合单例设计模式和依赖注入的最佳实践。
 * </p>
 */
@Configuration
public class AlipayConfig {

    private final AlipayProperties alipayProperties;

    /**
     * 构造函数依赖注入
     * 
     * @param alipayProperties 支付宝配置属性
     */
    public AlipayConfig(AlipayProperties alipayProperties) {
        this.alipayProperties = alipayProperties;
    }

    /**
     * 创建支付宝客户端实例
     * 
     * <p>
     * AlipayClient是调用支付宝API的核心类，使用建造者模式创建，
     * 通过Spring Bean管理确保线程安全和资源高效利用
     * </p>
     * 
     * @return AlipayClient 支付宝客户端实例
     */
    @Bean
    public AlipayClient alipayClient() {
        return new DefaultAlipayClient(
                alipayProperties.getGatewayUrl(),
                alipayProperties.getAppId(),
                alipayProperties.getMerchantPrivateKey(),
                alipayProperties.getFormat(),
                alipayProperties.getCharset(),
                alipayProperties.getAlipayPublicKey(),
                alipayProperties.getSignType());
    }
}