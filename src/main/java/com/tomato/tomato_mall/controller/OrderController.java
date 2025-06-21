package com.tomato.tomato_mall.controller;

import com.tomato.tomato_mall.dto.CancelOrderDTO;
import com.tomato.tomato_mall.dto.PaymentCallbackDTO;
import com.tomato.tomato_mall.service.OrderService;
import com.tomato.tomato_mall.vo.OrderDetailVO;
import com.tomato.tomato_mall.vo.OrderItemVO;
import com.tomato.tomato_mall.vo.OrderVO;
import com.tomato.tomato_mall.vo.PaymentVO;
import com.tomato.tomato_mall.vo.ResponseVO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * 订单控制器
 * <p>
 * 提供订单创建、支付处理和支付回调等功能的REST API接口
 * 除支付回调外的接口返回统一的ResponseVO格式，包含状态码、消息和数据
 * 管理员拥有获取所有订单信息的权限
 * </p>
 * 
 * @author Team CBDDL
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
   * </p>
   * 
   * @param orderId 订单ID
   * @return 返回包含支付表单和订单信息的响应体，状态码200
   */
  @PostMapping("/{orderId}/pay")
  public ResponseEntity<ResponseVO<PaymentVO>> initiatePayment(@PathVariable String orderId) {
    PaymentVO paymentVO = orderService.initiatePayment(orderId);
    return ResponseEntity.ok(ResponseVO.success(paymentVO));
  }

  /**
   * 支付回调处理接口
   * <p>
   * 处理支付平台的异步通知，验证支付结果，更新订单状态和库存
   * </p>
   * 
   * @param request  HTTP请求对象，包含支付平台回调参数
   * @param response HTTP响应对象，用于向支付平台返回处理结果
   * @throws IOException 当写入响应时发生IO异常时抛出
   */
  @PostMapping("/notify")
  public void handlePaymentCallback(HttpServletRequest request, HttpServletResponse response) throws IOException {
    try {
      Map<String, String> params = new HashMap<>();
      Map<String, String[]> requestParams = request.getParameterMap();
      for (String key : requestParams.keySet()) {
        params.put(key, requestParams.get(key)[0]);
      }

      PaymentCallbackDTO callbackDTO = new PaymentCallbackDTO(params);
      boolean success = orderService.handlePaymentCallback(callbackDTO);
      response.getWriter().print(success ? "success" : "fail");
    } catch (Exception e) {
      response.getWriter().print("fail");
    }
  }

  /**
   * 获取当前用户所有订单接口
   * <p>
   * 返回当前认证用户的所有订单列表
   * </p>
   *
   * @return 返回包含订单列表的响应体，状态码200
   * @throws java.util.NoSuchElementException 当用户不存在时抛出
   */
  @GetMapping
  public ResponseEntity<ResponseVO<List<OrderVO>>> getOrders() {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    List<OrderVO> orders = orderService.getOrdersByUsername(username);
    return ResponseEntity.ok(ResponseVO.success(orders));
  }

  /**
   * 获取所有订单接口
   * <p>
   * 返回系统中的所有订单列表，仅限管理员访问
   * </p>
   *
   * @return 返回包含所有订单列表的响应体，状态码200
   */
  @GetMapping("/all")
  @PreAuthorize("hasRole('admin')")
  public ResponseEntity<ResponseVO<List<OrderVO>>> getAllOrders() {
    List<OrderVO> orders = orderService.getAllOrders();
    return ResponseEntity.ok(ResponseVO.success(orders));
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
  public ResponseEntity<ResponseVO<OrderDetailVO>> getOrderDetail(@PathVariable String orderId) {
    OrderDetailVO orderDetailVO = orderService.getOrderById(orderId);
    return ResponseEntity.ok(ResponseVO.success(orderDetailVO));
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
   * @throws IllegalStateException            当订单状态不允许取消时抛出
   */
  @DeleteMapping
  public ResponseEntity<ResponseVO<OrderDetailVO>> cancelOrder(@Valid @RequestBody CancelOrderDTO cancelOrderDTO) {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    OrderDetailVO orderDetailVO = orderService.cancelOrder(username, cancelOrderDTO);
    return ResponseEntity.ok(ResponseVO.success(orderDetailVO));
  }

  /**
   * 确认收货接口
   * <p>
   * 用户确认收到指定订单项的商品，将订单项状态更新为已完成
   * </p>
   *
   * @param orderItemId 订单项ID
   * @return 返回包含更新后订单项信息的响应体，状态码200
   */
  @PatchMapping("/receive/{orderItemId}")
  public ResponseEntity<ResponseVO<OrderItemVO>> confirmReceive(@PathVariable Long orderItemId) {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    OrderItemVO orderItemVO = orderService.confirmReceive(username, orderItemId);
    return ResponseEntity.ok(ResponseVO.success(orderItemVO));
  }
}