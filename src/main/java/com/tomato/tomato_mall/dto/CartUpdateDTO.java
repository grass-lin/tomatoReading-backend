package com.tomato.tomato_mall.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 购物车更新数据传输对象
 * <p>
 * 该DTO封装了更新购物车商品数量时所需的信息。
 * 通过Bean Validation确保数据的有效性。
 * </p>
 *
 * @author Team CBDDL
 * @version 1.0
 */
@Data
public class CartUpdateDTO {
    @NotNull(message = "Product quantity cannot be null")
    @Min(value = 1, message = "Product quantity must be at least 1")
    private Integer quantity;
}