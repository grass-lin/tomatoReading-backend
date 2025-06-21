package com.tomato.tomato_mall.controller;

import com.tomato.tomato_mall.dto.CartAddDTO;
import com.tomato.tomato_mall.dto.CartUpdateDTO;
import com.tomato.tomato_mall.dto.CheckoutDTO;
import com.tomato.tomato_mall.service.CartService;
import com.tomato.tomato_mall.service.OrderService;
import com.tomato.tomato_mall.vo.CartItemVO;
import com.tomato.tomato_mall.vo.CartVO;
import com.tomato.tomato_mall.vo.OrderDetailVO;
import com.tomato.tomato_mall.vo.ResponseVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartControllerTest {

    @Mock
    private CartService cartService;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private CartController cartController;

    private CartAddDTO cartAddDTO;
    private CartUpdateDTO cartUpdateDTO;
    private CheckoutDTO checkoutDTO;
    private CartItemVO cartItemVO;
    private CartVO cartVO;
    private OrderDetailVO orderDetailVO;

    @BeforeEach
    void setUp() {
        cartAddDTO = new CartAddDTO();
        cartAddDTO.setProductId(1L);
        cartAddDTO.setQuantity(2);

        cartUpdateDTO = new CartUpdateDTO();
        cartUpdateDTO.setQuantity(3);

        checkoutDTO = new CheckoutDTO();
        checkoutDTO.setCartItemIds(Arrays.asList(1L, 2L));
        checkoutDTO.setPaymentMethod("ALIPAY");

        cartItemVO = CartItemVO.builder()
                .cartItemId(1L)
                .productId(1L)
                .title("测试图书")
                .price(new BigDecimal("99.90"))
                .description("测试描述")
                .cover("test.jpg")
                .detail("测试详情")
                .quantity(2)
                .build();

        cartVO = CartVO.builder()
                .items(Arrays.asList(cartItemVO))
                .total(1)
                .totalAmount(new BigDecimal("99.90"))
                .build();

        orderDetailVO = OrderDetailVO.builder()
                .id("ORDER_001")
                .username("testuser")
                .totalAmount(new BigDecimal("99.90"))
                .paymentMethod("支付宝")
                .createTime(LocalDateTime.now())
                .status("PENDING_PAYMENT")
                .items(Arrays.asList())
                .build();
    }

    private void mockSecurityContext(String username) {
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(username);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testAddToCart_Success() throws Exception {
        // --- Arrange ---
        mockSecurityContext("testuser");
        when(cartService.addToCart(anyString(), any(CartAddDTO.class))).thenReturn(cartItemVO);

        // --- Act ---
        ResponseEntity<ResponseVO<CartItemVO>> response = cartController.addToCart(cartAddDTO);

        // --- Assert ---
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ResponseVO<CartItemVO> body = response.getBody();
        assertNotNull(body);
        assertEquals(200, body.getCode());
        assertEquals(cartItemVO, body.getData());

        verify(cartService, times(1)).addToCart(eq("testuser"), eq(cartAddDTO));
    }

    @Test
    void testRemoveFromCart_Success() throws Exception {
        // --- Arrange ---
        mockSecurityContext("testuser");
        Long cartItemId = 1L;
        doNothing().when(cartService).removeFromCart(eq("testuser"), eq(cartItemId));

        // --- Act ---
        ResponseEntity<ResponseVO<String>> response = cartController.removeFromCart(cartItemId);

        // --- Assert ---
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ResponseVO<String> body = response.getBody();
        assertNotNull(body);
        assertEquals(200, body.getCode());
        assertEquals("删除成功", body.getData());

        verify(cartService, times(1)).removeFromCart(eq("testuser"), eq(cartItemId));
    }

    @Test
    void testUpdateCartItemQuantity_Success() throws Exception {
        // --- Arrange ---
        mockSecurityContext("testuser");
        Long cartItemId = 1L;
        when(cartService.updateCartItemQuantity(eq("testuser"), eq(cartItemId), eq(3))).thenReturn(cartItemVO);

        // --- Act ---
        ResponseEntity<ResponseVO<String>> response = cartController.updateCartItemQuantity(cartItemId, cartUpdateDTO);

        // --- Assert ---
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ResponseVO<String> body = response.getBody();
        assertNotNull(body);
        assertEquals(200, body.getCode());
        assertEquals("修改数量成功", body.getData());

        verify(cartService, times(1)).updateCartItemQuantity(eq("testuser"), eq(cartItemId), eq(3));
    }

    @Test
    void testGetCartItems_Success() throws Exception {
        // --- Arrange ---
        mockSecurityContext("testuser");
        when(cartService.getCartItems(eq("testuser"))).thenReturn(cartVO);

        // --- Act ---
        ResponseEntity<ResponseVO<CartVO>> response = cartController.getCartItems();

        // --- Assert ---
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ResponseVO<CartVO> body = response.getBody();
        assertNotNull(body);
        assertEquals(200, body.getCode());
        assertEquals(cartVO, body.getData());

        verify(cartService, times(1)).getCartItems(eq("testuser"));
    }

    @Test
    void testCheckout_Success() throws Exception {
        // --- Arrange ---
        mockSecurityContext("testuser");
        when(orderService.createOrder(eq("testuser"), any(CheckoutDTO.class))).thenReturn(orderDetailVO);

        // --- Act ---
        ResponseEntity<ResponseVO<OrderDetailVO>> response = cartController.checkout(checkoutDTO);

        // --- Assert ---
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ResponseVO<OrderDetailVO> body = response.getBody();
        assertNotNull(body);
        assertEquals(200, body.getCode());
        assertEquals(orderDetailVO, body.getData());

        verify(orderService, times(1)).createOrder(eq("testuser"), eq(checkoutDTO));
    }
}
