package com.tomato.tomato_mall.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 取消订单数据传输对象
 * <p>
 * 该DTO封装了取消订单时所需的信息，包括订单ID和取消原因。
 * 通过Bean Validation确保关键数据的有效性。
 * </p>
 *
 * @author Team CBDDL
 * @version 1.0
 */
@Data
public class CancelOrderDTO {
    
    /**
     * 订单ID
     * 用于标识需要取消的订单
     */
    @NotNull(message = "Order ID must not be empty")
    private Long orderId;

    /**
     * 取消原因
     * 可选字段，用于记录用户取消订单的原因
     */
    private String reason;
}
