package com.tomato.tomato_mall.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 物流信息实体类
 * <p>
 * 该类定义了系统中物流信息的数据结构，用于存储订单项的物流配送信息。
 * 包括物流公司、物流单号等关键信息，与订单项实体形成一对一关系。
 * </p>
 * 
 * @author Team CBDDL
 * @version 1.0
 */
@Entity
@Table(name = "logistics")
@Data
public class Logistics {

    /**
     * 物流信息ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "logistics_id")
    private Long id;

    /**
     * 关联的订单项
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id", nullable = false, unique = true)
    private OrderItem orderItem;

    /**
     * 物流公司编码
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "company", nullable = false)
    private LogisticsCompany company;

    /**
     * 物流单号
     */
    @Column(name = "tracking_number", nullable = false, length = 50)
    private String trackingNumber;

    /**
     * 创建时间
     */
    @Column(name = "create_time", nullable = false)
    private LocalDateTime createTime;


    public enum LogisticsCompany {
        ZTO("中通快递"),
        YTO("圆通速递"),
        YD("韵达快递"),
        STO("申通快递"),
        SF("顺丰速运"),
        JD("京东物流"),
        YZPY("邮政快递包裹"),
        EMS("EMS"),
        JTSD("极兔速递");

        private final String displayName;

        LogisticsCompany(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /**
     * 预创建方法
     */
    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
    }
}