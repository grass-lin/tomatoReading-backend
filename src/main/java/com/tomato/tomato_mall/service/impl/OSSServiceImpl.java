package com.tomato.tomato_mall.service.impl;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.auth.sts.AssumeRoleRequest;
import com.aliyuncs.auth.sts.AssumeRoleResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.tomato.tomato_mall.config.OSSProperties;
import com.tomato.tomato_mall.config.STSProperties;
import com.tomato.tomato_mall.enums.ErrorTypeEnum;
import com.tomato.tomato_mall.enums.OSSFileTypeEnum;
import com.tomato.tomato_mall.exception.BusinessException;
import com.tomato.tomato_mall.service.OSSService;
import com.tomato.tomato_mall.vo.OSSTokenVO;

import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * 对象存储服务实现类
 * <p>
 * 该类实现了{@link OSSService}接口，提供与阿里云对象存储服务(OSS)交互的核心功能。
 * 主要负责生成临时上传凭证，使客户端可以安全地将文件直接上传到OSS服务。
 * </p>
 * <p>
 * 实现基于阿里云STS(Security Token Service)服务，通过AssumeRole获取临时访问凭证，
 * 并通过策略控制上传权限和文件存储路径，确保安全可控的文件上传流程。
 * </p>
 * 
 * @author Team CBDDL
 * @version 1.0
 * @see com.tomato.tomato_mall.service.OSSService
 * @see com.tomato.tomato_mall.config.OSSProperties
 * @see com.tomato.tomato_mall.config.STSProperties
 */
@Service
public class OSSServiceImpl implements OSSService {

    private final OSSProperties ossProperties;
    private final STSProperties stsProperties;

    /**
     * 构造函数，通过依赖注入初始化OSS服务组件
     * 
     * @param ossProperties OSS配置属性，包含存储区域、存储桶名称等基础配置
     * @param stsProperties STS配置属性，包含访问密钥、角色ARN等安全认证配置
     */
    public OSSServiceImpl(OSSProperties ossProperties, STSProperties stsProperties) {
        this.ossProperties = ossProperties;
        this.stsProperties = stsProperties;
    }

    /**
     * 生成OSS上传临时访问凭证
     * <p>
     * 通过阿里云STS服务，为指定类型的文件生成临时访问凭证，允许客户端在有限时间内
     * 直接上传文件到OSS指定目录。生成的凭证具有严格的权限控制，仅允许执行PutObject操作。
     * </p>
     * <p>
     * 实现流程：
     * 1. 创建STS客户端并配置认证信息
     * 2. 构建AssumeRole请求，设置会话名称和有效期
     * 3. 根据文件类型生成唯一的存储目录路径
     * 4. 配置访问策略，限制仅能上传文件到指定目录
     * 5. 发送请求获取临时凭证
     * 6. 封装凭证信息并返回
     * </p>
     * 
     * @param fileType 文件类型枚举，决定了上传文件的存储目录分类
     * @return 包含临时访问凭证和存储信息的数据传输对象
     * @throws RuntimeException 当STS服务请求失败或配置错误时抛出，包装原始异常信息
     */
    @Override
    public OSSTokenVO generateUploadToken(OSSFileTypeEnum fileType) {
        if (fileType == null) {
            throw new BusinessException(ErrorTypeEnum.INVALID_FILE_TYPE, fileType);
        }
        try {
            String regionId = "";
            DefaultProfile.addEndpoint(regionId, "Sts", stsProperties.getEndpoint());
            // 创建STS客户端
            DefaultProfile profile = DefaultProfile.getProfile(regionId,
                    stsProperties.getAccessKeyId(),
                    stsProperties.getAccessKeySecret());
            DefaultAcsClient client = new DefaultAcsClient(profile);

            // 配置STS请求
            final AssumeRoleRequest request = new AssumeRoleRequest();
            request.setSysMethod(MethodType.POST);
            request.setRoleArn(stsProperties.getRoleArn());
            request.setRoleSessionName("tomato-reading-session-" + UUID.randomUUID().toString().substring(0, 8));
            request.setDurationSeconds(stsProperties.getTokenExpireTime());

            // 指定上传目录
            String directory = ossProperties.getBasePath() + "/" + fileType.getDirectory() + "/" +
                    UUID.randomUUID().toString().replace("-", "").substring(0, 8) + "/";

            String policy = "{\n" +
                    "    \"Version\": \"1\", \n" +
                    "    \"Statement\": [\n" +
                    "        {\n" +
                    "            \"Action\": [\n" +
                    "                \"oss:PutObject\"\n" +
                    "            ], \n" +
                    "            \"Resource\": [\n" +
                    "                \"acs:oss:*:*:" + ossProperties.getBucketName() + directory + "*\" \n" +
                    "            ], \n" +
                    "            \"Effect\": \"Allow\"\n" +
                    "        }\n" +
                    "    ]\n" +
                    "}";

            // 权限设置
            request.setPolicy(policy);

            // 发送请求获取STS凭证
            AssumeRoleResponse response = client.getAcsResponse(request);

            // 构建返回的DTO
            OSSTokenVO tokenDTO = new OSSTokenVO();
            tokenDTO.setRegion(ossProperties.getRegion());
            tokenDTO.setAccessKeyId(response.getCredentials().getAccessKeyId());
            tokenDTO.setAccessKeySecret(response.getCredentials().getAccessKeySecret());
            tokenDTO.setSecurityToken(response.getCredentials().getSecurityToken());
            tokenDTO.setBucket(ossProperties.getBucketName());
            tokenDTO.setFilePrefix(directory);
            tokenDTO.setExpiration(response.getCredentials().getExpiration());

            return tokenDTO;
        } catch (ClientException e) {
            System.err.println(e.getErrMsg());
            throw new BusinessException(ErrorTypeEnum.OSS_TOKEN_GENERATION_FAILED);
        }
    }
}