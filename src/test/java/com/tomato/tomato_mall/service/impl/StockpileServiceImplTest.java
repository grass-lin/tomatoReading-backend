package com.tomato.tomato_mall.service.impl;

import com.tomato.tomato_mall.dto.StockpileUpdateDTO;
import com.tomato.tomato_mall.entity.Product;
import com.tomato.tomato_mall.entity.Stockpile;
import com.tomato.tomato_mall.enums.ErrorTypeEnum;
import com.tomato.tomato_mall.exception.BusinessException;
import com.tomato.tomato_mall.repository.ProductRepository;
import com.tomato.tomato_mall.repository.StockpileRepository;
import com.tomato.tomato_mall.vo.StockpileVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockpileServiceImplTest {

    @Mock
    private StockpileRepository stockpileRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private StockpileServiceImpl stockpileService;

    private Product product;
    private Stockpile stockpile;
    private StockpileUpdateDTO updateDTO;

    @BeforeEach
    void setUp() {
        // 准备测试数据
        product = new Product();
        product.setId(1L);
        product.setTitle("Test Book");
        product.setPrice(new BigDecimal("29.99"));

        stockpile = new Stockpile();
        stockpile.setId(1L);
        stockpile.setProduct(product);
        stockpile.setAmount(100);
        stockpile.setFrozen(10);

        updateDTO = new StockpileUpdateDTO();
        updateDTO.setAmount(150);
    }

    // --- getStockpileByProductId 方法测试 ---
    @Test
    void getStockpileByProductId_Success() {
        // --- Arrange ---
        when(productRepository.existsById(1L)).thenReturn(true);
        when(stockpileRepository.findByProductId(1L)).thenReturn(Optional.of(stockpile));

        // --- Act ---
        StockpileVO result = stockpileService.getStockpileByProductId(1L);

        // --- Assert ---
        assertNotNull(result);
        assertEquals(stockpile.getId(), result.getId());
        assertEquals(stockpile.getAmount(), result.getAmount());
        assertEquals(stockpile.getFrozen(), result.getFrozen());
        assertEquals(product.getId(), result.getProductId());

        verify(productRepository, times(1)).existsById(1L);
        verify(stockpileRepository, times(1)).findByProductId(1L);
    }

    @Test
    void getStockpileByProductId_ProductNotFound() {
        // --- Arrange ---
        when(productRepository.existsById(1L)).thenReturn(false);

        // --- Act & Assert ---
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            stockpileService.getStockpileByProductId(1L);
        });

        assertEquals(ErrorTypeEnum.PRODUCT_NOT_FOUND, exception.getErrorType());
        verify(productRepository, times(1)).existsById(1L);
        verify(stockpileRepository, never()).findByProductId(any());
    }

    @Test
    void getStockpileByProductId_StockpileNotFound() {
        // --- Arrange ---
        when(productRepository.existsById(1L)).thenReturn(true);
        when(stockpileRepository.findByProductId(1L)).thenReturn(Optional.empty());

        // --- Act & Assert ---
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            stockpileService.getStockpileByProductId(1L);
        });

        assertEquals(ErrorTypeEnum.STOCKPILE_NOT_FOUND, exception.getErrorType());
        verify(productRepository, times(1)).existsById(1L);
        verify(stockpileRepository, times(1)).findByProductId(1L);
    }

    // --- updateStockpile 方法测试 ---
    @Test
    void updateStockpile_Success() {
        // --- Arrange ---
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(stockpileRepository.findByProductId(1L)).thenReturn(Optional.of(stockpile));
        when(stockpileRepository.save(any(Stockpile.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // --- Act ---
        StockpileVO result = stockpileService.updateStockpile(1L, updateDTO);

        // --- Assert ---
        assertNotNull(result);
        assertEquals(updateDTO.getAmount(), result.getAmount());
        assertEquals(stockpile.getFrozen(), result.getFrozen());
        assertEquals(product.getId(), result.getProductId());

        verify(productRepository, times(1)).findById(1L);
        verify(stockpileRepository, times(1)).findByProductId(1L);
        verify(stockpileRepository, times(1)).save(stockpile);
    }

    @Test
    void updateStockpile_ProductNotFound() {
        // --- Arrange ---
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // --- Act & Assert ---
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            stockpileService.updateStockpile(1L, updateDTO);
        });

        assertEquals(ErrorTypeEnum.PRODUCT_NOT_FOUND, exception.getErrorType());
        verify(productRepository, times(1)).findById(1L);
        verify(stockpileRepository, never()).findByProductId(any());
        verify(stockpileRepository, never()).save(any());
    }

    @Test
    void updateStockpile_StockpileNotFound() {
        // --- Arrange ---
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(stockpileRepository.findByProductId(1L)).thenReturn(Optional.empty());

        // --- Act & Assert ---
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            stockpileService.updateStockpile(1L, updateDTO);
        });

        assertEquals(ErrorTypeEnum.STOCKPILE_NOT_FOUND, exception.getErrorType());
        verify(productRepository, times(1)).findById(1L);
        verify(stockpileRepository, times(1)).findByProductId(1L);
        verify(stockpileRepository, never()).save(any());
    }

    @Test
    void updateStockpile_StockpileNotEnough() {
        // --- Arrange ---
        updateDTO.setAmount(5); // 小于冻结库存(10)
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(stockpileRepository.findByProductId(1L)).thenReturn(Optional.of(stockpile));

        // --- Act & Assert ---
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            stockpileService.updateStockpile(1L, updateDTO);
        });

        assertEquals(ErrorTypeEnum.STOCKPILE_NOT_ENOUGH, exception.getErrorType());
        verify(productRepository, times(1)).findById(1L);
        verify(stockpileRepository, times(1)).findByProductId(1L);
        verify(stockpileRepository, never()).save(any());
    }

    @Test
    void updateStockpile_AmountEqualToFrozen() {
        // --- Arrange ---
        updateDTO.setAmount(10); // 等于冻结库存
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(stockpileRepository.findByProductId(1L)).thenReturn(Optional.of(stockpile));
        when(stockpileRepository.save(any(Stockpile.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // --- Act ---
        StockpileVO result = stockpileService.updateStockpile(1L, updateDTO);

        // --- Assert ---
        assertNotNull(result);
        assertEquals(updateDTO.getAmount(), result.getAmount());
        assertEquals(stockpile.getFrozen(), result.getFrozen());

        verify(productRepository, times(1)).findById(1L);
        verify(stockpileRepository, times(1)).findByProductId(1L);
        verify(stockpileRepository, times(1)).save(stockpile);
    }
}
