package com.tomato.tomato_mall.controller;

import com.tomato.tomato_mall.dto.ProductCreateDTO;
import com.tomato.tomato_mall.dto.ProductUpdateDTO;
import com.tomato.tomato_mall.dto.StockpileUpdateDTO;
import com.tomato.tomato_mall.service.ProductService;
import com.tomato.tomato_mall.service.StockpileService;
import com.tomato.tomato_mall.vo.ProductVO;
import com.tomato.tomato_mall.vo.ResponseVO;
import com.tomato.tomato_mall.vo.StockpileVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // 启用 Mockito 扩展
class ProductControllerTest {

    @Mock // 模拟 ProductService
    private ProductService productService;

    @Mock // 模拟 StockpileService
    private StockpileService stockpileService;

    @InjectMocks // 创建 ProductController 实例，并注入上面的 Mock 对象
    private ProductController productController;

    private ProductCreateDTO createDTO;
    private ProductUpdateDTO updateDTO;
    private StockpileUpdateDTO stockpileUpdateDTO;
    private ProductVO productVO;
    private StockpileVO stockpileVO;

    @BeforeEach
    void setUp() {
        createDTO = new ProductCreateDTO();
        createDTO.setTitle("测试商品");
        createDTO.setPrice(new BigDecimal("99.99"));
        createDTO.setDescription("测试商品描述");

        updateDTO = new ProductUpdateDTO();
        updateDTO.setId(1L);
        updateDTO.setTitle("更新的商品");
        updateDTO.setPrice(new BigDecimal("199.99"));

        stockpileUpdateDTO = new StockpileUpdateDTO();
        stockpileUpdateDTO.setAmount(100);

        productVO = new ProductVO();
        productVO.setId(1L);
        productVO.setTitle("测试商品");
        productVO.setPrice(new BigDecimal("99.99"));
        productVO.setDescription("测试商品描述");

        stockpileVO = StockpileVO.builder()
                .id(1L)
                .productId(1L)
                .amount(100)
                .frozen(0)
                .build();
    }

    @Test
    void getAllProducts_Success() {
        // --- Arrange ---
        ProductVO product1 = new ProductVO();
        product1.setId(1L);
        product1.setTitle("商品1");
        ProductVO product2 = new ProductVO();
        product2.setId(2L);
        product2.setTitle("商品2");
        List<ProductVO> productList = Arrays.asList(product1, product2);
        when(productService.getAllProducts()).thenReturn(productList);

        // --- Act ---
        ResponseEntity<ResponseVO<List<ProductVO>>> response = productController.getAllProducts();

        // --- Assert ---
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ResponseVO<List<ProductVO>> body = response.getBody();
        assertNotNull(body);
        assertEquals(200, body.getCode());
        assertEquals(productList, body.getData());
        assertEquals(2, body.getData().size());

        // 验证 productService.getAllProducts 被调用
        verify(productService, times(1)).getAllProducts();
    }

    @Test
    void getProductsByPage_Success() {
        // --- Arrange ---
        ProductVO product1 = new ProductVO();
        product1.setId(1L);
        product1.setTitle("商品1");
        List<ProductVO> productList = Arrays.asList(product1);
        Page<ProductVO> productPage = new PageImpl<>(productList);
        when(productService.getProductsByPage(eq(0), eq(20), eq("test"), eq("id"))).thenReturn(productPage);

        // --- Act ---
        ResponseEntity<ResponseVO<Page<ProductVO>>> response = productController.getProductsByPage(0, 20, "test", "id");

        // --- Assert ---
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ResponseVO<Page<ProductVO>> body = response.getBody();
        assertNotNull(body);
        assertEquals(200, body.getCode());
        assertEquals(productPage, body.getData());
        assertEquals(1, body.getData().getContent().size());

        // 验证 productService.getProductsByPage 被调用
        verify(productService, times(1)).getProductsByPage(eq(0), eq(20), eq("test"), eq("id"));
    }

    @Test
    void getProductById_Success() {
        // --- Arrange ---
        Long productId = 1L;
        when(productService.getProductById(productId)).thenReturn(productVO);

        // --- Act ---
        ResponseEntity<ResponseVO<ProductVO>> response = productController.getProductById(productId);

        // --- Assert ---
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ResponseVO<ProductVO> body = response.getBody();
        assertNotNull(body);
        assertEquals(200, body.getCode());
        assertEquals(productVO, body.getData());

        // 验证 productService.getProductById 被调用
        verify(productService, times(1)).getProductById(eq(productId));
    }

    @Test
    void createProduct_Success() {
        // --- Arrange ---
        when(productService.createProduct(any(ProductCreateDTO.class))).thenReturn(productVO);

        // --- Act ---
        ResponseEntity<ResponseVO<ProductVO>> response = productController.createProduct(createDTO);

        // --- Assert ---
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ResponseVO<ProductVO> body = response.getBody();
        assertNotNull(body);
        assertEquals(200, body.getCode());
        assertEquals(productVO, body.getData());

        // 验证 productService.createProduct 被调用
        verify(productService, times(1)).createProduct(eq(createDTO));
    }

    @Test
    void updateProduct_Success() {
        // --- Arrange ---
        when(productService.updateProduct(any(ProductUpdateDTO.class))).thenReturn(productVO);

        // --- Act ---
        ResponseEntity<ResponseVO<String>> response = productController.updateProduct(updateDTO);

        // --- Assert ---
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ResponseVO<String> body = response.getBody();
        assertNotNull(body);
        assertEquals(200, body.getCode());
        assertEquals("更新成功", body.getData());

        // 验证 productService.updateProduct 被调用
        verify(productService, times(1)).updateProduct(eq(updateDTO));
    }

    @Test
    void deleteProduct_Success() {
        // --- Arrange ---
        Long productId = 1L;
        doNothing().when(productService).deleteProduct(productId);

        // --- Act ---
        ResponseEntity<ResponseVO<String>> response = productController.deleteProduct(productId);

        // --- Assert ---
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ResponseVO<String> body = response.getBody();
        assertNotNull(body);
        assertEquals(200, body.getCode());
        assertEquals("删除成功", body.getData());

        // 验证 productService.deleteProduct 被调用
        verify(productService, times(1)).deleteProduct(eq(productId));
    }

    @Test
    void getProductStockpile_Success() {
        // --- Arrange ---
        Long productId = 1L;
        when(stockpileService.getStockpileByProductId(productId)).thenReturn(stockpileVO);

        // --- Act ---
        ResponseEntity<ResponseVO<StockpileVO>> response = productController.getProductStockpile(productId);

        // --- Assert ---
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ResponseVO<StockpileVO> body = response.getBody();
        assertNotNull(body);
        assertEquals(200, body.getCode());
        assertEquals(stockpileVO, body.getData());

        // 验证 stockpileService.getStockpileByProductId 被调用
        verify(stockpileService, times(1)).getStockpileByProductId(eq(productId));
    }

    @Test
    void updateProductStock_Success() {
        // --- Arrange ---
        Long productId = 1L;
        when(stockpileService.updateStockpile(eq(productId), any(StockpileUpdateDTO.class))).thenReturn(stockpileVO);

        // --- Act ---
        ResponseEntity<ResponseVO<String>> response = productController.updateProductStock(productId, stockpileUpdateDTO);

        // --- Assert ---
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ResponseVO<String> body = response.getBody();
        assertNotNull(body);
        assertEquals(200, body.getCode());
        assertEquals("调整库存成功", body.getData());

        // 验证 stockpileService.updateStockpile 被调用
        verify(stockpileService, times(1)).updateStockpile(eq(productId), eq(stockpileUpdateDTO));
    }
}