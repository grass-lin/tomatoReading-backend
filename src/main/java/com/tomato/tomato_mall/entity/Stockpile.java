package com.tomato.tomato_mall.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 库存实体类
 * <p>
 * 该类定义了系统中商品库存的数据结构，用于存储和管理商品的库存信息。
 * 作为系统的核心实体之一，Stockpile 实体负责追踪每个商品的可用库存和已冻结库存，
 * 支持库存管理、库存预警、订单处理等核心业务功能。
 * </p>
 * <p>
 * 该实体通过JPA注解映射到数据库中的"stockpiles"表，使用自增长的ID作为主键，
 * 并通过外键与商品实体建立一对一关系。
 * </p>
 * <p>
 * 实体使用Lombok注解简化了代码，自动生成了getter、setter、equals、hashCode和toString方法。
 * </p>
 *
 * @author Team Tomato
 * @version 1.0
 * @see Product
 */
@Entity
@Table(name = "stockpiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Stockpile {

    /**
     * 库存ID
     * <p>
     * 系统自动生成的唯一标识符，作为库存实体的主键。
     * 采用自增长策略，由数据库在插入记录时自动分配。
     * </p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 可用库存数量
     * <p>
     * 商品当前可售的库存数量，不允许为空。
     * 表示可以立即用于销售或分配给新订单的商品数量。
     * </p>
     */
    @Column(nullable = false)
    private Integer amount;

    /**
     * 冻结库存数量
     * <p>
     * 商品当前被锁定的库存数量，不允许为空。
     * 表示已被订单预留但尚未完成支付或发货的商品数量，
     * 这部分数量不可用于新订单的分配。
     * </p>
     */
    @Column(nullable = false)
    private Integer frozen;

    /**
     * 关联的商品
     * <p>
     * 与该库存记录关联的商品实体，采用一对一关系映射。
     * 表示这条库存记录所属的商品。
     * 通过外键product_id关联到products表，不允许为空。
     * </p>
     */
    @OneToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
}