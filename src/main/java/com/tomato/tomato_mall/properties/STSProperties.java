package com.tomato.tomato_mall.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 阿里云STS(Security Token Service)配置属性类
 * <p>
 * 该类用于从配置文件中读取STS相关配置
 * </p>
 *
 * @author Team CBDDL
 * @version 1.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "app.sts")
public class STSProperties {

    /**
     * 阿里云访问密钥ID
     */
    private String accessKeyId;

    /**
     * 阿里云访问密钥Secret
     */
    private String accessKeySecret;

    /**
     * STS服务端点地址
     */
    private String endpoint;

    /**
     * 阿里云服务区域ID
     */
    private String regionId;

    /**
     * RAM角色ARN
     */
    private String roleArn;

    /**
     * 临时访问令牌过期时间，单位：秒
     */
    private Long tokenExpireTime;
}