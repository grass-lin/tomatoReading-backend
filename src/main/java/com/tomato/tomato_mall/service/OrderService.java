package com.tomato.tomato_mall.service;

import com.tomato.tomato_mall.dto.CheckoutDTO;
import com.tomato.tomato_mall.dto.PaymentCallbackDTO;
import com.tomato.tomato_mall.vo.OrderVO;
import com.tomato.tomato_mall.vo.PaymentCallbackVO;
import com.tomato.tomato_mall.vo.PaymentVO;

/**
 * 订单服务接口
 * <p>
 * 该接口定义了订单管理的核心业务功能，包括订单创建、支付处理和支付回调等操作。
 * 作为系统订单管理的核心组件，提供了订单从创建到支付完成的全流程管理功能。
 * </p>
 * <p>
 * 接口的实现类通常需要与购物车服务、库存服务、支付网关等组件协作，
 * 以完成订单处理的各个环节，确保交易的一致性和安全性。
 * </p>
 *
 * @author Team Tomato
 * @version 1.0
 */
public interface OrderService {
    
    /**
     * 从购物车创建订单
     * <p>
     * 根据用户选择的购物车商品创建订单，包括验证库存、锁定库存、计算金额等操作。
     * 创建成功后返回订单信息，同时从购物车中移除已结算的商品。
     * </p>
     *
     * @param username 用户名
     * @param checkoutDTO 结算数据传输对象，包含商品ID、配送地址和支付方式等信息
     * @return 创建成功的订单视图对象
     * @throws IllegalArgumentException 当商品库存不足或数据无效时抛出此异常
     * @throws java.util.NoSuchElementException 当用户或商品不存在时抛出此异常
     */
    OrderVO createOrder(String username, CheckoutDTO checkoutDTO);
    
    /**
     * 发起订单支付
     * <p>
     * 根据订单ID生成支付表单，供前端调用第三方支付平台进行支付。
     * 目前支持支付宝支付方式。
     * </p>
     *
     * @param orderId 订单ID
     * @return 支付信息视图对象，包含支付表单和订单信息
     * @throws java.util.NoSuchElementException 当订单不存在时抛出此异常
     * @throws IllegalStateException 当订单状态不允许支付时抛出此异常
     */
    PaymentVO initiatePayment(String orderId);
    
    /**
     * 处理支付回调
     * <p>
     * 处理支付平台的异步通知，验证支付结果，更新订单状态，释放或扣减库存。
     * 支付成功时将订单状态更新为已支付，支付失败时可能需要恢复库存。
     * </p>
     *
     * @param callbackDTO 支付回调数据传输对象，包含支付结果和订单信息
     * @return 处理结果视图对象，包含订单状态和交易号等信息
     * @throws java.security.SignatureException 当支付回调验签失败时抛出此异常
     * @throws java.util.NoSuchElementException 当订单不存在时抛出此异常
     */
    PaymentCallbackVO processPaymentCallback(PaymentCallbackDTO callbackDTO);
    
    /**
     * 查询订单详情
     * <p>
     * 根据订单ID查询订单的详细信息，包括订单状态、金额、商品清单等。
     * </p>
     *
     * @param orderId 订单ID
     * @return 订单视图对象
     * @throws java.util.NoSuchElementException 当订单不存在时抛出此异常
     */
    OrderVO getOrderById(String orderId);
}