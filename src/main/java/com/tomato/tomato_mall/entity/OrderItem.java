package com.tomato.tomato_mall.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 订单项实体类
 * <p>
 * 该类定义了系统中订单项的数据结构，用于存储订单中每个商品的详细信息。
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
    @JoinColumn(name = "product_id", nullable = false)
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
    @PreUpdate
    protected void onSave() {
        updateSubtotal();
    }
}