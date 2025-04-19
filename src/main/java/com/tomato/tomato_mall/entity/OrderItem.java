package com.tomato.tomato_mall.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单项实体类
 * <p>
 * 该类定义了系统中订单项的数据结构，用于存储订单中每个商品的详细信息。
 * 添加了状态字段，用于追踪订单项的生命周期。
 * </p>
 */
@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    /**
     * 订单项ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long id;

    /**
     * 关联的订单
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    /**
     * 关联的商品
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    /**
     * 商品名称
     */
    @Column(name = "product_name", nullable = false, length = 100)
    private String productName;

    /**
     * 商品价格
     */
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    /**
     * 商品数量
     */
    @Column(name = "quantity", nullable = false)
    private Integer quantity = 1;

    /**
     * 商品小计
     */
    @Column(name = "subtotal", nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    /**
     * 订单项状态
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderItemStatus status = OrderItemStatus.PENDING;

    /**
     * 原购物车项ID，用于追踪来源
     */
    @Column(name = "cart_item_id")
    private Long cartItemId;

    /**
     * 最后更新时间
     */
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    /**
     * 订单项状态枚举
     */
    public enum OrderItemStatus {
        /**
         * 待支付状态 - 订单已创建，等待支付
         */
        PENDING,

        /**
         * 已支付状态 - 订单已支付，等待发货
         */
        PAID,

        /**
         * 已发货状态 - 商品已发货，等待收货
         */
        SHIPPED,

        /**
         * 已完成状态 - 订单已完成
         */
        COMPLETED,

        /**
         * 已取消状态 - 订单已取消
         */
        CANCELLED,

    }

    /**
     * 计算小计金额
     */
    public BigDecimal calculateSubtotal() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }

    /**
     * 更新小计金额
     */
    public void updateSubtotal() {
        this.subtotal = calculateSubtotal();
    }

    /**
     * 预创建/预更新方法
     */
    @PrePersist
    protected void onCreate() {
        updateSubtotal();
        updateTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateSubtotal();
        updateTime = LocalDateTime.now();
    }
}