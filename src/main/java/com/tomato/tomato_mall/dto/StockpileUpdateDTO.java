package com.tomato.tomato_mall.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 库存更新数据传输对象
 * <p>
 * 该类用于传递库存数量更新的数据，包含需要更新的库存量信息。
 * 用于商品库存数量的更新操作。
 * </p>
 * 
 * @author Team CBDDL
 * @version 1.0
 */
@Data
public class StockpileUpdateDTO {
    
    /**
     * 需要更新的库存数量
     */
    @NotNull(message = "Amount cannot be null")
    private Integer amount;
}