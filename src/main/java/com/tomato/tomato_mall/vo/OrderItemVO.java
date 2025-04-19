package com.tomato.tomato_mall.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 订单项视图对象
 * <p>
 * 该类用于向前端返回订单中的单个商品项信息，包括商品ID、名称、价格、数量、小计金额和状态等。
 * 作为视图对象，它封装了前端展示订单项所需的数据，隐藏了内部实现细节。
 * </p>
 * <p>
 * 主要用于订单详情中商品列表的数据展示。
 * </p>
 *
 * @author Team CBDDL
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemVO {

    /**
     * 订单项ID
     * <p>
     * 订单项的唯一标识符
     * </p>
     */
    private Long id;

    /**
     * 商品ID
     * <p>
     * 订单项对应的商品唯一标识符
     * </p>
     */
    private Long productId;

    /**
     * 商品名称
     * <p>
     * 订单项中商品的名称
     * </p>
     */
    private String productName;

    /**
     * 商品单价
     * <p>
     * 订单项中商品的单价，使用BigDecimal确保精确的货币计算
     * </p>
     */
    private BigDecimal price;

    /**
     * 购买数量
     * <p>
     * 订单项中商品的购买数量
     * </p>
     */
    private Integer quantity;

    /**
     * 小计金额
     * <p>
     * 该订单项的总金额，等于单价乘以数量，使用BigDecimal确保精确的货币计算
     * </p>
     */
    private BigDecimal subtotal;

    /**
     * 订单项状态
     * <p>
     * 当前订单项的处理状态，如"NORMAL"(正常)、"RETURNED"(已退货)等
     * </p>
     */
    private String status;
}