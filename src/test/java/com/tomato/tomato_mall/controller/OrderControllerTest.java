package com.tomato.tomato_mall.controller;

import com.tomato.tomato_mall.dto.CancelOrderDTO;
import com.tomato.tomato_mall.service.OrderService;
import com.tomato.tomato_mall.vo.OrderDetailVO;
import com.tomato.tomato_mall.vo.OrderItemVO;
import com.tomato.tomato_mall.vo.OrderVO;
import com.tomato.tomato_mall.vo.PaymentVO;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    private OrderVO orderVO;
    private OrderDetailVO orderDetailVO;
    private OrderItemVO orderItemVO;
    private PaymentVO paymentVO;
    private CancelOrderDTO cancelOrderDTO;

    @BeforeEach
    void setUp() {
        orderVO = OrderVO.builder()
                .id("ORDER_001")
                .username("testuser")
                .totalAmount(new BigDecimal("99.90"))
                .paymentMethod("支付宝")
                .createTime(LocalDateTime.now())
                .status("PENDING_PAYMENT")
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

        orderItemVO = OrderItemVO.builder()
                .id(1L)
                .productId(1L)
                .productName("测试图书")
                .price(new BigDecimal("99.90"))
                .quantity(1)
                .subtotal(new BigDecimal("99.90"))
                .status("NORMAL")
                .build();

        paymentVO = PaymentVO.builder()
                .orderId("ORDER_001")
                .totalAmount(new BigDecimal("99.90"))
                .paymentForm("<form>Payment Form</form>")
                .build();

        cancelOrderDTO = new CancelOrderDTO();
        cancelOrderDTO.setOrderId("ORDER_001");
        cancelOrderDTO.setReason("不想要了");
    }

    private void mockSecurityContext(String username) {
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(username);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testInitiatePayment_Success() throws Exception {
        // --- Arrange ---
        String orderId = "ORDER_001";
        when(orderService.initiatePayment(orderId)).thenReturn(paymentVO);

        // --- Act ---
        ResponseEntity<ResponseVO<PaymentVO>> response = orderController.initiatePayment(orderId);

        // --- Assert ---
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ResponseVO<PaymentVO> body = response.getBody();
        assertNotNull(body);
        assertEquals(200, body.getCode());
        assertEquals(paymentVO, body.getData());

        verify(orderService, times(1)).initiatePayment(eq(orderId));
    }

    @Test
    void testGetOrders_Success() throws Exception {
        // --- Arrange ---
        mockSecurityContext("testuser");
        List<OrderVO> orders = Arrays.asList(orderVO);
        when(orderService.getOrdersByUsername(eq("testuser"))).thenReturn(orders);

        // --- Act ---
        ResponseEntity<ResponseVO<List<OrderVO>>> response = orderController.getOrders();

        // --- Assert ---
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ResponseVO<List<OrderVO>> body = response.getBody();
        assertNotNull(body);
        assertEquals(200, body.getCode());
        assertEquals(orders, body.getData());
        assertEquals(1, body.getData().size());

        verify(orderService, times(1)).getOrdersByUsername(eq("testuser"));
    }

    @Test
    void testGetOrderDetail_Success() throws Exception {
        // --- Arrange ---
        String orderId = "ORDER_001";
        when(orderService.getOrderById(orderId)).thenReturn(orderDetailVO);

        // --- Act ---
        ResponseEntity<ResponseVO<OrderDetailVO>> response = orderController.getOrderDetail(orderId);

        // --- Assert ---
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ResponseVO<OrderDetailVO> body = response.getBody();
        assertNotNull(body);
        assertEquals(200, body.getCode());
        assertEquals(orderDetailVO, body.getData());

        verify(orderService, times(1)).getOrderById(eq(orderId));
    }

    @Test
    void testCancelOrder_Success() throws Exception {
        // --- Arrange ---
        mockSecurityContext("testuser");
        when(orderService.cancelOrder(eq("testuser"), any(CancelOrderDTO.class))).thenReturn(orderDetailVO);

        // --- Act ---
        ResponseEntity<ResponseVO<OrderDetailVO>> response = orderController.cancelOrder(cancelOrderDTO);

        // --- Assert ---
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ResponseVO<OrderDetailVO> body = response.getBody();
        assertNotNull(body);
        assertEquals(200, body.getCode());
        assertEquals(orderDetailVO, body.getData());

        verify(orderService, times(1)).cancelOrder(eq("testuser"), eq(cancelOrderDTO));
    }

    @Test
    void testConfirmReceive_Success() throws Exception {
        // --- Arrange ---
        mockSecurityContext("testuser");
        Long orderItemId = 1L;
        when(orderService.confirmReceive(eq("testuser"), eq(orderItemId))).thenReturn(orderItemVO);

        // --- Act ---
        ResponseEntity<ResponseVO<OrderItemVO>> response = orderController.confirmReceive(orderItemId);

        // --- Assert ---
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ResponseVO<OrderItemVO> body = response.getBody();
        assertNotNull(body);
        assertEquals(200, body.getCode());
        assertEquals(orderItemVO, body.getData());

        verify(orderService, times(1)).confirmReceive(eq("testuser"), eq(orderItemId));
    }
}
