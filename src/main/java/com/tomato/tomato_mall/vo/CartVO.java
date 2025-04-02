package com.tomato.tomato_mall.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * 购物车视图对象
 * <p>
 * 该类用于向前端返回完整的购物车信息，包括购物车商品列表、商品种类总数和总金额等。
 * 作为视图对象，它封装了前端所需的购物车汇总数据。
 * </p>
 *
 * @author Team CBDDL
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartVO {
    private List<CartItemVO> items;
    private Integer total;
    private BigDecimal totalAmount;
}