package com.tomato.tomato_mall.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.tomato.tomato_mall.config.AlipayProperties;
import com.tomato.tomato_mall.dto.CancelOrderDTO;
import com.tomato.tomato_mall.dto.CheckoutDTO;
import com.tomato.tomato_mall.dto.PaymentCallbackDTO;
import com.tomato.tomato_mall.entity.CartItem;
import com.tomato.tomato_mall.entity.CartItem.CartItemStatus;
import com.tomato.tomato_mall.entity.Order;
import com.tomato.tomato_mall.entity.Order.OrderStatus;
import com.tomato.tomato_mall.entity.OrderItem;
import com.tomato.tomato_mall.entity.OrderItem.OrderItemStatus;
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
import com.tomato.tomato_mall.vo.OrderDetailVO;
import com.tomato.tomato_mall.vo.OrderItemVO;
import com.tomato.tomato_mall.vo.OrderVO;
import com.tomato.tomato_mall.vo.PaymentVO;

import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * 订单服务实现类
 * <p>
 * 该类实现了{@link OrderService}接口，提供订单的创建、查询、取消和支付等核心功能。
 * 包含订单生命周期管理、支付处理、库存锁定/释放以及超时订单处理等业务逻辑的具体实现。
 * </p>
 * 
 * @author Team CBDDL
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
    private final CartServiceImpl cartService;

    /**
     * 构造函数，通过依赖注入初始化订单服务组件
     * 
     * @param orderRepository     订单数据访问对象，负责订单数据的持久化操作
     * @param orderItemRepository 订单项数据访问对象，负责订单项数据的持久化操作
     * @param paymentRepository   支付数据访问对象，负责支付数据的持久化操作
     * @param userRepository      用户数据访问对象，负责用户数据的持久化操作
     * @param cartRepository      购物车数据访问对象，负责购物车数据的持久化操作
     * @param productRepository   商品数据访问对象，负责商品数据的持久化操作
     * @param stockpileRepository 库存数据访问对象，负责库存数据的持久化操作
     * @param alipayClient        支付宝客户端，用于调用支付宝API
     * @param alipayProperties    支付宝配置属性，包含支付宝接口相关配置
     * @param cartService         购物车服务，用于处理购物车相关逻辑
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
            AlipayProperties alipayProperties,
            CartServiceImpl cartService) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.stockpileRepository = stockpileRepository;
        this.alipayClient = alipayClient;
        this.alipayProperties = alipayProperties;
        this.cartService = cartService;
    }

    /**
     * 创建订单
     * <p>
     * 根据用户提交的结算信息创建订单，包括验证购物车项、锁定商品库存、
     * 计算订单金额、创建订单项并关联购物车项等操作。
     * 使用事务确保数据一致性，所有操作要么全部成功，要么全部失败。
     * </p>
     * 
     * @param username    用户名，用于识别订单所属用户
     * @param checkoutDTO 结算数据传输对象，包含创建订单所需的信息
     * @return 创建成功的订单视图对象
     * @throws NoSuchElementException   当用户不存在时抛出此异常
     * @throws IllegalArgumentException 当购物车项不属于当前用户、状态不为激活或库存不足时抛出此异常
     */
    @Override
    @Transactional
    public OrderDetailVO createOrder(String username, CheckoutDTO checkoutDTO) {
        // 获取用户信息
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("用户不存在"));

        // 获取购物车项
        List<Long> cartItemIds = checkoutDTO.getCartItemIds();
        List<CartItem> cartItems = cartRepository.findAllById(cartItemIds);

        // 验证购物车项
        for (CartItem cartItem : cartItems) {
            if (!cartItem.getUser().getId().equals(user.getId())) {
                throw new IllegalArgumentException("购物车商品不属于当前用户");
            }
            if (cartItem.getStatus() != CartItemStatus.ACTIVE) {
                throw new IllegalArgumentException("购物车商品状态错误，无法结算");
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
            orderItem.updateSubtotal();
            orderItem.setStatus(OrderItemStatus.PENDING);
            orderItem.setCartItemId(cartItem.getId()); // 记录关联的购物车项ID
            orderItems.add(orderItem);

            totalAmount = totalAmount.add(orderItem.getSubtotal());
        }

        order.setTotalAmount(totalAmount);
        order.setItems(orderItems);

        // 保存订单和订单项
        Order savedOrder = orderRepository.save(order);
        for (OrderItem item : orderItems) {
            item.setOrder(savedOrder);
        }
        orderItemRepository.saveAll(orderItems);

        // 将购物车项标记为已结算
        cartService.markCartItemsAsCheckedOut(cartItemIds, savedOrder.getId());

        // 返回订单视图对象
        return convertToOrderDetailVO(savedOrder);
    }

    /**
     * 取消订单
     * <p>
     * 取消指定的订单，包括验证订单所属用户、检查订单是否可取消、
     * 更新订单及订单项状态、释放锁定的库存以及恢复购物车项等操作。
     * 使用事务确保数据一致性。
     * </p>
     * 
     * @param username       用户名，用于识别订单所属用户
     * @param cancelOrderDTO 订单取消数据传输对象，包含订单ID和取消原因
     * @return 取消后的订单视图对象
     * @throws NoSuchElementException   当用户或订单不存在时抛出此异常
     * @throws IllegalArgumentException 当订单不属于当前用户时抛出此异常
     * @throws IllegalStateException    当订单状态不允许取消时抛出此异常
     */
    @Override
    @Transactional
    public OrderDetailVO cancelOrder(String username, CancelOrderDTO cancelOrderDTO) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("用户不存在"));

        Long orderIdLong = cancelOrderDTO.getOrderId();
        Order order = orderRepository.findById(orderIdLong)
                .orElseThrow(() -> new NoSuchElementException("订单不存在"));

        // 验证订单属于当前用户
        if (!order.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("无权操作该订单");
        }

        // 验证订单是否可以取消
        if (!order.canCancel()) {
            throw new IllegalStateException("订单状态不允许取消");
        }

        // 更新订单状态
        order.setStatus(OrderStatus.CANCELLED);
        order.setCancelReason(cancelOrderDTO.getReason());
        order.setCancelTime(LocalDateTime.now());
        orderRepository.save(order);

        // 更新订单项状态
        List<OrderItem> orderItems = orderItemRepository.findByOrder(order);
        for (OrderItem item : orderItems) {
            item.setStatus(OrderItemStatus.CANCELLED);
        }
        orderItemRepository.saveAll(orderItems);

        // 恢复锁定的库存
        releaseStockForOrder(order);

        // 恢复购物车项到活跃状态
        cartService.restoreCartItemsByOrderId(order.getId());

        return convertToOrderDetailVO(order);
    }

    /**
     * 发起支付
     * <p>
     * 为指定订单创建支付表单，包括验证订单状态、构建支付宝支付参数、
     * 创建支付记录等操作。此方法通过支付宝SDK生成支付表单HTML。
     * 使用事务确保数据一致性。
     * </p>
     * 
     * @param orderId 订单ID
     * @return 支付视图对象，包含支付表单HTML和相关支付信息
     * @throws NoSuchElementException 当订单不存在时抛出此异常
     * @throws IllegalStateException  当订单状态不允许支付时抛出此异常
     * @throws RuntimeException       当创建支付表单失败时抛出此异常
     */
    @Override
    @Transactional
    public PaymentVO initiatePayment(String orderId) {
        // 查询订单
        Long orderIdLong = Long.parseLong(orderId);
        Order order = orderRepository.findById(orderIdLong)
                .orElseThrow(() -> new NoSuchElementException("订单不存在"));

        // 验证订单状态
        if (!order.canPay()) {
            throw new IllegalStateException("订单状态不允许支付");
        }

        // 检测订单是否超时
        LocalDateTime timeoutThreshold = LocalDateTime.now().minusMinutes(30);
        if (order.getStatus() == OrderStatus.PENDING && order.getCreateTime().isBefore(timeoutThreshold)) {
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
     * 处理来自支付宝的异步通知，验证签名，更新订单状态并处理库存。
     * 根据支付结果（成功/失败）执行不同的业务逻辑，确保数据一致性。
     * </p>
     *
     * @param callbackDTO 支付回调数据传输对象，包含支付平台回调的所有参数
     * @return 是否处理成功，true表示成功处理
     */
    @Override
    @Transactional
    public boolean handlePaymentCallback(PaymentCallbackDTO callbackDTO) {
        Map<String, String> params = callbackDTO.getParameters();

        try {
            // 验证支付宝签名
            boolean signVerified = AlipaySignature.rsaCheckV1(
                    params,
                    alipayProperties.getAlipayPublicKey(),
                    "UTF-8",
                    "RSA2");

            if (!signVerified) {
                return false;
            }

            // 获取交易状态和订单ID
            String tradeStatus = params.get("trade_status");
            String orderIdStr = params.get("out_trade_no");

            if (orderIdStr == null) {
                return false;
            }

            Long orderId = Long.parseLong(orderIdStr);
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new NoSuchElementException("订单不存在"));

            if ("TRADE_SUCCESS".equals(tradeStatus) || "TRADE_FINISHED".equals(tradeStatus)) {
                return processSuccessfulPayment(order, params);
            } else if ("TRADE_CLOSED".equals(tradeStatus)) {
                return processFailedPayment(order, params);
            }

            return true;

        } catch (AlipayApiException e) {
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 处理支付成功的回调
     * <p>
     * 更新订单状态为已支付，更新订单项状态，扣减冻结的库存，
     * 同时更新支付记录的状态和交易号。
     * </p>
     * 
     * @param order  要处理的订单
     * @param params 支付回调参数
     * @return 处理是否成功
     */
    private boolean processSuccessfulPayment(Order order, Map<String, String> params) {
        // 检查订单是否已支付，避免重复处理
        if (order.getStatus() == OrderStatus.PAID) {
            return true;
        }

        // 只有待支付状态的订单可以被处理
        if (order.getStatus() != OrderStatus.PENDING) {
            return false;
        }

        // 更新订单状态
        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);

        // 更新购物车项状态
        cartService.markCartItemsByOrderIdAsCompleted(order.getId());

        // 更新订单项状态
        List<OrderItem> orderItems = orderItemRepository.findByOrder(order);
        for (OrderItem item : orderItems) {
            item.setStatus(OrderItemStatus.PAID);
        }
        orderItemRepository.saveAll(orderItems);

        // 从冻结库存中扣减实际库存
        deductStockForOrder(order);

        // 更新支付记录
        String tradeNo = params.get("trade_no"); // 支付宝交易号
        List<Payment> payments = paymentRepository.findByOrderAndStatus(
                order, Payment.PaymentStatus.PENDING);

        for (Payment payment : payments) {
            payment.setStatus(Payment.PaymentStatus.SUCCESS);
            payment.setTradeNo(tradeNo);
            payment.setCompleteTime(LocalDateTime.now());
            paymentRepository.save(payment);
        }

        return true;
    }

    /**
     * 处理支付失败的回调
     * <p>
     * 更新支付记录状态为失败，但不改变订单状态，
     * 保留订单待支付状态以允许重试或等待超时处理。
     * </p>
     * 
     * @param order  要处理的订单
     * @param params 支付回调参数
     * @return 处理是否成功
     */
    private boolean processFailedPayment(Order order, Map<String, String> params) {
        // 只处理待支付的订单
        if (order.getStatus() != OrderStatus.PENDING) {
            return true;
        }

        // 更新支付记录
        List<Payment> payments = paymentRepository.findByOrderAndStatus(
                order, Payment.PaymentStatus.PENDING);

        for (Payment payment : payments) {
            payment.setStatus(Payment.PaymentStatus.FAILED);
            payment.setCompleteTime(LocalDateTime.now());
            paymentRepository.save(payment);
        }

        return true;
    }

    /**
     * 从冻结库存中扣减实际库存
     * <p>
     * 支付成功后，将订单中商品的锁定库存转为实际消耗，
     * 同时减少商品的总库存和冻结库存。
     * </p>
     * 
     * @param order 要处理的订单
     * @throws NoSuchElementException 当商品库存不存在时抛出此异常
     */
    private void deductStockForOrder(Order order) {
        List<OrderItem> orderItems = orderItemRepository.findByOrder(order);
        for (OrderItem item : orderItems) {
            Stockpile stockpile = stockpileRepository.findByProductId(item.getProduct().getId())
                    .orElseThrow(() -> new NoSuchElementException("商品库存不存在"));

            // 减少库存和冻结数量
            stockpile.setAmount(stockpile.getAmount() - item.getQuantity());
            stockpile.setFrozen(stockpile.getFrozen() - item.getQuantity());
            stockpileRepository.save(stockpile);
        }
    }

    /**
     * 释放锁定的库存
     * <p>
     * 订单取消或支付失败时，释放订单中商品的锁定库存，
     * 确保库存数据的准确性。采用安全的方式处理，防止冻结数量为负。
     * </p>
     * 
     * @param order 要处理的订单
     */
    private void releaseStockForOrder(Order order) {
        List<OrderItem> orderItems = orderItemRepository.findByOrder(order);
        for (OrderItem item : orderItems) {
            Stockpile stockpile = stockpileRepository.findByProductId(item.getProduct().getId())
                    .orElse(null);

            if (stockpile != null) {
                stockpile.setFrozen(Math.max(0, stockpile.getFrozen() - item.getQuantity()));
                stockpileRepository.save(stockpile);
            }
        }
    }

    /**
     * 根据ID获取订单详情
     * <p>
     * 查询指定ID的订单详细信息，包括订单基本信息和关联的所有订单项，
     * 并转换为前端展示所需的详细视图对象。
     * </p>
     * 
     * @param orderId 订单ID
     * @return 包含订单项的详细订单视图对象
     * @throws NoSuchElementException 当指定ID的订单不存在时抛出此异常
     */
    @Override
    public OrderDetailVO getOrderById(String orderId) {
        Long orderIdLong = Long.parseLong(orderId);
        Order order = orderRepository.findById(orderIdLong)
                .orElseThrow(() -> new NoSuchElementException("订单不存在"));

        // 确保订单项被加载
        List<OrderItem> orderItems = orderItemRepository.findByOrder(order);
        order.setItems(orderItems);

        return convertToOrderDetailVO(order);
    }

    /**
     * 获取用户所有订单
     * <p>
     * 根据用户名获取该用户的所有订单信息，
     * 将订单实体列表转换为订单视图对象列表返回
     * </p>
     *
     * @param username 用户名
     * @return 用户的订单视图对象列表
     * @throws NoSuchElementException 当用户不存在时抛出此异常
     */
    @Override
    public List<OrderVO> getOrdersByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("用户不存在"));

        List<Order> orders = orderRepository.findByUser(user);
        return orders.stream()
                .map(this::convertToOrderVO)
                .collect(Collectors.toList());
    }

    /**
     * 获取所有订单
     * <p>
     * 获取系统中的所有订单信息，主要用于管理员查看和统计分析。
     * 将订单实体列表转换为订单视图对象列表返回。
     * </p>
     *
     * @return 系统中所有订单的视图对象列表
     */
    @Override
    public List<OrderVO> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream()
                .map(this::convertToOrderVO)
                .collect(Collectors.toList());
    }

    /**
     * 处理超时订单
     * <p>
     * 定时任务，每5分钟执行一次，用于处理超过30分钟未支付的订单。
     * 将超时订单状态更新为超时取消，释放锁定的库存，
     * 并更新相关的订单项、支付记录和购物车项状态。
     * 使用事务确保数据一致性。
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
            order.setCancelReason("订单支付超时自动取消");
            order.setCancelTime(LocalDateTime.now());
            orderRepository.save(order);

            // 更新订单项状态
            List<OrderItem> orderItems = orderItemRepository.findByOrder(order);
            for (OrderItem item : orderItems) {
                item.setStatus(OrderItemStatus.CANCELLED);
            }
            orderItemRepository.saveAll(orderItems);

            // 释放锁定的库存
            releaseStockForOrder(order);

            // 更新购物车项状态
            cartService.markCartItemsByOrderIdAsCancelled(order.getId());

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
     * 封装订单实体到前端展示层所需的数据结构，
     * 提取订单的ID、用户ID、总金额、支付方式、创建时间和状态等信息。
     * </p>
     * 
     * @param order 要转换的订单实体
     * @return 转换后的订单视图对象
     */
    private OrderVO convertToOrderVO(Order order) {
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(order, orderVO);
        orderVO.setUsername(order.getUser().getUsername());
        orderVO.setStatus(order.getStatus().toString());
        return orderVO;
    }

    /**
     * 将订单实体转换为详细视图对象
     * <p>
     * 封装订单实体到前端展示层所需的详细数据结构，
     * 提取订单的全部信息和所有订单项信息。
     * </p>
     * 
     * @param order 要转换的订单实体
     * @return 转换后的详细订单视图对象
     */
    private OrderDetailVO convertToOrderDetailVO(Order order) {
        OrderDetailVO orderDetailVO = new OrderDetailVO();
        BeanUtils.copyProperties(order, orderDetailVO);
        orderDetailVO.setUsername(order.getUser().getUsername());
        orderDetailVO.setStatus(order.getStatus().toString());
        List<OrderItemVO> orderItemVOs = order.getItems().stream().map(this::convertToOrderItemVO)
                .collect(Collectors.toList());
        orderDetailVO.setItems(orderItemVOs);
        return orderDetailVO;
    }

    /**
     * 将订单项实体转换为视图对象
     * <p>
     * 封装订单项实体到前端展示层所需的数据结构，
     * 提取订单项的ID、商品ID、商品名称、价格、数量、小计金额和状态等信息。
     * 该方法用于在订单详情查询中转换订单项数据。
     * </p>
     * 
     * @param orderItem 要转换的订单项实体
     * @return 转换后的订单项视图对象
     */
    private OrderItemVO convertToOrderItemVO(OrderItem orderItem) {
        OrderItemVO orderItemVO = new OrderItemVO();
        BeanUtils.copyProperties(orderItem, orderItemVO);
        orderItemVO.setProductId(orderItem.getProduct().getId());
        orderItemVO.setStatus(orderItem.getStatus().toString());
        return orderItemVO;
    }

}