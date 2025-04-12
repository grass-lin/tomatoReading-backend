package com.tomato.tomato_mall.controller;

import com.tomato.tomato_mall.dto.CancelOrderDTO;
import com.tomato.tomato_mall.dto.PaymentCallbackDTO;
import com.tomato.tomato_mall.service.OrderService;
import com.tomato.tomato_mall.vo.OrderVO;
import com.tomato.tomato_mall.vo.PaymentCallbackVO;
import com.tomato.tomato_mall.vo.PaymentVO;
import com.tomato.tomato_mall.vo.ResponseVO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * 订单控制器
 * <p>
 * 提供订单创建、支付处理和支付回调等功能的REST API接口
 * 所有接口返回统一的ResponseVO格式，包含状态码、消息和数据
 * </p>
 * 
 * @author Team Tomato
 * @version 1.0
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    /**
     * 构造函数，通过依赖注入初始化订单服务
     * 
     * @param orderService 订单服务，处理订单相关业务逻辑
     */
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * 发起支付接口
     * <p>
     * 根据订单ID生成支付表单，供前端调用第三方支付平台
     * 支付流程的第二步
     * </p>
     * 
     * @param orderId 订单ID
     * @return 返回包含支付表单和订单信息的响应体，状态码200
     * @throws java.util.NoSuchElementException 当订单不存在时抛出
     * @throws IllegalStateException 当订单状态不允许支付时抛出
     */
    @PostMapping("/{orderId}/pay")
    public ResponseEntity<ResponseVO<PaymentVO>> initiatePayment(@PathVariable String orderId) {
        PaymentVO paymentVO = orderService.initiatePayment(orderId);
        return ResponseEntity.ok(ResponseVO.success(paymentVO));
    }

    /**
     * 支付回调处理接口
     * <p>
     * 处理支付宝的异步通知，验证支付结果，更新订单状态和库存
     * 支付流程的第三步
     * </p>
     * 
     * @param callbackDTO 支付回调数据传输对象，包含支付结果和订单信息
     * @return 返回处理结果响应体，状态码200
     * @throws java.security.SignatureException 当支付回调验签失败时抛出
     * @throws java.util.NoSuchElementException 当订单不存在时抛出
     */
    @PostMapping("/notify")
    public ResponseEntity<ResponseVO<PaymentCallbackVO>> handlePaymentCallback(
            @Valid @RequestBody PaymentCallbackDTO callbackDTO) {
        PaymentCallbackVO callbackVO = orderService.processPaymentCallback(callbackDTO);
        return ResponseEntity.ok(ResponseVO.success(callbackVO));
    }

    /**
     * 获取订单详情接口
     * <p>
     * 根据订单ID查询订单的详细信息
     * </p>
     * 
     * @param orderId 订单ID
     * @return 返回包含订单详情的响应体，状态码200
     * @throws java.util.NoSuchElementException 当订单不存在时抛出
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<ResponseVO<OrderVO>> getOrderDetail(@PathVariable String orderId) {
        OrderVO orderVO = orderService.getOrderById(orderId);
        return ResponseEntity.ok(ResponseVO.success(orderVO));
    }

    /**
     * 取消订单接口
     * <p>
     * 允许用户取消未支付或未完成的订单，根据提供的取消信息处理订单取消逻辑
     * </p>
     * 
     * @param cancelOrderDTO 订单取消数据传输对象，包含订单ID和取消原因
     * @return 返回包含已取消订单详情的响应体，状态码200
     * @throws java.util.NoSuchElementException 当订单不存在时抛出
     * @throws IllegalStateException 当订单状态不允许取消时抛出
     */
    @DeleteMapping
    public ResponseEntity<ResponseVO<OrderVO>> cancelOrder(@Valid @RequestBody CancelOrderDTO cancelOrderDTO) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        OrderVO orderVO = orderService.cancelOrder(username, cancelOrderDTO);
        return ResponseEntity.ok(ResponseVO.success(orderVO));
    }
}