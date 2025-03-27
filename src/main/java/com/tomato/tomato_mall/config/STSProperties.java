package com.tomato.tomato_mall.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.sts")
public class STSProperties {

    private String accessKeyId;

    private String accessKeySecret;
    
    private String endpoint;
    
    private String regionId;

    private String roleArn;

    private Long tokenExpireTime;
}