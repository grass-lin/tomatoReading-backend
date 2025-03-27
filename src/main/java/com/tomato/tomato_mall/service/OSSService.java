package com.tomato.tomato_mall.service;

import com.tomato.tomato_mall.enums.OSSFileTypeEnum;
import com.tomato.tomato_mall.vo.OSSTokenVO;

/**
 * 对象存储服务接口
 * <p>
 * 该接口定义了与对象存储服务(OSS)交互的业务功能，主要提供文件上传凭证生成等核心功能。
 * 作为系统文件存储的核心组件，负责安全、高效地管理用户上传的各类文件资源。
 * </p>
 * <p>
 * 接口的实现类通常需要与云存储服务(如阿里云OSS)的API进行交互，处理临时凭证的申请、
 * 权限控制策略的配置以及存储路径的规划等工作，确保文件上传过程的安全性和可靠性。
 * </p>
 *
 * @author Team CBDDL
 * @version 1.0
 * @see com.tomato.tomato_mall.enums.OSSFileTypeEnum
 * @see com.tomato.tomato_mall.vo.OSSTokenVO
 * @see com.tomato.tomato_mall.config.OSSProperties
 * @see com.tomato.tomato_mall.config.STSProperties
 */
public interface OSSService {

    /**
     * 生成OSS上传临时访问凭证
     * <p>
     * 根据指定的文件类型，生成用于客户端直传文件到OSS服务的临时访问凭证。
     * 该凭证包含有限时间和有限权限的AccessKey、SecurityToken等信息，
     * 可用于前端直接上传文件到OSS，无需通过应用服务器中转，提高传输效率。
     * </p>
     * <p>
     * 生成的凭证应当遵循最小权限原则，仅授权上传到指定目录的权限，并设置合理的过期时间，
     * 以保障系统安全。同时，凭证中还应包含文件存储的完整路径前缀信息，用于客户端构建上传请求。
     * </p>
     *
     * @param fileType 文件类型枚举，用于确定文件存储的目录分类
     * @return 包含临时访问凭证和存储路径信息的数据传输对象
     * @throws RuntimeException 当获取凭证过程中遇到网络错误或配置问题时可能抛出此异常
     */
    OSSTokenVO generateUploadToken(OSSFileTypeEnum fileType);
}