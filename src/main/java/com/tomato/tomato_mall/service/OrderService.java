package com.tomato.tomato_mall.service;

import com.tomato.tomato_mall.dto.CancelOrderDTO;
import com.tomato.tomato_mall.dto.CheckoutDTO;
import com.tomato.tomato_mall.dto.PaymentCallbackDTO;
import com.tomato.tomato_mall.vo.OrderVO;
import com.tomato.tomato_mall.vo.PaymentVO;

import java.util.List;

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
     * @param username    用户名
     * @param checkoutDTO 结算数据传输对象，包含商品ID、配送地址和支付方式等信息
     * @return 创建成功的订单视图对象
     * @throws IllegalArgumentException         当商品库存不足或数据无效时抛出此异常
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
     * @throws IllegalStateException            当订单状态不允许支付时抛出此异常
     */
    PaymentVO initiatePayment(String orderId);

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

    /**
     * 取消订单
     * <p>
     * 根据订单ID取消订单，并记录取消原因。
     * 取消订单后可能需要恢复库存，更新订单状态为已取消。
     * </p>
     *
     * @param username       用户名
     * @param cancelOrderDTO 取消订单数据传输对象，包含订单ID和取消原因等信息
     * @return 取消后的订单视图对象
     * @throws java.util.NoSuchElementException 当订单不存在时抛出此异常
     * @throws IllegalStateException            当订单状态不允许取消时抛出此异常
     */
    OrderVO cancelOrder(String username, CancelOrderDTO cancelOrderDTO);

    /**
     * 处理支付回调
     * <p>
     * 处理来自支付平台的回调请求，验证签名，处理支付结果。
     * 根据支付结果更新订单状态、库存等相关信息。
     * </p>
     *
     * @param callbackDTO 支付回调数据传输对象，包含支付平台回调的所有参数
     * @return 是否处理成功，true表示成功处理
     * @throws IllegalArgumentException 当回调参数无效或签名验证失败时抛出此异常
     */
    boolean handlePaymentCallback(PaymentCallbackDTO callbackDTO);

    /**
     * 获取用户所有订单
     * <p>
     * 根据用户名获取该用户的所有订单信息
     * </p>
     *
     * @param username 用户名
     * @return 用户的订单列表
     * @throws java.util.NoSuchElementException 当用户不存在时抛出此异常
     */
    List<OrderVO> getOrdersByUsername(String username);
}