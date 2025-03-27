package com.tomato.tomato_mall.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.oss")
public class OSSProperties {

    private String bucketName;

    private String region;

    private String basePath;
}