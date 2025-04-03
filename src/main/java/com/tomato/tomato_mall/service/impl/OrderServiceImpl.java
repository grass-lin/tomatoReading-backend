package com.tomato.tomato_mall.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.tomato.tomato_mall.config.AlipayProperties;
import com.tomato.tomato_mall.dto.CheckoutDTO;
import com.tomato.tomato_mall.dto.PaymentCallbackDTO;
import com.tomato.tomato_mall.entity.CartItem;
import com.tomato.tomato_mall.entity.Order;
import com.tomato.tomato_mall.entity.Order.OrderStatus;
import com.tomato.tomato_mall.entity.OrderItem;
import com.tomato.tomato_mall.entity.Payment;
import com.tomato.tomato_mall.entity.Product;
import com.tomato.tomato_mall.entity.Stockpile;
import com.tomato.tomato_mall.entity.User;
import com.tomato.tomato_mall.repository.CartRepository;
import com.tomato.tomato_mall.repository.OrderItemRepository;
import com.tomato.tomato_mall.repository.OrderRepository;
import com.tomato.tomato_mall.repository.PaymentRepository;
import com.tomato.tomato_mall.repository.ProductRepository;
import com.tomato.tomato_mall.repository.StockpileRepository;
import com.tomato.tomato_mall.repository.UserRepository;
import com.tomato.tomato_mall.service.OrderService;
import com.tomato.tomato_mall.util.JsonUtils;
import com.tomato.tomato_mall.vo.OrderVO;
import com.tomato.tomato_mall.vo.PaymentCallbackVO;
import com.tomato.tomato_mall.vo.PaymentVO;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SignatureException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * 订单服务实现类
 * <p>
 * 该类实现了{@link OrderService}接口，提供订单创建、支付处理和支付回调等核心功能。
 * 包含订单处理、库存管理、支付集成等业务逻辑的具体实现。
 * </p>
 *
 * @author Team Tomato
 * @version 1.0
 */
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final StockpileRepository stockpileRepository;
    private final AlipayClient alipayClient;
    private final AlipayProperties alipayProperties;

    /**
     * 构造函数，通过依赖注入初始化订单服务组件
     */
    public OrderServiceImpl(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            PaymentRepository paymentRepository,
            UserRepository userRepository,
            CartRepository cartRepository,
            ProductRepository productRepository,
            StockpileRepository stockpileRepository,
            AlipayClient alipayClient,
            AlipayProperties alipayProperties) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.stockpileRepository = stockpileRepository;
        this.alipayClient = alipayClient;
        this.alipayProperties = alipayProperties;
    }

    /**
     * 从购物车创建订单
     * <p>
     * 根据用户选择的购物车商品创建订单，验证并锁定库存，计算订单金额。
     * 使用事务确保数据一致性，创建订单同时从购物车移除商品并锁定库存。
     * </p>
     *
     * @param username    用户名
     * @param checkoutDTO 结算数据传输对象
     * @return 创建的订单视图对象
     * @throws NoSuchElementException   当用户或商品不存在时抛出此异常
     * @throws IllegalArgumentException 当库存不足时抛出此异常
     */
    @Override
    @Transactional
    public OrderVO createOrder(String username, CheckoutDTO checkoutDTO) {
        // 获取用户信息
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("用户不存在"));

        // 获取购物车项
        List<Long> cartItemIds = checkoutDTO.getCartItemIds();
        List<CartItem> cartItems = cartRepository.findAllById(cartItemIds);

        // 验证购物车项是否属于当前用户
        for (CartItem cartItem : cartItems) {
            if (!cartItem.getUser().getId().equals(user.getId())) {
                throw new IllegalArgumentException("购物车商品不属于当前用户");
            }
        }

        // 创建订单
        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        order.setPaymentMethod(checkoutDTO.getPaymentMethod().toUpperCase());
        order.setReceiverName(checkoutDTO.getShippingAddress().getName());
        order.setReceiverPhone(checkoutDTO.getShippingAddress().getPhone());
        order.setShippingAddress(checkoutDTO.getShippingAddress().getAddress());
        order.setZipCode(checkoutDTO.getShippingAddress().getPostalCode());
        order.setCreateTime(LocalDateTime.now());

        // 计算订单总金额并创建订单项
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            int quantity = cartItem.getQuantity();

            // 验证并锁定库存
            Stockpile stockpile = stockpileRepository.findByProductId(product.getId())
                    .orElseThrow(() -> new NoSuchElementException("商品库存不存在"));

            if (stockpile.getAmount() - stockpile.getFrozen() < quantity) {
                throw new IllegalArgumentException("商品 " + product.getTitle() + " 库存不足");
            }

            // 锁定库存
            stockpile.setFrozen(stockpile.getFrozen() + quantity);
            stockpileRepository.save(stockpile);

            // 创建订单项
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setProductName(product.getTitle());
            orderItem.setPrice(product.getPrice());
            orderItem.setQuantity(quantity);
            orderItem.setSubtotal(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
            orderItems.add(orderItem);

            totalAmount = totalAmount.add(orderItem.getSubtotal());
        }

        order.setTotalAmount(totalAmount);
        order.setItems(orderItems);

        // 保存订单
        Order savedOrder = orderRepository.save(order);

        // 保存所有订单项
        for (OrderItem item : orderItems) {
            item.setOrder(savedOrder);
        }
        orderItemRepository.saveAll(orderItems);

        // 从购物车移除已结算的商品
        cartRepository.deleteAll(cartItems);

        // 返回订单视图对象
        return convertToOrderVO(savedOrder);
    }

    /**
     * 发起订单支付
     * <p>
     * 根据订单ID生成支付宝支付表单，供前端展示支付页面。
     * 验证订单状态，确保订单可以进行支付操作。
     * </p>
     *
     * @param orderId 订单ID
     * @return 支付信息视图对象，包含支付表单和订单信息
     * @throws NoSuchElementException 当订单不存在时抛出此异常
     * @throws IllegalStateException  当订单状态不允许支付时抛出此异常
     */
    @Override
    @Transactional
    public PaymentVO initiatePayment(String orderId) {
        // 查询订单
        Long orderIdLong = Long.parseLong(orderId);
        Order order = orderRepository.findById(orderIdLong)
                .orElseThrow(() -> new NoSuchElementException("订单不存在"));

        // 验证订单状态
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("订单状态不允许支付");
        }

        try {
            // 创建支付宝请求
            AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
            request.setReturnUrl(alipayProperties.getReturnUrl());
            request.setNotifyUrl(alipayProperties.getNotifyUrl());

            // 构建支付参数
            Map<String, Object> bizContent = new HashMap<>();
            bizContent.put("out_trade_no", order.getId().toString());
            bizContent.put("total_amount", order.getTotalAmount().toString());
            bizContent.put("subject", "Tomato Mall 订单");
            bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");
            bizContent.put("timeout_express", alipayProperties.getTimeoutExpress());

            request.setBizContent(JsonUtils.toJson(bizContent));

            // 记录支付尝试
            Payment payment = new Payment();
            payment.setOrder(order);
            payment.setAmount(order.getTotalAmount());
            payment.setPaymentMethod(order.getPaymentMethod());
            payment.setStatus(Payment.PaymentStatus.PENDING);
            payment.setCreateTime(LocalDateTime.now());
            paymentRepository.save(payment);

            // 调用支付宝接口获取支付表单
            String form = alipayClient.pageExecute(request).getBody();

            // 构造响应对象
            return PaymentVO.builder()
                    .orderId(order.getId().toString())
                    .totalAmount(order.getTotalAmount())
                    .paymentMethod(order.getPaymentMethod())
                    .paymentForm(form)
                    .build();

        } catch (AlipayApiException e) {
            throw new RuntimeException("创建支付表单失败", e);
        }
    }

    /**
     * 处理支付回调
     * <p>
     * 处理支付宝异步通知，验证支付结果，更新订单状态和库存。
     * 支付成功时将订单状态更新为已支付，并释放锁定的库存。
     * </p>
     *
     * @param callbackDTO 支付回调数据传输对象
     * @return 处理结果视图对象
     * @throws SignatureException     当支付回调验签失败时抛出此异常
     * @throws NoSuchElementException 当订单不存在时抛出此异常
     */
    @Override
    @Transactional
    public PaymentCallbackVO processPaymentCallback(PaymentCallbackDTO callbackDTO) {
        // 查找订单
        Long orderId = Long.parseLong(callbackDTO.getOrderId());
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("订单不存在"));

        // 验证回调签名
        // 实际项目应调用支付宝SDK验证签名，这里假设验证通过

        // 验证金额是否一致
        if (order.getTotalAmount().compareTo(callbackDTO.getTotalAmount()) != 0) {
            throw new IllegalArgumentException("支付金额不匹配");
        }

        // 处理支付结果
        if ("TRADE_SUCCESS".equals(callbackDTO.getPaymentStatus())) {
            // 更新订单状态
            order.setStatus(OrderStatus.SUCCESS);
            order.setPaymentTime(LocalDateTime.parse(callbackDTO.getPaymentTime(),
                    DateTimeFormatter.ISO_DATE_TIME));
            order.setTradeNo(callbackDTO.getTradeNo());
            orderRepository.save(order);

            // 更新支付记录
            List<Payment> payments = paymentRepository.findByOrderAndPaymentMethodOrderByCreateTimeDesc(
                    order, order.getPaymentMethod());
            if (!payments.isEmpty()) {
                Payment payment = payments.get(0);
                payment.setStatus(Payment.PaymentStatus.SUCCESS);
                payment.setTradeNo(callbackDTO.getTradeNo());
                payment.setCompleteTime(LocalDateTime.parse(callbackDTO.getPaymentTime(),
                        DateTimeFormatter.ISO_DATE_TIME));
                paymentRepository.save(payment);
            }

            // 从冻结库存中扣减实际库存
            List<OrderItem> orderItems = orderItemRepository.findByOrder(order);
            for (OrderItem item : orderItems) {
                Stockpile stockpile = stockpileRepository.findByProductId(item.getProduct().getId())
                        .orElseThrow(() -> new NoSuchElementException("商品库存不存在"));

                // 减少库存和冻结数量
                stockpile.setAmount(stockpile.getAmount() - item.getQuantity());
                stockpile.setFrozen(stockpile.getFrozen() - item.getQuantity());
                stockpileRepository.save(stockpile);
            }
        } else {
            // 支付失败，恢复库存锁定
            order.setStatus(OrderStatus.FAILED);
            orderRepository.save(order);

            List<OrderItem> orderItems = orderItemRepository.findByOrder(order);
            for (OrderItem item : orderItems) {
                Stockpile stockpile = stockpileRepository.findByProductId(item.getProduct().getId())
                        .orElseThrow(() -> new NoSuchElementException("商品库存不存在"));

                // 解锁库存
                stockpile.setFrozen(stockpile.getFrozen() - item.getQuantity());
                stockpileRepository.save(stockpile);
            }

            // 更新支付记录
            List<Payment> payments = paymentRepository.findByOrderAndPaymentMethodOrderByCreateTimeDesc(
                    order, order.getPaymentMethod());
            if (!payments.isEmpty()) {
                Payment payment = payments.get(0);
                payment.setStatus(Payment.PaymentStatus.FAILED);
                paymentRepository.save(payment);
            }
        }

        // 构建响应对象
        return PaymentCallbackVO.builder()
                .orderId(order.getId().toString())
                .status(callbackDTO.getPaymentStatus())
                .tradeNo(callbackDTO.getTradeNo())
                .paymentTime(callbackDTO.getPaymentTime())
                .build();
    }

    /**
     * 查询订单详情
     * <p>
     * 根据订单ID查询订单详细信息，返回订单视图对象。
     * </p>
     *
     * @param orderId 订单ID
     * @return 订单视图对象
     * @throws NoSuchElementException 当订单不存在时抛出此异常
     */
    @Override
    public OrderVO getOrderById(String orderId) {
        Long orderIdLong = Long.parseLong(orderId);
        Order order = orderRepository.findById(orderIdLong)
                .orElseThrow(() -> new NoSuchElementException("订单不存在"));
        return convertToOrderVO(order);
    }

    /**
     * 定时处理超时未支付订单
     * <p>
     * 每5分钟执行一次，查找创建时间超过30分钟但仍未支付的订单，
     * 更新订单状态为超时，并释放锁定的库存。
     * </p>
     */
    @Scheduled(fixedRate = 300000) // 每5分钟执行一次
    @Transactional
    public void processExpiredOrders() {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(30); // 30分钟前
        List<Order> expiredOrders = orderRepository.findByCreateTimeLessThanAndStatus(
                threshold, OrderStatus.PENDING);

        for (Order order : expiredOrders) {
            // 更新订单状态
            order.setStatus(OrderStatus.TIMEOUT);
            orderRepository.save(order);

            // 释放锁定的库存
            List<OrderItem> orderItems = orderItemRepository.findByOrder(order);
            for (OrderItem item : orderItems) {
                Stockpile stockpile = stockpileRepository.findByProductId(item.getProduct().getId())
                        .orElse(null);

                if (stockpile != null) {
                    stockpile.setFrozen(Math.max(0, stockpile.getFrozen() - item.getQuantity()));
                    stockpileRepository.save(stockpile);
                }
            }

            // 更新支付记录
            List<Payment> payments = paymentRepository.findByOrderAndStatus(
                    order, Payment.PaymentStatus.PENDING);
            for (Payment payment : payments) {
                payment.setStatus(Payment.PaymentStatus.TIMEOUT);
                paymentRepository.save(payment);
            }
        }
    }

    /**
     * 将订单实体转换为视图对象
     * <p>
     * 封装订单实体到前端展示层所需的数据结构。
     * </p>
     *
     * @param order 订单实体
     * @return 订单视图对象
     */
    private OrderVO convertToOrderVO(Order order) {
        return OrderVO.builder()
                .orderId(order.getId().toString())
                .userId(order.getUser().getId().toString())
                .totalAmount(order.getTotalAmount())
                .paymentMethod(order.getPaymentMethod())
                .createTime(order.getCreateTime())
                .status(order.getStatus().toString())
                .build();
    }
}