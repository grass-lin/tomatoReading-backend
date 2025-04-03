package com.tomato.tomato_mall.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 支付回调结果视图对象
 * <p>
 * 该类用于向支付平台返回回调处理结果，包括订单ID、交易状态、支付平台交易号和支付完成时间等。
 * 作为视图对象，它封装了需要返回给支付平台的数据。
 * </p>
 * <p>
 * 主要用于支付流程的第三步，响应支付平台的异步通知。
 * </p>
 *
 * @author Team Tomato
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentCallbackVO {
    
    /**
     * 订单ID
     * <p>
     * 处理完成的订单唯一标识符
     * </p>
     */
    private String orderId;
    
    /**
     * 交易状态
     * <p>
     * 处理后的交易状态，通常为"TRADE_SUCCESS"
     * </p>
     */
    private String status;
    
    /**
     * 支付平台交易号
     * <p>
     * 支付平台生成的交易唯一标识符
     * </p>
     */
    private String tradeNo;
    
    /**
     * 支付完成时间
     * <p>
     * 用户在支付平台完成支付的时间
     * </p>
     */
    private String paymentTime;
}