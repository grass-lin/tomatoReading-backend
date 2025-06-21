package com.tomato.tomato_mall.controller;

import com.tomato.tomato_mall.enums.OSSFileTypeEnum;
import com.tomato.tomato_mall.service.OSSService;
import com.tomato.tomato_mall.vo.OSSTokenVO;
import com.tomato.tomato_mall.vo.ResponseVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OSSControllerTest {

    @Mock
    private OSSService ossService;

    @InjectMocks
    private OSSController ossController;

    private OSSTokenVO ossTokenVO;

    @BeforeEach
    void setUp() {
        ossTokenVO = new OSSTokenVO();
        ossTokenVO.setRegion("oss-cn-hangzhou");
        ossTokenVO.setAccessKeyId("LTAI4GxxxxxxxxxxxxxxxxxxxxZ");
        ossTokenVO.setAccessKeySecret("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
        ossTokenVO.setSecurityToken("CAISxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
        ossTokenVO.setExpiration("2024-03-15T12:00:00Z");
        ossTokenVO.setBucket("tomato-mall-bucket");
        ossTokenVO.setFilePrefix("avatar/2024/03/15/");
    }

    @Test
    void testGetUploadToken_Avatar_Success() {
        // --- Arrange ---
        String fileType = "avatar";
        when(ossService.generateUploadToken(OSSFileTypeEnum.AVATAR)).thenReturn(ossTokenVO);

        // --- Act ---
        ResponseEntity<ResponseVO<OSSTokenVO>> response = ossController.getUploadToken(fileType);

        // --- Assert ---
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ResponseVO<OSSTokenVO> body = response.getBody();
        assertNotNull(body);
        assertEquals(200, body.getCode());
        assertEquals(ossTokenVO, body.getData());
        assertEquals("oss-cn-hangzhou", body.getData().getRegion());
        assertEquals("tomato-mall-bucket", body.getData().getBucket());
        assertEquals("avatar/2024/03/15/", body.getData().getFilePrefix());

        verify(ossService, times(1)).generateUploadToken(eq(OSSFileTypeEnum.AVATAR));
    }

    @Test
    void testGetUploadToken_Cover_Success() {
        // --- Arrange ---
        String fileType = "cover";
        OSSTokenVO coverTokenVO = new OSSTokenVO();
        coverTokenVO.setRegion("oss-cn-hangzhou");
        coverTokenVO.setAccessKeyId("LTAI4GxxxxxxxxxxxxxxxxxxxxZ");
        coverTokenVO.setAccessKeySecret("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
        coverTokenVO.setSecurityToken("CAISxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
        coverTokenVO.setExpiration("2024-03-15T12:00:00Z");
        coverTokenVO.setBucket("tomato-mall-bucket");
        coverTokenVO.setFilePrefix("cover/2024/03/15/");
        
        when(ossService.generateUploadToken(OSSFileTypeEnum.COVER)).thenReturn(coverTokenVO);

        // --- Act ---
        ResponseEntity<ResponseVO<OSSTokenVO>> response = ossController.getUploadToken(fileType);

        // --- Assert ---
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ResponseVO<OSSTokenVO> body = response.getBody();
        assertNotNull(body);
        assertEquals(200, body.getCode());
        assertEquals(coverTokenVO, body.getData());
        assertEquals("cover/2024/03/15/", body.getData().getFilePrefix());

        verify(ossService, times(1)).generateUploadToken(eq(OSSFileTypeEnum.COVER));
    }

    @Test
    void testGetUploadToken_Advertisement_Success() {
        // --- Arrange ---
        String fileType = "advertisement";
        OSSTokenVO adTokenVO = new OSSTokenVO();
        adTokenVO.setRegion("oss-cn-hangzhou");
        adTokenVO.setAccessKeyId("LTAI4GxxxxxxxxxxxxxxxxxxxxZ");
        adTokenVO.setAccessKeySecret("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
        adTokenVO.setSecurityToken("CAISxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
        adTokenVO.setExpiration("2024-03-15T12:00:00Z");
        adTokenVO.setBucket("tomato-mall-bucket");
        adTokenVO.setFilePrefix("advertisement/2024/03/15/");
        
        when(ossService.generateUploadToken(OSSFileTypeEnum.ADVERTISEMENT)).thenReturn(adTokenVO);

        // --- Act ---
        ResponseEntity<ResponseVO<OSSTokenVO>> response = ossController.getUploadToken(fileType);

        // --- Assert ---
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ResponseVO<OSSTokenVO> body = response.getBody();
        assertNotNull(body);
        assertEquals(200, body.getCode());
        assertEquals(adTokenVO, body.getData());
        assertEquals("advertisement/2024/03/15/", body.getData().getFilePrefix());

        verify(ossService, times(1)).generateUploadToken(eq(OSSFileTypeEnum.ADVERTISEMENT));
    }

    @Test
    void testGetUploadToken_InvalidFileType_ReturnsNull() {
        // --- Arrange ---
        String fileType = "invalid";
        when(ossService.generateUploadToken(null)).thenReturn(ossTokenVO);

        // --- Act ---
        ResponseEntity<ResponseVO<OSSTokenVO>> response = ossController.getUploadToken(fileType);

        // --- Assert ---
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ResponseVO<OSSTokenVO> body = response.getBody();
        assertNotNull(body);
        assertEquals(200, body.getCode());
        assertEquals(ossTokenVO, body.getData());

        verify(ossService, times(1)).generateUploadToken(eq(null));
    }
}
