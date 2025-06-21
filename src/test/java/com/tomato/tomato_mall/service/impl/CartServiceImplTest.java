package com.tomato.tomato_mall.service.impl;

import com.tomato.tomato_mall.dto.CartAddDTO;
import com.tomato.tomato_mall.entity.CartItem;
import com.tomato.tomato_mall.entity.CartItem.CartItemStatus;
import com.tomato.tomato_mall.entity.Product;
import com.tomato.tomato_mall.entity.Stockpile;
import com.tomato.tomato_mall.entity.User;
import com.tomato.tomato_mall.enums.ErrorTypeEnum;
import com.tomato.tomato_mall.exception.BusinessException;
import com.tomato.tomato_mall.repository.CartRepository;
import com.tomato.tomato_mall.repository.ProductRepository;
import com.tomato.tomato_mall.repository.StockpileRepository;
import com.tomato.tomato_mall.repository.UserRepository;
import com.tomato.tomato_mall.vo.CartItemVO;
import com.tomato.tomato_mall.vo.CartVO;
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
class CartServiceImplTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private StockpileRepository stockpileRepository;

    @InjectMocks
    private CartServiceImpl cartService;

    private User user;
    private Product product;
    private Stockpile stockpile;
    private CartAddDTO cartAddDTO;
    private CartItem cartItem;

    @BeforeEach
    void setUp() {
        // 准备测试数据
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setName("Test User");

        product = new Product();
        product.setId(1L);
        product.setTitle("Test Book");
        product.setPrice(new BigDecimal("29.99"));
        product.setRate(8.5);

        stockpile = new Stockpile();
        stockpile.setId(1L);
        stockpile.setProduct(product);
        stockpile.setAmount(100);

        cartAddDTO = new CartAddDTO();
        cartAddDTO.setProductId(1L);
        cartAddDTO.setQuantity(2);

        cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setUser(user);
        cartItem.setProduct(product);
        cartItem.setQuantity(2);
        cartItem.setStatus(CartItemStatus.ACTIVE);
    }

    // --- addToCart 方法测试 ---
    @Test
    void addToCart_Success_NewItem() {
        // --- Arrange ---
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(productRepository.findById(cartAddDTO.getProductId())).thenReturn(Optional.of(product));
        when(stockpileRepository.findByProductId(product.getId())).thenReturn(Optional.of(stockpile));
        when(cartRepository.findByUserAndProductAndStatus(user, product, CartItemStatus.ACTIVE))
                .thenReturn(Optional.empty());
        when(cartRepository.save(any(CartItem.class))).thenAnswer(invocation -> {
            CartItem savedItem = invocation.getArgument(0);
            savedItem.setId(1L);
            return savedItem;
        });

        // --- Act ---
        CartItemVO result = cartService.addToCart(user.getUsername(), cartAddDTO);

        // --- Assert ---
        assertNotNull(result);
        assertEquals(cartAddDTO.getQuantity(), result.getQuantity());
        assertEquals(product.getTitle(), result.getTitle());

        verify(userRepository, times(1)).findByUsername(user.getUsername());
        verify(productRepository, times(1)).findById(cartAddDTO.getProductId());
        verify(stockpileRepository, times(1)).findByProductId(product.getId());
        verify(cartRepository, times(1)).save(any(CartItem.class));
    }

    @Test
    void addToCart_Success_UpdateExistingItem() {
        // --- Arrange ---
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(productRepository.findById(cartAddDTO.getProductId())).thenReturn(Optional.of(product));
        when(stockpileRepository.findByProductId(product.getId())).thenReturn(Optional.of(stockpile));
        when(cartRepository.findByUserAndProductAndStatus(user, product, CartItemStatus.ACTIVE))
                .thenReturn(Optional.of(cartItem));
        when(cartRepository.save(any(CartItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // --- Act ---
        CartItemVO result = cartService.addToCart(user.getUsername(), cartAddDTO);

        // --- Assert ---
        assertNotNull(result);
        assertEquals(4, result.getQuantity()); // 原来2 + 新增2 = 4

        verify(cartRepository, times(1)).save(cartItem);
    }

    @Test
    void addToCart_UserNotFound() {
        // --- Arrange ---
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());

        // --- Act & Assert ---
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            cartService.addToCart(user.getUsername(), cartAddDTO);
        });

        assertEquals(ErrorTypeEnum.USER_NOT_FOUND, exception.getErrorType());
        verify(userRepository, times(1)).findByUsername(user.getUsername());
        verify(productRepository, never()).findById(any());
    }

    @Test
    void addToCart_ProductNotFound() {
        // --- Arrange ---
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(productRepository.findById(cartAddDTO.getProductId())).thenReturn(Optional.empty());

        // --- Act & Assert ---
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            cartService.addToCart(user.getUsername(), cartAddDTO);
        });

        assertEquals(ErrorTypeEnum.PRODUCT_NOT_FOUND, exception.getErrorType());
        verify(productRepository, times(1)).findById(cartAddDTO.getProductId());
        verify(stockpileRepository, never()).findByProductId(any());
    }

    @Test
    void addToCart_StockpileNotFound() {
        // --- Arrange ---
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(productRepository.findById(cartAddDTO.getProductId())).thenReturn(Optional.of(product));
        when(stockpileRepository.findByProductId(product.getId())).thenReturn(Optional.empty());

        // --- Act & Assert ---
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            cartService.addToCart(user.getUsername(), cartAddDTO);
        });

        assertEquals(ErrorTypeEnum.STOCKPILE_NOT_FOUND, exception.getErrorType());
        verify(stockpileRepository, times(1)).findByProductId(product.getId());
        verify(cartRepository, never()).save(any());
    }

    @Test
    void addToCart_StockpileNotEnough() {
        // --- Arrange ---
        stockpile.setAmount(1); // 库存只有1，但要添加2个
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(productRepository.findById(cartAddDTO.getProductId())).thenReturn(Optional.of(product));
        when(stockpileRepository.findByProductId(product.getId())).thenReturn(Optional.of(stockpile));

        // --- Act & Assert ---
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            cartService.addToCart(user.getUsername(), cartAddDTO);
        });

        assertEquals(ErrorTypeEnum.STOCKPILE_NOT_ENOUGH, exception.getErrorType());
        verify(cartRepository, never()).save(any());
    }

    // --- updateCartItemQuantity 方法测试 ---
    @Test
    void updateCartItemQuantity_Success() {
        // --- Arrange ---
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(cartRepository.findById(1L)).thenReturn(Optional.of(cartItem));
        when(stockpileRepository.findByProductId(product.getId())).thenReturn(Optional.of(stockpile));
        when(cartRepository.save(any(CartItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // --- Act ---
        CartItemVO result = cartService.updateCartItemQuantity(user.getUsername(), 1L, 3);

        // --- Assert ---
        assertNotNull(result);
        assertEquals(3, result.getQuantity());

        verify(userRepository, times(1)).findByUsername(user.getUsername());
        verify(cartRepository, times(1)).findById(1L);
        verify(cartRepository, times(1)).save(any(CartItem.class));
    }

    // --- removeFromCart 方法测试 ---
    @Test
    void removeFromCart_Success() {
        // --- Arrange ---
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(cartRepository.findById(1L)).thenReturn(Optional.of(cartItem));

        // --- Act ---
        cartService.removeFromCart(user.getUsername(), 1L);

        // --- Assert ---
        verify(userRepository, times(1)).findByUsername(user.getUsername());
        verify(cartRepository, times(1)).findById(1L);
        verify(cartRepository, times(1)).delete(cartItem);
    }

    // --- getCartItems 方法测试 ---
    @Test
    void getCartItems_Success() {
        // --- Arrange ---
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        List<CartItem> cartItems = new ArrayList<>();
        cartItems.add(cartItem);
        when(cartRepository.findByUserAndStatus(user, CartItemStatus.ACTIVE)).thenReturn(cartItems);

        // --- Act ---
        CartVO result = cartService.getCartItems(user.getUsername());

        // --- Assert ---
        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        assertEquals(cartItem.getQuantity(), result.getItems().get(0).getQuantity());

        verify(userRepository, times(1)).findByUsername(user.getUsername());
        verify(cartRepository, times(1)).findByUserAndStatus(user, CartItemStatus.ACTIVE);
    }

    @Test
    void getCartItems_UserNotFound() {
        // --- Arrange ---
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());

        // --- Act & Assert ---
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            cartService.getCartItems(user.getUsername());
        });

        assertEquals(ErrorTypeEnum.USER_NOT_FOUND, exception.getErrorType());
        verify(userRepository, times(1)).findByUsername(user.getUsername());
        verify(cartRepository, never()).findByUserAndStatus(any(), any());
    }
}
