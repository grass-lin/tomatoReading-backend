package com.tomato.tomato_mall.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 支付视图对象
 * <p>
 * 该类用于向前端返回支付相关信息，包括支付表单、订单ID、订单金额和支付方式等。
 * 作为视图对象，它封装了前端发起支付所需的数据。
 * </p>
 * <p>
 * 主要用于支付流程的第二步，前端通过该对象获取支付表单并展示给用户。
 * </p>
 *
 * @author Team Tomato
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentVO {
    
    /**
     * 支付表单
     * <p>
     * 第三方支付平台(如支付宝)生成的支付表单HTML，前端直接渲染展示
     * </p>
     */
    private String paymentForm;
    
    /**
     * 订单ID
     * <p>
     * 要支付的订单唯一标识符
     * </p>
     */
    private String orderId;
    
    /**
     * 订单总金额
     * <p>
     * 订单的应付金额，使用BigDecimal确保精确的货币计算
     * </p>
     */
    private BigDecimal totalAmount;
    
    /**
     * 支付方式
     * <p>
     * 用户选择的支付方式，目前仅支持"Alipay"
     * </p>
     */
    private String paymentMethod;
}