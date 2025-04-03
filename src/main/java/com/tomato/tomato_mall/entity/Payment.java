package com.tomato.tomato_mall.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付记录实体类
 * <p>
 * 该类定义了系统中支付记录的数据结构，用于存储订单支付的详细信息。
 * </p>
 */
@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    
    /**
     * 支付ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long id;

    /**
     * 关联的订单
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    /**
     * 支付金额
     */
    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    /**
     * 支付方式
     */
    @Column(name = "payment_method", nullable = false, length = 50)
    private String paymentMethod;

    /**
     * 支付状态
     */
    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private PaymentStatus status = PaymentStatus.PENDING;

    /**
     * 第三方交易号
     */
    @Column(name = "trade_no", length = 64)
    private String tradeNo;

    /**
     * 支付创建时间
     */
    @Column(name = "create_time", nullable = false)
    private LocalDateTime createTime;

    /**
     * 支付完成时间
     */
    @Column(name = "complete_time")
    private LocalDateTime completeTime;

    /**
     * 支付状态枚举
     */
    public enum PaymentStatus {
        PENDING,  // 处理中
        SUCCESS,  // 支付成功
        FAILED,   // 支付失败
        TIMEOUT   // 支付超时
    }

    /**
     * 预创建方法
     */
    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
    }
}