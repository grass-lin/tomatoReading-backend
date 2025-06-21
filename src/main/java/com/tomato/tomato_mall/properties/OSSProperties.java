package com.tomato.tomato_mall.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 对象存储服务(OSS)配置属性类
 * <p>
 * 该类用于从配置文件中读取OSS相关配置
 * </p>
 */
@Data
@Component
@ConfigurationProperties(prefix = "app.oss")
public class OSSProperties {

    /**
     * OSS存储桶名称
     */
    private String bucketName;

    /**
     * OSS服务区域
     */
    private String region;

    /**
     * 文件存储基础路径
     */
    private String basePath;
}