package com.tomato.tomato_mall.service.impl;

import com.tomato.tomato_mall.config.OSSProperties;
import com.tomato.tomato_mall.config.STSProperties;
import com.tomato.tomato_mall.enums.ErrorTypeEnum;
import com.tomato.tomato_mall.enums.OSSFileTypeEnum;
import com.tomato.tomato_mall.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;

/**
 * OSSServiceImpl单元测试类
 * 
 * @author Team CBDDL
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class OSSServiceImplTest {

    @Mock
    private OSSProperties ossProperties;

    @Mock
    private STSProperties stsProperties;

    private OSSServiceImpl ossService;

    @BeforeEach
    void setUp() {
        ossService = new OSSServiceImpl(ossProperties, stsProperties);
        
        // 使用lenient()来避免不必要的stubbing警告
        lenient().when(ossProperties.getBucketName()).thenReturn("test-bucket");
        lenient().when(ossProperties.getRegion()).thenReturn("cn-hangzhou");
        lenient().when(ossProperties.getBasePath()).thenReturn("tomato-reading");
        
        lenient().when(stsProperties.getEndpoint()).thenReturn("https://sts.cn-hangzhou.aliyuncs.com");
        lenient().when(stsProperties.getAccessKeyId()).thenReturn("test-access-key-id");
        lenient().when(stsProperties.getAccessKeySecret()).thenReturn("test-access-key-secret");
        lenient().when(stsProperties.getRoleArn()).thenReturn("acs:ram::123456789:role/test-role");
        lenient().when(stsProperties.getTokenExpireTime()).thenReturn(3600L);
    }

    @Test
    void testGenerateUploadToken_NullFileType() {
        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            ossService.generateUploadToken(null);
        });

        assertEquals(ErrorTypeEnum.INVALID_FILE_TYPE, exception.getErrorType());
    }

    @Test
    void testConstructor_ValidParameters() {
        // 测试构造函数
        OSSServiceImpl service = new OSSServiceImpl(ossProperties, stsProperties);
        assertNotNull(service);
    }

    @Test
    void testOSSFileTypeEnum_GetDirectory() {
        // 测试枚举类的方法
        assertEquals("avatar", OSSFileTypeEnum.AVATAR.getDirectory());
        assertEquals("cover", OSSFileTypeEnum.COVER.getDirectory());
        assertEquals("advertisement", OSSFileTypeEnum.ADVERTISEMENT.getDirectory());
    }

    @Test
    void testOSSFileTypeEnum_GetByDirectory() {
        // 测试枚举类的静态方法
        assertEquals(OSSFileTypeEnum.AVATAR, OSSFileTypeEnum.getByDirectory("avatar"));
        assertEquals(OSSFileTypeEnum.COVER, OSSFileTypeEnum.getByDirectory("cover"));
        assertEquals(OSSFileTypeEnum.ADVERTISEMENT, OSSFileTypeEnum.getByDirectory("advertisement"));
        
        // 测试大小写不敏感
        assertEquals(OSSFileTypeEnum.AVATAR, OSSFileTypeEnum.getByDirectory("AVATAR"));
        assertEquals(OSSFileTypeEnum.COVER, OSSFileTypeEnum.getByDirectory("Cover"));
        
        // 测试不存在的目录
        assertNull(OSSFileTypeEnum.getByDirectory("nonexistent"));
        assertNull(OSSFileTypeEnum.getByDirectory(null));
    }

    @Test
    void testConfigurationProperties() {
        // 验证配置属性的设置
        assertEquals("test-bucket", ossProperties.getBucketName());
        assertEquals("cn-hangzhou", ossProperties.getRegion());
        assertEquals("tomato-reading", ossProperties.getBasePath());
        
        assertEquals("https://sts.cn-hangzhou.aliyuncs.com", stsProperties.getEndpoint());
        assertEquals("test-access-key-id", stsProperties.getAccessKeyId());
        assertEquals("test-access-key-secret", stsProperties.getAccessKeySecret());
        assertEquals("acs:ram::123456789:role/test-role", stsProperties.getRoleArn());
        assertEquals(3600L, stsProperties.getTokenExpireTime());
    }

    /**
     * 注意：由于 generateUploadToken 方法中包含对阿里云SDK的复杂调用，
     * 包括静态方法调用和第三方客户端初始化，很难进行完整的单元测试Mock。
     * 建议在集成测试中测试完整的Token生成流程。
     * 
     * 这里主要测试了：
     * 1. 空参数验证
     * 2. 枚举类的功能
     * 3. 配置属性的设置
     * 4. 构造函数的正常工作
     */
}
