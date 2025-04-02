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
 *
 * @author Team CBDDL
 * @version 1.0
 */
@Data
public class CartAddDTO {
    @NotNull(message = "Product ID cannot be null")
    private Long productId;
    
    @NotNull(message = "Product quantity cannot be null")
    @Min(value = 1, message = "Product quantity must be at least 1")
    private Integer quantity;
}