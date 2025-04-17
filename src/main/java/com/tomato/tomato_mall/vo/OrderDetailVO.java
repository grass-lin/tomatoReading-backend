package com.tomato.tomato_mall.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单详情视图对象
 * <p>
 * 该类用于向前端返回订单详细信息，包括订单基本信息、收货人信息、取消原因以及订单项列表等。
 * 作为视图对象，它封装了前端展示订单详情所需的完整数据，隐藏了内部实现细节。
 * </p>
 * <p>
 * 主要用于订单详情查询接口的数据展示。
 * </p>
 *
 * @author Team CBDDL
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDetailVO {

    /**
     * 订单ID
     * <p>
     * 订单的唯一标识符
     * </p>
     */
    private Long id;

    /**
     * 用户名
     * <p>
     * 下单用户的用户名
     * </p>
     */
    private String username;

    /**
     * 订单总金额
     * <p>
     * 订单中所有商品的总价，使用BigDecimal确保精确的货币计算
     * </p>
     */
    private BigDecimal totalAmount;

    /**
     * 支付方式
     * <p>
     * 用户选择的支付方式，如"ALIPAY"或"WECHAT"
     * </p>
     */
    private String paymentMethod;

    /**
     * 订单创建时间
     * <p>
     * 订单在系统中创建的时间戳
     * </p>
     */
    private LocalDateTime createTime;

    /**
     * 订单更新时间
     * <p>
     * 订单信息最后一次更新的时间戳
     * </p>
     */
    private LocalDateTime updateTime;

    /**
     * 订单状态
     * <p>
     * 当前订单的处理状态，如"PENDING"(待支付)、"PAID"(已支付)、"SHIPPED"(已发货)等
     * </p>
     */
    private String status;

    /**
     * 收货人姓名
     * <p>
     * 订单收货地址中的收件人姓名
     * </p>
     */
    private String receiverName;

    /**
     * 收货人电话
     * <p>
     * 收货人的联系电话
     * </p>
     */
    private String receiverPhone;

    /**
     * 收货地址
     * <p>
     * 详细的收货地址信息
     * </p>
     */
    private String shippingAddress;

    /**
     * 邮政编码
     * <p>
     * 收货地址对应的邮政编码
     * </p>
     */
    private String zipCode;

    /**
     * 订单取消原因
     * <p>
     * 用户取消订单时提供的原因，仅在订单被取消时有值
     * </p>
     */
    private String cancelReason;

    /**
     * 订单取消时间
     * <p>
     * 用户取消订单的时间戳，仅在订单被取消时有值
     * </p>
     */
    private LocalDateTime cancelTime;

    /**
     * 订单项列表
     * <p>
     * 包含该订单中所有商品项的详细信息列表
     * </p>
     */
    private List<OrderItemVO> items;
}