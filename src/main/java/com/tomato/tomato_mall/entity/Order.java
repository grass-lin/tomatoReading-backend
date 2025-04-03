package com.tomato.tomato_mall.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 订单实体类
 * <p>
 * 该类定义了系统中订单的数据结构，用于存储用户下单的信息。
 * 作为系统的核心实体之一，Order实体与订单相关的所有操作紧密关联。
 * </p>
 */
@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    
    /**
     * 订单ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

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
     * 支付时间
     */
    @Column(name = "payment_time")
    private LocalDateTime paymentTime;

    /**
     * 第三方交易号
     */
    @Column(name = "trade_no", length = 64)
    private String tradeNo;

    /**
     * 添加订单项
     *
     * @param item 要添加的订单项
     */
    public void addOrderItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }

    /**
     * 移除订单项
     *
     * @param item 要移除的订单项
     */
    public void removeOrderItem(OrderItem item) {
        items.remove(item);
        item.setOrder(null);
    }

    /**
     * 订单状态枚举
     */
    public enum OrderStatus {
        PENDING,  // 待支付
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