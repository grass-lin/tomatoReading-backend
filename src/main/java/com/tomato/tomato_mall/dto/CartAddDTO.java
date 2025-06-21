package com.tomato.tomato_mall.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 购物车添加数据传输对象
 * <p>
 * 该DTO封装了添加商品到购物车时所需的信息，包括商品ID和数量。
 * 通过Bean Validation确保数据的有效性。
 * </p>
 * <p>
 * 主要用于用户将商品添加到购物车的操作，支持指定商品数量。
 * </p>
 *
 * @author Team CBDDL
 * @version 1.0
 */
@Data
public class CartAddDTO {
    
    /**
     * 商品ID
     * <p>
     * 要添加到购物车的商品唯一标识符，必填字段。
     * 用于关联具体的商品信息。
     * </p>
     */
    @NotNull(message = "Product ID cannot be null")
    private Long productId;
    
    /**
     * 商品数量
     * <p>
     * 要添加到购物车的商品数量，必填字段且必须大于等于1。
     * 用于指定用户购买该商品的数量。
     * </p>
     */
    @NotNull(message = "Product quantity cannot be null")
    @Min(value = 1, message = "Product quantity must be at least 1")
    private Integer quantity;
}