package com.tomato.tomato_mall.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 购物车商品视图对象
 * <p>
 * 该类用于向前端返回购物车商品信息，包括购物车商品ID、商品基本信息和数量等。
 * 作为视图对象，它封装了前端所需的购物车数据，剔除了内部实现细节。
 * </p>
 *
 * @author Team CBDDL
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemVO {
    private Long cartItemId;
    private Long productId;
    private String title;
    private BigDecimal price;
    private String description;
    private String cover;
    private String detail;
    private Integer quantity;
}