package com.tomato.tomato_mall.vo;

import lombok.Data;

/**
 * 对象存储服务令牌视图对象
 * <p>
 * 该类封装了用于客户端直接上传文件到对象存储服务(OSS)所需的临时凭证信息。
 * 作为控制器层返回给客户端的数据载体，提供安全可控的OSS访问凭证。
 * </p>
 * <p>
 * 包含了区域、访问密钥、安全令牌和存储路径等关键信息，使前端能够直接与OSS服务交互，
 * 实现文件的高效上传，而无需通过应用服务器中转。主要用于用户头像、产品图片等文件上传场景。
 * </p>
 *
 * @author Team CBDDL
 * @version 1.0
 */
@Data
public class OSSTokenVO {

    /**
     * OSS服务区域
     * <p>
     * 对象存储服务的地理区域标识。
     * </p>
     */
    private String region;

    /**
     * 访问密钥ID
     * <p>
     * 临时访问凭证的AccessKey ID，用于身份验证。
     * </p>
     */
    private String accessKeyId;
    
    /**
     * 访问密钥密文
     * <p>
     * 临时访问凭证的AccessKey Secret，与AccessKey ID配对使用。
     * </p>
     */
    private String accessKeySecret;

    /**
     * 安全令牌
     * <p>
     * STS服务颁发的临时安全令牌，用于验证用户访问权限。
     * </p>
     */
    private String securityToken;

    /**
     * 过期时间
     * <p>
     * 临时访问凭证的失效时间，格式为ISO8601标准时间字符串。
     * </p>
     */
    private String expiration;

    /**
     * 存储桶名称
     * <p>
     * OSS的存储空间名称，文件将上传到此存储桶中。
     * </p>
     */
    private String bucket;

    /**
     * 文件路径前缀
     * <p>
     * 上传文件在OSS中的存储路径前缀，通常包含文件类型目录和随机生成的子目录。
     * 客户端需要将此前缀与文件名拼接，形成完整的OSS对象键(Object Key)。
     * </p>
     */
    private String filePrefix;
}