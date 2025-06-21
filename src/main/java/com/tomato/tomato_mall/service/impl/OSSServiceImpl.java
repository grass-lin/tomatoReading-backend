package com.tomato.tomato_mall.service.impl;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.auth.sts.AssumeRoleRequest;
import com.aliyuncs.auth.sts.AssumeRoleResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.tomato.tomato_mall.enums.ErrorTypeEnum;
import com.tomato.tomato_mall.enums.OSSFileTypeEnum;
import com.tomato.tomato_mall.exception.BusinessException;
import com.tomato.tomato_mall.properties.OSSProperties;
import com.tomato.tomato_mall.properties.STSProperties;
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
 * @see com.tomato.tomato_mall.properties.OSSProperties
 * @see com.tomato.tomato_mall.properties.STSProperties
 */
@Service
public class OSSServiceImpl implements OSSService {

    private final OSSProperties ossProperties;
    private final STSProperties stsProperties;

    /**
     * 构造函数，通过依赖注入初始化OSS服务组件
     * 
     * @param ossProperties OSS配置属性
     * @param stsProperties STS配置属性
     */
    public OSSServiceImpl(OSSProperties ossProperties, STSProperties stsProperties) {
        this.ossProperties = ossProperties;
        this.stsProperties = stsProperties;
    }

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