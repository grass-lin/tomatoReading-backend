package com.tomato.tomato_mall.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.tomato.tomato_mall.dto.CancelOrderDTO;
import com.tomato.tomato_mall.dto.CheckoutDTO;
import com.tomato.tomato_mall.dto.PaymentCallbackDTO;
import com.tomato.tomato_mall.entity.CartItem;
import com.tomato.tomato_mall.entity.CartItem.CartItemStatus;
import com.tomato.tomato_mall.entity.Order;
import com.tomato.tomato_mall.entity.Order.OrderStatus;
import com.tomato.tomato_mall.entity.OrderItem;
import com.tomato.tomato_mall.entity.OrderItem.OrderItemStatus;
import com.tomato.tomato_mall.enums.ErrorTypeEnum;
import com.tomato.tomato_mall.exception.BusinessException;
import com.tomato.tomato_mall.properties.AlipayProperties;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    @Override
    @Transactional
    public OrderDetailVO createOrder(String username, CheckoutDTO checkoutDTO) {
        // 验证用户
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ErrorTypeEnum.USER_NOT_FOUND));

        // 验证购物车项
        List<Long> cartItemIds = checkoutDTO.getCartItemIds();
        List<CartItem> cartItems = cartRepository.findAllById(cartItemIds);
        if (cartItems.size() != cartItemIds.size()) {
            throw new BusinessException(ErrorTypeEnum.CARTITEM_NOT_FOUND);
        }
        // 迭代器策略
        cartItems.forEach(cartItem -> {
            if (cartItem.getStatus() != CartItemStatus.ACTIVE) {
                throw new BusinessException(ErrorTypeEnum.CARTITEM_STATUS_ERROR);
            }
            if (!cartItem.getUser().getId().equals(user.getId())) {
                throw new BusinessException(ErrorTypeEnum.CARTITEM_NOT_BELONG_TO_USER);
            }
        });

        // 创建订单
        Order order = new Order();
        order.setUser(user);
        order.setPaymentMethod(checkoutDTO.getPaymentMethod());
        order.setReceiverName(checkoutDTO.getShippingAddress().getName());
        order.setReceiverPhone(checkoutDTO.getShippingAddress().getPhone());
        order.setShippingAddress(checkoutDTO.getShippingAddress().getAddress());
        order.setZipCode(checkoutDTO.getShippingAddress().getPostalCode());

        // 计算订单总金额并创建订单项
        List<OrderItem> orderItems = cartItems.stream()
                .map(cartItem -> {
                    Product product = cartItem.getProduct();
                    int quantity = cartItem.getQuantity();
                    // 验证并锁定库存
                    Stockpile stockpile = stockpileRepository.findByProductId(product.getId())
                            .orElseThrow(() -> new BusinessException(ErrorTypeEnum.STOCKPILE_NOT_FOUND));
                    if (stockpile.getAmount() - stockpile.getFrozen() < quantity) {
                        throw new BusinessException(ErrorTypeEnum.STOCKPILE_NOT_ENOUGH);
                    }
                    stockpile.setFrozen(stockpile.getFrozen() + quantity);
                    stockpileRepository.save(stockpile);
                    // 修改购物车项状态
                    cartItem.setStatus(CartItemStatus.CHECKED_OUT);
                    // 创建订单项
                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrder(order);
                    orderItem.setProduct(product);
                    orderItem.setCartItem(cartItem);
                    // 保存商品的快照名称与价格
                    orderItem.setProductName(product.getTitle());
                    orderItem.setPrice(product.getPrice());
                    orderItem.setQuantity(quantity);
                    orderItem.updateSubtotal();
                    return orderItem;
                }).collect(Collectors.toList());
        order.setItems(orderItems);

        // 计算订单总金额
        BigDecimal totalAmount = orderItems.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalAmount(totalAmount);

        // 保存订单
        Order savedOrder = orderRepository.save(order);

        // 返回保存的订单视图对象
        return convertToOrderDetailVO(savedOrder);
    }

    @Override
    @Transactional
    public OrderDetailVO cancelOrder(String username, CancelOrderDTO cancelOrderDTO) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ErrorTypeEnum.USER_NOT_FOUND));

        Order order = orderRepository.findById(cancelOrderDTO.getOrderId())
                .orElseThrow(() -> new BusinessException(ErrorTypeEnum.ORDER_NOT_FOUND));

        // 验证订单属于当前用户
        if (!order.getUser().getId().equals(user.getId())) {
            throw new BusinessException(ErrorTypeEnum.ORDER_NOT_BELONG_TO_USER);
        }

        // 验证订单是否可以取消
        if (!order.canCancel()) {
            throw new BusinessException(ErrorTypeEnum.ORDER_STATUS_NOT_ALLOW_CANCEL);
        }

        // 更新订单状态
        order.setStatus(OrderStatus.CANCELLED);
        order.setCancelReason(cancelOrderDTO.getReason());
        order.setCancelTime(LocalDateTime.now());

        // 更新订单项状态
        List<OrderItem> orderItems = order.getItems();
        orderItems.forEach(item -> {
            item.setStatus(OrderItemStatus.CANCELLED);
            // 恢复冻结的库存
            Stockpile stockpile = stockpileRepository.findByProductId(item.getProduct().getId())
                    .orElseThrow(() -> new BusinessException(ErrorTypeEnum.STOCKPILE_NOT_FOUND));
            stockpile.setFrozen(stockpile.getFrozen() - item.getQuantity());
            // 恢复购物车项状态并清空关联
            CartItem cartItem = item.getCartItem();
            if (cartItem != null) {
                cartItem.setStatus(CartItemStatus.ACTIVE);
                item.setCartItem(null);   
            }
        });
        orderRepository.save(order);

        return convertToOrderDetailVO(order);
    }

    @Override
    @Transactional
    public PaymentVO initiatePayment(String orderId) {
        // 查询订单
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorTypeEnum.ORDER_NOT_FOUND));

        // 验证订单状态
        if (!order.canPay()) {
            throw new BusinessException(ErrorTypeEnum.ORDER_STATUS_NOT_ALLOW_PAY);
        }

        // 检测订单是否超时
        LocalDateTime timeoutThreshold = LocalDateTime.now().minusMinutes(30);
        if (order.getStatus() == OrderStatus.PENDING && order.getCreateTime().isBefore(timeoutThreshold)) {
            throw new BusinessException(ErrorTypeEnum.ORDER_STATUS_NOT_ALLOW_PAY);
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
            throw new BusinessException(ErrorTypeEnum.CREATE_PAY_FORM_FAILED);
        }
    }

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
            String orderId = params.get("out_trade_no");

            if (orderId == null) {
                return false;
            }

            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new BusinessException(ErrorTypeEnum.ORDER_NOT_FOUND));

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

    @Override
    public OrderDetailVO getOrderById(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorTypeEnum.ORDER_NOT_FOUND));

        return convertToOrderDetailVO(order);
    }

    @Override
    public List<OrderVO> getOrdersByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ErrorTypeEnum.USER_NOT_FOUND));

        List<Order> orders = orderRepository.findByUser(user);
        return orders.stream()
                .map(this::convertToOrderVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderVO> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream()
                .map(this::convertToOrderVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrderItemVO confirmReceive(String username, Long orderItemId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ErrorTypeEnum.USER_NOT_FOUND));

        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new BusinessException(ErrorTypeEnum.ORDER_ITEM_NOT_FOUND));

        // 验证订单项属于当前用户
        if (!orderItem.getOrder().getUser().getId().equals(user.getId())) {
            throw new BusinessException(ErrorTypeEnum.ORDER_ITEM_NOT_BELONG_TO_USER);
        }

        // 验证订单项状态是否为已发货
        if (orderItem.getStatus() != OrderItem.OrderItemStatus.SHIPPED) {
            throw new BusinessException(ErrorTypeEnum.ORDER_ITEM_STATUS_ERROR);
        }

        // 更新订单项状态为已完成
        orderItem.setStatus(OrderItem.OrderItemStatus.COMPLETED);
        orderItemRepository.save(orderItem);

        // 检查订单中所有订单项是否都已完成，如果是，更新订单状态为已完成
        Order order = orderItem.getOrder();
        boolean allCompleted = order.getItems().stream()
                .allMatch(item -> item.getStatus() == OrderItem.OrderItemStatus.COMPLETED);

        if (allCompleted) {
            order.setStatus(Order.OrderStatus.COMPLETED);
            orderRepository.save(order);
        }

        return convertToOrderItemVO(orderItem);
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

            // 更新订单项状态
            List<OrderItem> orderItems = order.getItems();
            orderItems.forEach(item -> {
                item.setStatus(OrderItemStatus.CANCELLED);
                // 恢复冻结的库存
                Stockpile stockpile = stockpileRepository.findByProductId(item.getProduct().getId())
                        .orElse(null);
                if (stockpile != null) {
                    stockpile.setFrozen(Math.max(0, stockpile.getFrozen() - item.getQuantity()));
                    stockpileRepository.save(stockpile);
                }
                // 删除关联的购物车项
                CartItem cartItem = item.getCartItem();
                if (cartItem != null) {
                    item.setCartItem(null);
                    cartRepository.delete(cartItem);
                }
            });
            orderRepository.save(order);

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

        // 更新订单项状态和库存
        List<OrderItem> orderItems = order.getItems();
        orderItems.forEach(item -> {
            item.setStatus(OrderItemStatus.PAID);

            Stockpile stockpile = stockpileRepository.findByProductId(item.getProduct().getId())
                    .orElseThrow(() -> new BusinessException(ErrorTypeEnum.STOCKPILE_NOT_FOUND));
            stockpile.setAmount(stockpile.getAmount() - item.getQuantity());
            stockpile.setFrozen(stockpile.getFrozen() - item.getQuantity());
            stockpileRepository.save(stockpile);

            // 删除关联的购物车项
            CartItem cartItem = item.getCartItem();
            if (cartItem != null) {
                item.setCartItem(null);
                cartRepository.delete(cartItem);
            }
        });
        orderRepository.save(order);

        // 更新支付记录
        String tradeNo = params.get("trade_no"); // 支付宝交易号
        List<Payment> payments = paymentRepository.findByOrderAndStatus(
                order, Payment.PaymentStatus.PENDING);
        payments.forEach(payment -> {
            payment.setStatus(Payment.PaymentStatus.SUCCESS);
            payment.setTradeNo(tradeNo);
            payment.setCompleteTime(LocalDateTime.now());
        });
        paymentRepository.saveAll(payments);

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
        payments.forEach(payment -> {
            payment.setStatus(Payment.PaymentStatus.FAILED);
            payment.setCompleteTime(LocalDateTime.now());
        });
        paymentRepository.saveAll(payments);

        return true;
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
        if (orderItem.getProduct() != null) {
            orderItemVO.setProductId(orderItem.getProduct().getId());
        }
        orderItemVO.setStatus(orderItem.getStatus().toString());
        return orderItemVO;
    }

}