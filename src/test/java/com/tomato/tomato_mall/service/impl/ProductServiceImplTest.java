package com.tomato.tomato_mall.service.impl;

import com.tomato.tomato_mall.dto.ProductCreateDTO;
import com.tomato.tomato_mall.dto.ProductUpdateDTO;
import com.tomato.tomato_mall.dto.SpecificationDTO;
import com.tomato.tomato_mall.entity.Product;
import com.tomato.tomato_mall.entity.Specification;
import com.tomato.tomato_mall.entity.Stockpile;
import com.tomato.tomato_mall.enums.ErrorTypeEnum;
import com.tomato.tomato_mall.exception.BusinessException;
import com.tomato.tomato_mall.repository.AdvertisementRepository;
import com.tomato.tomato_mall.repository.CartRepository;
import com.tomato.tomato_mall.repository.OrderItemRepository;
import com.tomato.tomato_mall.repository.ProductRepository;
import com.tomato.tomato_mall.repository.StockpileRepository;
import com.tomato.tomato_mall.util.VectorStoreUtil;
import com.tomato.tomato_mall.vo.ProductVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private StockpileRepository stockpileRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private AdvertisementRepository advertisementRepository;

    @Mock
    private VectorStoreUtil vectorStoreUtil;

    @InjectMocks
    private ProductServiceImpl productService;

    private ProductCreateDTO createDTO;
    private ProductUpdateDTO updateDTO;
    private Product product;
    private Stockpile stockpile;

    @BeforeEach
    void setUp() {
        // 准备测试数据
        createDTO = new ProductCreateDTO();
        createDTO.setTitle("Test Book");
        createDTO.setPrice(new BigDecimal("29.99"));
        createDTO.setRate(8.5);
        createDTO.setDescription("Test book description");
        createDTO.setDetail("Test book detail");

        // 规格数据
        List<SpecificationDTO> specDTOs = new ArrayList<>();
        SpecificationDTO specDTO = new SpecificationDTO();
        specDTO.setItem("Format");
        specDTO.setValue("Paperback");
        specDTOs.add(specDTO);
        createDTO.setSpecifications(specDTOs);

        updateDTO = new ProductUpdateDTO();
        updateDTO.setId(1L);
        updateDTO.setTitle("Updated Book");
        updateDTO.setPrice(new BigDecimal("39.99"));

        product = new Product();
        product.setId(1L);
        product.setTitle("Test Book");
        product.setPrice(new BigDecimal("29.99"));
        product.setRate(8.5);
        product.setDescription("Test book description");

        stockpile = new Stockpile();
        stockpile.setId(1L);
        stockpile.setProduct(product);
        stockpile.setAmount(100);
        product.setStockpile(stockpile);

        // 初始化规格
        List<Specification> specifications = new ArrayList<>();
        Specification spec = new Specification();
        spec.setId(1L);
        spec.setItem("Format");
        spec.setValue("Paperback");
        spec.setProduct(product);
        specifications.add(spec);
        product.setSpecifications(specifications);
    }

    // --- createProduct 方法测试 ---
    @Test
    void createProduct_Success() {
        // --- Arrange ---
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product savedProduct = invocation.getArgument(0);
            savedProduct.setId(1L);
            return savedProduct;
        });

        // --- Act ---
        ProductVO result = productService.createProduct(createDTO);

        // --- Assert ---
        assertNotNull(result);
        assertEquals(createDTO.getTitle(), result.getTitle());
        assertEquals(createDTO.getPrice(), result.getPrice());
        assertEquals(createDTO.getRate(), result.getRate());
        assertEquals(createDTO.getDescription(), result.getDescription());

        verify(productRepository, times(1)).save(any(Product.class));
        verify(vectorStoreUtil, times(1)).addProductVector(any(Product.class));
    }

    @Test
    void createProduct_WithLongDetail() {
        // --- Arrange ---
        String longDetail = "A".repeat(600); // 超过500字符
        createDTO.setDetail(longDetail);
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product savedProduct = invocation.getArgument(0);
            savedProduct.setId(1L);
            return savedProduct;
        });

        // --- Act ---
        ProductVO result = productService.createProduct(createDTO);

        // --- Assert ---
        assertNotNull(result);
        verify(productRepository, times(1)).save(argThat(savedProduct -> 
            savedProduct.getDetail().length() == 490));
    }

    // --- deleteProduct 方法测试 ---
    @Test
    void deleteProduct_Success() {
        // --- Arrange ---
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(orderItemRepository.existsByProductAndStatus(any(), any())).thenReturn(false);
        when(orderItemRepository.findByProduct(product)).thenReturn(new ArrayList<>());

        // --- Act ---
        productService.deleteProduct(1L);

        // --- Assert ---
        verify(productRepository, times(1)).findById(1L);
        verify(cartRepository, times(1)).deleteByProductId(1L);
        verify(advertisementRepository, times(1)).deleteAllByProduct(product);
        verify(productRepository, times(1)).delete(product);
        verify(vectorStoreUtil, times(1)).removeProductVector(1L);
    }

    @Test
    void deleteProduct_ProductNotFound() {
        // --- Arrange ---
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // --- Act & Assert ---
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            productService.deleteProduct(1L);
        });

        assertEquals(ErrorTypeEnum.PRODUCT_NOT_FOUND, exception.getErrorType());
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, never()).delete(any(Product.class));
    }

    // --- updateProduct 方法测试 ---
    @Test
    void updateProduct_Success() {
        // --- Arrange ---
        when(productRepository.findById(updateDTO.getId())).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // --- Act ---
        ProductVO result = productService.updateProduct(updateDTO);

        // --- Assert ---
        assertNotNull(result);
        assertEquals(updateDTO.getTitle(), result.getTitle());
        assertEquals(updateDTO.getPrice(), result.getPrice());

        verify(productRepository, times(1)).findById(updateDTO.getId());
        verify(productRepository, times(1)).save(any(Product.class));
        verify(vectorStoreUtil, times(1)).updateProductVector(any(Product.class));
    }

    @Test
    void updateProduct_ProductNotFound() {
        // --- Arrange ---
        when(productRepository.findById(updateDTO.getId())).thenReturn(Optional.empty());

        // --- Act & Assert ---
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            productService.updateProduct(updateDTO);
        });

        assertEquals(ErrorTypeEnum.PRODUCT_NOT_FOUND, exception.getErrorType());
        verify(productRepository, times(1)).findById(updateDTO.getId());
        verify(productRepository, never()).save(any(Product.class));
    }

    // --- getProductById 方法测试 ---
    @Test
    void getProductById_Success() {
        // --- Arrange ---
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // --- Act ---
        ProductVO result = productService.getProductById(1L);

        // --- Assert ---
        assertNotNull(result);
        assertEquals(product.getTitle(), result.getTitle());
        assertEquals(product.getPrice(), result.getPrice());
        assertEquals(product.getRate(), result.getRate());

        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void getProductById_ProductNotFound() {
        // --- Arrange ---
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // --- Act & Assert ---
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            productService.getProductById(1L);
        });

        assertEquals(ErrorTypeEnum.PRODUCT_NOT_FOUND, exception.getErrorType());
        verify(productRepository, times(1)).findById(1L);
    }
}
