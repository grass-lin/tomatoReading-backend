package com.tomato.tomato_mall.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 订单实体类
 * <p>
 * 该类定义了系统中订单的数据结构，用于存储用户下单的信息。
 * 扩展了订单状态枚举，增强了订单生命周期管理能力。
 * </p>
 */
@Entity
@Table(name = "orders")
@Data
public class Order {

    /**
     * 订单ID - 基于时间戳生成
     */
    @Id
    @UuidGenerator
    @Column(name = "order_id")
    private String id;

    /**
     * 关联的用户
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * 订单总金额
     */
    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    /**
     * 支付方式
     */
    @Column(name = "payment_method", nullable = false, length = 50)
    private String paymentMethod;

    /**
     * 订单状态
     */
    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.PENDING;

    /**
     * 订单创建时间
     */
    @Column(name = "create_time")
    private LocalDateTime createTime;

    /**
     * 订单更新时间
     */
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    /**
     * 订单项列表
     */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    /**
     * 收货人姓名
     */
    @Column(name = "receiver_name", nullable = false, length = 50)
    private String receiverName;

    /**
     * 收货人电话
     */
    @Column(name = "receiver_phone", nullable = false, length = 20)
    private String receiverPhone;

    /**
     * 收货地址
     */
    @Column(name = "shipping_address", nullable = false, length = 200)
    private String shippingAddress;

    /**
     * 邮政编码
     */
    @Column(name = "zip_code", length = 10)
    private String zipCode;

    /**
     * 订单取消原因
     */
    @Column(name = "cancel_reason", length = 200)
    private String cancelReason;

    /**
     * 订单取消时间
     */
    @Column(name = "cancel_time")
    private LocalDateTime cancelTime;

    /**
     * 订单状态枚举
     */
    public enum OrderStatus {
        /**
         * 待支付状态 - 订单已创建但尚未完成支付
         */
        PENDING,

        /**
         * 已支付状态 - 订单已完成支付，等待处理
         */
        PAID,

        /**
         * 已完成状态 - 买家已确认收货，订单完成
         */
        COMPLETED,

        /**
         * 已取消状态 - 订单已被取消，可能是超时未支付或用户主动取消
         */
        CANCELLED,

        /**
         * 支付超时状态 - 超时未支付
         */
        TIMEOUT,
    }

    /**
     * 判断订单是否可取消
     */
    public boolean canCancel() {
        return status == OrderStatus.PENDING;
    }

    /**
     * 判断订单是否可支付
     */
    public boolean canPay() {
        return status == OrderStatus.PENDING;
    }

    /**
     * 预创建方法
     */
    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
    }

    /**
     * 预更新方法
     */
    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }
}