package com.tomato.tomato_mall.service.impl;

import com.tomato.tomato_mall.dto.AdvertisementCreateDTO;
import com.tomato.tomato_mall.dto.AdvertisementUpdateDTO;
import com.tomato.tomato_mall.entity.Advertisement;
import com.tomato.tomato_mall.entity.Product;
import com.tomato.tomato_mall.enums.ErrorTypeEnum;
import com.tomato.tomato_mall.exception.BusinessException;
import com.tomato.tomato_mall.repository.AdvertisementRepository;
import com.tomato.tomato_mall.repository.ProductRepository;
import com.tomato.tomato_mall.vo.AdvertisementVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdvertisementServiceImplTest {

    @Mock
    private AdvertisementRepository advertisementRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private AdvertisementServiceImpl advertisementService;

    private Advertisement advertisement;
    private Product product;
    private AdvertisementCreateDTO createDTO;
    private AdvertisementUpdateDTO updateDTO;

    @BeforeEach
    void setUp() {
        // 准备测试数据
        product = new Product();
        product.setId(1L);
        product.setTitle("Test Book");
        product.setPrice(new BigDecimal("29.99"));

        advertisement = new Advertisement();
        advertisement.setId(1L);
        advertisement.setTitle("Test Advertisement");
        advertisement.setContent("Test advertisement content");
        advertisement.setImageUrl("http://test.com/image.jpg");
        advertisement.setProduct(product);

        createDTO = new AdvertisementCreateDTO();
        createDTO.setTitle("New Advertisement");
        createDTO.setContent("New advertisement content");
        createDTO.setImgUrl("http://test.com/new-image.jpg");
        createDTO.setProductId(1L);

        updateDTO = new AdvertisementUpdateDTO();
        updateDTO.setId(1L);
        updateDTO.setTitle("Updated Advertisement");
        updateDTO.setContent("Updated advertisement content");
    }

    // --- getAllAdvertisements 方法测试 ---
    @Test
    void getAllAdvertisements_Success() {
        // --- Arrange ---
        List<Advertisement> advertisements = Arrays.asList(advertisement);
        when(advertisementRepository.findAll()).thenReturn(advertisements);

        // --- Act ---
        List<AdvertisementVO> result = advertisementService.getAllAdvertisements();

        // --- Assert ---
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(advertisement.getTitle(), result.get(0).getTitle());
        assertEquals(advertisement.getContent(), result.get(0).getContent());

        verify(advertisementRepository, times(1)).findAll();
    }

    @Test
    void getAllAdvertisements_EmptyList() {
        // --- Arrange ---
        when(advertisementRepository.findAll()).thenReturn(Arrays.asList());

        // --- Act ---
        List<AdvertisementVO> result = advertisementService.getAllAdvertisements();

        // --- Assert ---
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(advertisementRepository, times(1)).findAll();
    }

    // --- getAdvertisementById 方法测试 ---
    @Test
    void getAdvertisementById_Success() {
        // --- Arrange ---
        when(advertisementRepository.findById(1L)).thenReturn(Optional.of(advertisement));

        // --- Act ---
        AdvertisementVO result = advertisementService.getAdvertisementById(1L);

        // --- Assert ---
        assertNotNull(result);
        assertEquals(advertisement.getTitle(), result.getTitle());
        assertEquals(advertisement.getContent(), result.getContent());
        assertEquals(advertisement.getImageUrl(), result.getImgUrl());
        assertEquals(product.getId(), result.getProductId());

        verify(advertisementRepository, times(1)).findById(1L);
    }

    @Test
    void getAdvertisementById_AdvertisementNotFound() {
        // --- Arrange ---
        when(advertisementRepository.findById(1L)).thenReturn(Optional.empty());

        // --- Act & Assert ---
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            advertisementService.getAdvertisementById(1L);
        });

        assertEquals(ErrorTypeEnum.ADVERTISEMENT_NOT_FOUND, exception.getErrorType());
        verify(advertisementRepository, times(1)).findById(1L);
    }

    // --- createAdvertisement 方法测试 ---
    @Test
    void createAdvertisement_Success() {
        // --- Arrange ---
        when(productRepository.findById(createDTO.getProductId())).thenReturn(Optional.of(product));
        when(advertisementRepository.save(any(Advertisement.class))).thenAnswer(invocation -> {
            Advertisement savedAd = invocation.getArgument(0);
            savedAd.setId(1L);
            return savedAd;
        });

        // --- Act ---
        AdvertisementVO result = advertisementService.createAdvertisement(createDTO);

        // --- Assert ---
        assertNotNull(result);
        assertEquals(createDTO.getTitle(), result.getTitle());
        assertEquals(createDTO.getContent(), result.getContent());
        assertEquals(createDTO.getImgUrl(), result.getImgUrl());
        assertEquals(createDTO.getProductId(), result.getProductId());

        verify(productRepository, times(1)).findById(createDTO.getProductId());
        verify(advertisementRepository, times(1)).save(any(Advertisement.class));
    }

    @Test
    void createAdvertisement_ProductNotFound() {
        // --- Arrange ---
        when(productRepository.findById(createDTO.getProductId())).thenReturn(Optional.empty());

        // --- Act & Assert ---
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            advertisementService.createAdvertisement(createDTO);
        });

        assertEquals(ErrorTypeEnum.PRODUCT_NOT_FOUND, exception.getErrorType());
        verify(productRepository, times(1)).findById(createDTO.getProductId());
        verify(advertisementRepository, never()).save(any());
    }

    // --- updateAdvertisement 方法测试 ---
    @Test
    void updateAdvertisement_Success() {
        // --- Arrange ---
        when(advertisementRepository.findById(updateDTO.getId())).thenReturn(Optional.of(advertisement));
        when(advertisementRepository.save(any(Advertisement.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // --- Act ---
        AdvertisementVO result = advertisementService.updateAdvertisement(updateDTO);

        // --- Assert ---
        assertNotNull(result);
        assertEquals(updateDTO.getTitle(), result.getTitle());
        assertEquals(updateDTO.getContent(), result.getContent());

        verify(advertisementRepository, times(1)).findById(updateDTO.getId());
        verify(advertisementRepository, times(1)).save(advertisement);
    }

    @Test
    void updateAdvertisement_WithProductId() {
        // --- Arrange ---
        updateDTO.setProductId(2L);
        Product newProduct = new Product();
        newProduct.setId(2L);
        newProduct.setTitle("New Product");

        when(advertisementRepository.findById(updateDTO.getId())).thenReturn(Optional.of(advertisement));
        when(productRepository.findById(updateDTO.getProductId())).thenReturn(Optional.of(newProduct));
        when(advertisementRepository.save(any(Advertisement.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // --- Act ---
        AdvertisementVO result = advertisementService.updateAdvertisement(updateDTO);

        // --- Assert ---
        assertNotNull(result);
        assertEquals(updateDTO.getProductId(), result.getProductId());

        verify(advertisementRepository, times(1)).findById(updateDTO.getId());
        verify(productRepository, times(1)).findById(updateDTO.getProductId());
        verify(advertisementRepository, times(1)).save(advertisement);
    }

    @Test
    void updateAdvertisement_AdvertisementNotFound() {
        // --- Arrange ---
        when(advertisementRepository.findById(updateDTO.getId())).thenReturn(Optional.empty());

        // --- Act & Assert ---
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            advertisementService.updateAdvertisement(updateDTO);
        });

        assertEquals(ErrorTypeEnum.ADVERTISEMENT_NOT_FOUND, exception.getErrorType());
        verify(advertisementRepository, times(1)).findById(updateDTO.getId());
        verify(advertisementRepository, never()).save(any());
    }

    @Test
    void updateAdvertisement_ProductNotFound() {
        // --- Arrange ---
        updateDTO.setProductId(999L);
        when(advertisementRepository.findById(updateDTO.getId())).thenReturn(Optional.of(advertisement));
        when(productRepository.findById(updateDTO.getProductId())).thenReturn(Optional.empty());

        // --- Act & Assert ---
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            advertisementService.updateAdvertisement(updateDTO);
        });

        assertEquals(ErrorTypeEnum.PRODUCT_NOT_FOUND, exception.getErrorType());
        verify(advertisementRepository, times(1)).findById(updateDTO.getId());
        verify(productRepository, times(1)).findById(updateDTO.getProductId());
        verify(advertisementRepository, never()).save(any());
    }

    // --- deleteAdvertisement 方法测试 ---
    @Test
    void deleteAdvertisement_Success() {
        // --- Arrange ---
        when(advertisementRepository.existsById(1L)).thenReturn(true);

        // --- Act ---
        advertisementService.deleteAdvertisement(1L);

        // --- Assert ---
        verify(advertisementRepository, times(1)).existsById(1L);
        verify(advertisementRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteAdvertisement_AdvertisementNotFound() {
        // --- Arrange ---
        when(advertisementRepository.existsById(1L)).thenReturn(false);

        // --- Act & Assert ---
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            advertisementService.deleteAdvertisement(1L);
        });

        assertEquals(ErrorTypeEnum.ADVERTISEMENT_NOT_FOUND, exception.getErrorType());
        verify(advertisementRepository, times(1)).existsById(1L);
        verify(advertisementRepository, never()).deleteById(any());
    }
}
