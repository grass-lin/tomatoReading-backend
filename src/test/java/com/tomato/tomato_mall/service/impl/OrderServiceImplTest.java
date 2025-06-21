package com.tomato.tomato_mall.service.impl;

import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.tomato.tomato_mall.config.AlipayProperties;
import com.tomato.tomato_mall.dto.CheckoutDTO;
import com.tomato.tomato_mall.dto.CancelOrderDTO;
import com.tomato.tomato_mall.dto.ShippingAddressDTO;
import com.tomato.tomato_mall.entity.CartItem;
import com.tomato.tomato_mall.entity.CartItem.CartItemStatus;
import com.tomato.tomato_mall.entity.Order;
import com.tomato.tomato_mall.entity.Order.OrderStatus;
import com.tomato.tomato_mall.entity.Product;
import com.tomato.tomato_mall.entity.Stockpile;
import com.tomato.tomato_mall.entity.User;
import com.tomato.tomato_mall.enums.ErrorTypeEnum;
import com.tomato.tomato_mall.exception.BusinessException;
import com.tomato.tomato_mall.repository.CartRepository;
import com.tomato.tomato_mall.repository.OrderItemRepository;
import com.tomato.tomato_mall.repository.OrderRepository;
import com.tomato.tomato_mall.repository.PaymentRepository;
import com.tomato.tomato_mall.repository.ProductRepository;
import com.tomato.tomato_mall.repository.StockpileRepository;
import com.tomato.tomato_mall.repository.UserRepository;
import com.tomato.tomato_mall.vo.OrderDetailVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private StockpileRepository stockpileRepository;

    @Mock
    private AlipayClient alipayClient;

    @Mock
    private AlipayProperties alipayProperties;

    @InjectMocks
    private OrderServiceImpl orderService;

    private User user;
    private Order order;
    private CheckoutDTO checkoutDTO;
    private CancelOrderDTO cancelOrderDTO;
    private List<CartItem> cartItems;
    private Product product;
    private Stockpile stockpile;

    @BeforeEach
    void setUp() {
        // 准备测试数据
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setName("Test User");

        // 准备商品
        product = new Product();
        product.setId(1L);
        product.setTitle("Test Book");
        product.setPrice(new BigDecimal("29.99"));

        // 准备库存
        stockpile = new Stockpile();
        stockpile.setId(1L);
        stockpile.setProduct(product);
        stockpile.setAmount(100);
        stockpile.setFrozen(0);

        // 准备购物车项
        CartItem cartItem1 = new CartItem();
        cartItem1.setId(1L);
        cartItem1.setUser(user);
        cartItem1.setProduct(product);
        cartItem1.setQuantity(2);
        cartItem1.setStatus(CartItemStatus.ACTIVE);

        CartItem cartItem2 = new CartItem();
        cartItem2.setId(2L);
        cartItem2.setUser(user);
        cartItem2.setProduct(product);
        cartItem2.setQuantity(1);
        cartItem2.setStatus(CartItemStatus.ACTIVE);

        cartItems = Arrays.asList(cartItem1, cartItem2);

        order = new Order();
        order.setId("ORDER-123456");
        order.setUser(user);
        order.setTotalAmount(new BigDecimal("59.98"));
        order.setStatus(OrderStatus.PENDING);
        order.setPaymentMethod("ALIPAY");
        order.setReceiverName("Test User");
        order.setReceiverPhone("13800138000");
        order.setShippingAddress("Test Address");
        order.setCreateTime(LocalDateTime.now());
        order.setItems(new ArrayList<>()); // 初始化空的订单项列表

        // 准备结账DTO
        checkoutDTO = new CheckoutDTO();
        List<Long> cartItemIds = new ArrayList<>();
        cartItemIds.add(1L);
        cartItemIds.add(2L);
        checkoutDTO.setCartItemIds(cartItemIds);
        checkoutDTO.setPaymentMethod("ALIPAY");
        
        // 准备收货地址
        ShippingAddressDTO shippingAddressDTO = new ShippingAddressDTO();
        shippingAddressDTO.setName("Test User");
        shippingAddressDTO.setPhone("13800138000");
        shippingAddressDTO.setAddress("Test Address");
        shippingAddressDTO.setPostalCode("100000");
        checkoutDTO.setShippingAddress(shippingAddressDTO);

        cancelOrderDTO = new CancelOrderDTO();
        cancelOrderDTO.setOrderId("ORDER-123456");
        cancelOrderDTO.setReason("Changed mind");
    }

    // --- createOrder 方法测试 ---
    @Test
    void createOrder_Success() {
        // --- Arrange ---
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(cartRepository.findAllById(checkoutDTO.getCartItemIds())).thenReturn(cartItems);
        when(stockpileRepository.findByProductId(product.getId())).thenReturn(Optional.of(stockpile));
        when(stockpileRepository.save(any(Stockpile.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order savedOrder = invocation.getArgument(0);
            savedOrder.setId("ORDER-123456");
            savedOrder.setCreateTime(LocalDateTime.now());
            return savedOrder;
        });

        // --- Act ---
        OrderDetailVO result = orderService.createOrder(user.getUsername(), checkoutDTO);

        // --- Assert ---
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("PENDING", result.getStatus());

        verify(userRepository, times(1)).findByUsername(user.getUsername());
        verify(cartRepository, times(1)).findAllById(checkoutDTO.getCartItemIds());
        verify(stockpileRepository, times(cartItems.size())).findByProductId(product.getId());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void createOrder_UserNotFound() {
        // --- Arrange ---
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());

        // --- Act & Assert ---
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            orderService.createOrder(user.getUsername(), checkoutDTO);
        });

        assertEquals(ErrorTypeEnum.USER_NOT_FOUND, exception.getErrorType());
        verify(userRepository, times(1)).findByUsername(user.getUsername());
        verify(orderRepository, never()).save(any());
    }

    // --- cancelOrder 方法测试 ---
    @Test
    void cancelOrder_Success() {
        // --- Arrange ---
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(orderRepository.findById(cancelOrderDTO.getOrderId())).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // --- Act ---
        OrderDetailVO result = orderService.cancelOrder(user.getUsername(), cancelOrderDTO);

        // --- Assert ---
        assertNotNull(result);
        assertEquals("CANCELLED", result.getStatus());

        verify(userRepository, times(1)).findByUsername(user.getUsername());
        verify(orderRepository, times(1)).findById(cancelOrderDTO.getOrderId());
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void cancelOrder_OrderNotFound() {
        // --- Arrange ---
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(orderRepository.findById(cancelOrderDTO.getOrderId())).thenReturn(Optional.empty());

        // --- Act & Assert ---
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            orderService.cancelOrder(user.getUsername(), cancelOrderDTO);
        });

        assertEquals(ErrorTypeEnum.ORDER_NOT_FOUND, exception.getErrorType());
        verify(userRepository, times(1)).findByUsername(user.getUsername());
        verify(orderRepository, times(1)).findById(cancelOrderDTO.getOrderId());
        verify(orderRepository, never()).save(any());
    }

    // --- getOrderById 方法测试 ---
    @Test
    void getOrderById_Success() {
        // --- Arrange ---
        when(orderRepository.findById("ORDER-123456")).thenReturn(Optional.of(order));

        // --- Act ---
        OrderDetailVO result = orderService.getOrderById("ORDER-123456");

        // --- Assert ---
        assertNotNull(result);
        assertEquals(order.getId(), result.getId());
        assertEquals("PENDING", result.getStatus());
        assertEquals(order.getTotalAmount(), result.getTotalAmount());

        verify(orderRepository, times(1)).findById("ORDER-123456");
    }

    @Test
    void getOrderById_OrderNotFound() {
        // --- Arrange ---
        when(orderRepository.findById("ORDER-123456")).thenReturn(Optional.empty());

        // --- Act & Assert ---
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            orderService.getOrderById("ORDER-123456");
        });

        assertEquals(ErrorTypeEnum.ORDER_NOT_FOUND, exception.getErrorType());
        verify(orderRepository, times(1)).findById("ORDER-123456");
    }

    // --- initiatePayment 方法测试 ---
    @Test
    void initiatePayment_Success() throws Exception {
        // --- Arrange ---
        when(orderRepository.findById("ORDER-123456")).thenReturn(Optional.of(order));
        when(alipayProperties.getReturnUrl()).thenReturn("http://test-return.com");
        when(alipayProperties.getNotifyUrl()).thenReturn("http://test-notify.com");
        when(alipayProperties.getTimeoutExpress()).thenReturn("30m");
        
        // Mock AlipayTradePagePayResponse
        AlipayTradePagePayResponse mockResponse = new AlipayTradePagePayResponse();
        mockResponse.setBody("<form>test payment form</form>");
        when(alipayClient.pageExecute(any(AlipayTradePagePayRequest.class))).thenReturn(mockResponse);

        // --- Act & Assert ---
        assertDoesNotThrow(() -> {
            orderService.initiatePayment("ORDER-123456");
        });

        verify(orderRepository, times(1)).findById("ORDER-123456");
        verify(alipayClient, times(1)).pageExecute(any(AlipayTradePagePayRequest.class));
    }

    @Test
    void initiatePayment_OrderNotFound() {
        // --- Arrange ---
        when(orderRepository.findById("ORDER-123456")).thenReturn(Optional.empty());

        // --- Act & Assert ---
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            orderService.initiatePayment("ORDER-123456");
        });

        assertEquals(ErrorTypeEnum.ORDER_NOT_FOUND, exception.getErrorType());
        verify(orderRepository, times(1)).findById("ORDER-123456");
    }
}
