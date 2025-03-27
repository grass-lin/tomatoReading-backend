package com.tomato.tomato_mall.controller;

import com.tomato.tomato_mall.enums.OSSFileTypeEnum;
import com.tomato.tomato_mall.service.OSSService;
import com.tomato.tomato_mall.vo.OSSTokenVO;
import com.tomato.tomato_mall.vo.ResponseVO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 对象存储服务控制器
 * <p>
 * 提供与对象存储服务(OSS)相关的REST API接口，包括获取上传令牌等功能
 * 所有接口返回统一的ResponseVO格式，包含状态码、消息和数据
 * </p>
 * 
 * @author Team CBDDL
 * @version 1.0
 */
@RestController
@RequestMapping("/api/oss")
public class OSSController {

    private final OSSService ossService;

    /**
     * 构造函数，通过依赖注入初始化OSS服务
     * 
     * @param ossService 对象存储服务，处理文件上传令牌生成等操作
     */
    public OSSController(OSSService ossService) {
        this.ossService = ossService;
    }

    /**
     * 获取文件上传令牌接口
     * <p>
     * 根据指定的文件类型，生成用于OSS文件上传的临时授权令牌
     * 验证文件类型是否有效，然后返回包含访问凭证的令牌信息
     * </p>
     * 
     * @param fileType 文件类型，对应存储的目录分类
     * @return 返回包含上传令牌信息的响应体，状态码200；或在文件类型无效时返回错误信息，状态码400
     */
    @GetMapping("/{fileType}")
    public ResponseEntity<ResponseVO<OSSTokenVO>> getUploadToken(@PathVariable String fileType) {
        OSSFileTypeEnum fileTypeEnum = OSSFileTypeEnum.getByDirectory(fileType);
        if (fileTypeEnum == null) {
            return ResponseEntity.badRequest()
                    .body(ResponseVO.error(400, "Invalid file type: " + fileType));
        }
        OSSTokenVO token = ossService.generateUploadToken(fileTypeEnum);
        return ResponseEntity.ok(ResponseVO.success(token));
    }
}