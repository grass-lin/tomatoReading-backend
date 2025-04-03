package com.tomato.tomato_mall.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单视图对象
 * <p>
 * 该类用于向前端返回订单信息，包括订单ID、用户ID、订单金额、支付方式、创建时间和订单状态等。
 * 作为视图对象，它封装了前端所需的订单数据，隐藏了内部实现细节。
 * </p>
 * <p>
 * 主要用于订单创建后的信息返回，以及订单查询接口的数据展示。
 * </p>
 *
 * @author Team Tomato
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderVO {
    
    /**
     * 订单ID
     * <p>
     * 订单的唯一标识符
     * </p>
     */
    private String orderId;
    
    /**
     * 用户ID
     * <p>
     * 下单用户的唯一标识符
     * </p>
     */
    private String userId;
    
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
     * 用户选择的支付方式，如"ALIPAY"
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
     * 订单状态
     * <p>
     * 当前订单的处理状态，如"PENDING"(待支付)
     * </p>
     */
    private String status;
}