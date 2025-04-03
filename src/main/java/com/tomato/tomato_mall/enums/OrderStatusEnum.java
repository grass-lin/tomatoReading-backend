package com.tomato.tomato_mall.enums;

/**
 * 订单状态枚举
 * <p>
 * 定义系统中订单的各种状态，用于跟踪订单生命周期。
 * </p>
 *
 * @author Team Tomato
 * @version 1.0
 */
public enum OrderStatusEnum {
    /**
     * 待支付状态
     * 订单已创建但尚未完成支付
     */
    PENDING,
    
    /**
     * 已支付状态
     * 订单已完成支付，等待卖家发货
     */
    PAID,
    
    /**
     * 已发货状态
     * 卖家已发货，等待买家确认收货
     */
    SHIPPED,
    
    /**
     * 已完成状态
     * 买家已确认收货，订单完成
     */
    COMPLETED,
    
    /**
     * 已取消状态
     * 订单已被取消，可能是超时未支付或用户主动取消
     */
    CANCELLED,
    
    /**
     * 已退款状态
     * 订单已退款
     */
    REFUNDED
}