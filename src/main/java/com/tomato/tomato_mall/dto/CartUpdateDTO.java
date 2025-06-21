package com.tomato.tomato_mall.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 购物车更新数据传输对象
 * <p>
 * 该DTO封装了更新购物车商品数量时所需的信息，仅包含需要更新的商品数量。
 * 通过Bean Validation确保数据的有效性。
 * </p>
 * <p>
 * 主要用于用户修改购物车中已存在商品的数量操作，支持增加或减少商品数量。
 * </p>
 *
 * @author Team CBDDL
 * @version 1.0
 */
@Data
public class CartUpdateDTO {
    
    /**
     * 商品数量
     * <p>
     * 要更新的购物车商品数量，必填字段且必须大于等于1。
     * 用于修改购物车中指定商品的购买数量。
     * </p>
     */
    @NotNull(message = "Product quantity cannot be null")
    @Min(value = 1, message = "Product quantity must be at least 1")
    private Integer quantity;
}