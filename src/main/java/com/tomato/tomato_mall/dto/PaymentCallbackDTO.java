package com.tomato.tomato_mall.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 支付回调数据传输对象
 * <p>
 * 该DTO封装了第三方支付平台(如支付宝)回调时传递的支付结果信息，
 * 包括订单ID、支付状态、支付时间、交易号和支付金额等。
 * </p>
 * <p>
 * 主要用于支付流程的第三步，接收并处理支付平台的异步通知。
 * </p>
 *
 * @author Team Tomato
 * @version 1.0
 */
@Data
public class PaymentCallbackDTO {
    
    /**
     * 订单ID
     * <p>
     * 与支付相关联的订单唯一标识符
     * </p>
     */
    @NotBlank(message = "Order ID must not be empty")
    private String orderId;
    
    /**
     * 支付状态
     * <p>
     * 支付平台返回的支付状态，如"TRADE_SUCCESS"
     * </p>
     */
    @NotBlank(message = "Payment status must not be empty")
    private String paymentStatus;
    
    /**
     * 支付完成时间
     * <p>
     * 用户在支付平台完成支付的时间，格式为ISO8601标准时间字符串
     * </p>
     */
    @NotBlank(message = "Payment time must not be empty")
    private String paymentTime;
    
    /**
     * 交易号
     * <p>
     * 支付平台生成的交易唯一标识符
     * </p>
     */
    @NotBlank(message = "Trade number must not be empty")
    private String tradeNo;
    
    /**
     * 支付金额
     * <p>
     * 用户实际支付的金额，使用BigDecimal确保精确的货币计算
     * </p>
     */
    @NotNull(message = "Total amount must not be null")
    private BigDecimal totalAmount;
}