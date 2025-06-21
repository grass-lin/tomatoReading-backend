package com.tomato.tomato_mall.controller;

import com.tomato.tomato_mall.dto.AdvertisementCreateDTO;
import com.tomato.tomato_mall.dto.AdvertisementUpdateDTO;
import com.tomato.tomato_mall.service.AdvertisementService;
import com.tomato.tomato_mall.vo.AdvertisementVO;
import com.tomato.tomato_mall.vo.ResponseVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdvertisementControllerTest {

    @Mock
    private AdvertisementService advertisementService;

    @InjectMocks
    private AdvertisementController advertisementController;

    private AdvertisementCreateDTO createDTO;
    private AdvertisementUpdateDTO updateDTO;
    private AdvertisementVO advertisementVO;

    @BeforeEach
    void setUp() {
        createDTO = new AdvertisementCreateDTO();
        createDTO.setTitle("春季促销活动");
        createDTO.setContent("春季新书大促销，全场8折优惠！");
        createDTO.setImgUrl("https://example.com/spring-sale.jpg");
        createDTO.setProductId(1L);

        updateDTO = new AdvertisementUpdateDTO();
        updateDTO.setId(1L);
        updateDTO.setTitle("夏季促销活动");
        updateDTO.setContent("夏季新书大促销，全场7折优惠！");
        updateDTO.setImgUrl("https://example.com/summer-sale.jpg");
        updateDTO.setProductId(2L);

        advertisementVO = AdvertisementVO.builder()
                .id(1L)
                .title("春季促销活动")
                .content("春季新书大促销，全场8折优惠！")
                .imgUrl("https://example.com/spring-sale.jpg")
                .productId(1L)
                .build();
    }

    @Test
    void testGetAllAdvertisements_Success() {
        // --- Arrange ---
        AdvertisementVO ad1 = AdvertisementVO.builder()
                .id(1L)
                .title("春季促销活动")
                .content("春季新书大促销，全场8折优惠！")
                .imgUrl("https://example.com/spring-sale.jpg")
                .productId(1L)
                .build();
        AdvertisementVO ad2 = AdvertisementVO.builder()
                .id(2L)
                .title("夏季促销活动")
                .content("夏季新书大促销，全场7折优惠！")
                .imgUrl("https://example.com/summer-sale.jpg")
                .productId(2L)
                .build();
        List<AdvertisementVO> advertisements = Arrays.asList(ad1, ad2);
        when(advertisementService.getAllAdvertisements()).thenReturn(advertisements);

        // --- Act ---
        ResponseEntity<ResponseVO<List<AdvertisementVO>>> response = advertisementController.getAllAdvertisements();

        // --- Assert ---
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ResponseVO<List<AdvertisementVO>> body = response.getBody();
        assertNotNull(body);
        assertEquals(200, body.getCode());
        assertEquals(advertisements, body.getData());
        assertEquals(2, body.getData().size());

        verify(advertisementService, times(1)).getAllAdvertisements();
    }

    @Test
    void testGetAdvertisementById_Success() {
        // --- Arrange ---
        Long advertisementId = 1L;
        when(advertisementService.getAdvertisementById(advertisementId)).thenReturn(advertisementVO);

        // --- Act ---
        ResponseEntity<ResponseVO<AdvertisementVO>> response = advertisementController.getAdvertisementById(advertisementId);

        // --- Assert ---
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ResponseVO<AdvertisementVO> body = response.getBody();
        assertNotNull(body);
        assertEquals(200, body.getCode());
        assertEquals(advertisementVO, body.getData());

        verify(advertisementService, times(1)).getAdvertisementById(eq(advertisementId));
    }

    @Test
    void testCreateAdvertisement_Success() {
        // --- Arrange ---
        when(advertisementService.createAdvertisement(any(AdvertisementCreateDTO.class))).thenReturn(advertisementVO);

        // --- Act ---
        ResponseEntity<ResponseVO<AdvertisementVO>> response = advertisementController.createAdvertisement(createDTO);

        // --- Assert ---
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ResponseVO<AdvertisementVO> body = response.getBody();
        assertNotNull(body);
        assertEquals(200, body.getCode());
        assertEquals(advertisementVO, body.getData());

        verify(advertisementService, times(1)).createAdvertisement(eq(createDTO));
    }

    @Test
    void testUpdateAdvertisement_Success() {
        // --- Arrange ---
        when(advertisementService.updateAdvertisement(any(AdvertisementUpdateDTO.class))).thenReturn(advertisementVO);

        // --- Act ---
        ResponseEntity<ResponseVO<String>> response = advertisementController.updateAdvertisement(updateDTO);

        // --- Assert ---
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ResponseVO<String> body = response.getBody();
        assertNotNull(body);
        assertEquals(200, body.getCode());
        assertEquals("更新成功", body.getData());

        verify(advertisementService, times(1)).updateAdvertisement(eq(updateDTO));
    }

    @Test
    void testDeleteAdvertisement_Success() {
        // --- Arrange ---
        Long advertisementId = 1L;
        doNothing().when(advertisementService).deleteAdvertisement(advertisementId);

        // --- Act ---
        ResponseEntity<ResponseVO<String>> response = advertisementController.deleteAdvertisement(advertisementId);

        // --- Assert ---
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ResponseVO<String> body = response.getBody();
        assertNotNull(body);
        assertEquals(200, body.getCode());
        assertEquals("删除成功", body.getData());

        verify(advertisementService, times(1)).deleteAdvertisement(eq(advertisementId));
    }
}
