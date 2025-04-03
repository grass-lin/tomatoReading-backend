package com.tomato.tomato_mall.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.List;

/**
 * 订单结算数据传输对象
 * <p>
 * 该DTO封装了用户结算购物车时提交的信息，包括用户ID、选中的购物车商品ID列表、
 * 收货地址信息以及支付方式等。用于从前端向后端传递创建订单所需的数据。
 * </p>
 * <p>
 * 主要用于订单创建流程的第一步，实现从购物车到订单的转换。
 * </p>
 *
 * @author Team Tomato
 * @version 1.0
 */
@Data
public class CheckoutDTO {

    /**
     * 购物车商品ID列表
     * <p>
     * 用户选择结算的购物车商品ID列表，支持部分商品结算
     * </p>
     */
    @NotEmpty(message = "Cart item IDs must not be empty")
    private List<Long> cartItemIds;

    /**
     * 收货地址信息
     * <p>
     * 包含收货人姓名、电话、地址、邮编等信息
     * </p>
     */
    @Valid
    private ShippingAddressDTO shippingAddress;

    /**
     * 支付方式
     * <p>
     * 用户选择的支付方式，目前仅支持支付宝
     * </p>
     */
    @NotBlank(message = "Payment method must not be empty")
    @Pattern(regexp = "ALIPAY", message = "Currently only supports Alipay payment method")
    private String paymentMethod;
}