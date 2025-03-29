package com.tomato.tomato_mall.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import java.math.BigDecimal;
import java.util.Set;

/**
 * 商品实体类
 * <p>
 * 该类定义了系统中商品的数据结构，用于存储商品的基本信息。
 * 作为系统的核心实体之一，Product 实体与商品相关的所有操作紧密关联，
 * 包括但不限于：商品展示、商品管理、库存管理等功能。
 * </p>
 * <p>
 * 该实体通过JPA注解映射到数据库中的"products"表，使用自增长的ID作为主键。
 * </p>
 * <p>
 * 实体使用Lombok注解简化了代码，自动生成了getter、setter、equals、hashCode和toString方法。
 * </p>
 *
 * @author Team CBDDL
 * @version 1.0
 */
@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    /**
     * 商品ID
     * <p>
     * 系统自动生成的唯一标识符，作为商品实体的主键。
     * 采用自增长策略，由数据库在插入记录时自动分配。
     * </p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 商品名称
     * <p>
     * 商品的标题或名称，不允许为空。
     * 最大长度为50个字符。
     * </p>
     */
    @Column(nullable = false, length = 50)
    private String title;

    /**
     * 商品价格
     * <p>
     * 商品的销售价格，不允许为空，最低为0元。
     * 使用BigDecimal类型以确保精确的金额计算。
     * </p>
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    /**
     * 商品评分
     * <p>
     * 商品的评分，范围从0分到10分，不允许为空。
     * </p>
     */
    @Column(nullable = false)
    private Double rate;

    /**
     * 商品描述
     * <p>
     * 商品的简要描述，可以为空。
     * 最大长度为255个字符。
     * </p>
     */
    @Column(length = 255)
    private String description;

    /**
     * 商品封面图片URL
     * <p>
     * 商品的封面图片链接地址，可以为空。
     * 最大长度为500个字符。
     * </p>
     */
    @Column(length = 500)
    private String cover;

    /**
     * 商品详细说明
     * <p>
     * 商品的详细介绍，可以为空。
     * 最大长度为500个字符。
     * </p>
     */
    @Column(length = 500)
    private String detail;

    /**
     * 商品规格列表
     * <p>
     * 与该商品关联的规格列表，采用一对多关系映射。
     * 当删除商品时，级联删除关联的规格记录。
     * </p>
     */
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Specification> specifications;

    /**
     * 商品库存
     * <p>
     * 与该商品关联的库存信息，采用一对一关系映射。
     * 当删除商品时，级联删除关联的库存记录。
     * </p>
     */
    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private Stockpile stockpile;
}