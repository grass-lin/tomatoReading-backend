package com.tomato.tomato_mall.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 购物车商品实体类
 * <p>
 * 该类定义了系统中购物车商品的数据结构，用于存储用户添加到购物车的商品信息。
 * 作为系统的核心实体之一，CartItem 实体与购物车相关的所有操作紧密关联，
 * 包括但不限于：添加商品到购物车、修改购物车商品数量、删除购物车商品等功能。
 * </p>
 * <p>
 * 该实体通过JPA注解映射到数据库中的"carts"表，使用自增长的ID作为主键。
 * 通过外键关联到用户和商品实体，确保数据的完整性和一致性。
 * </p>
 * <p>
 * 实体使用Lombok注解简化了代码，自动生成了getter、setter、equals、hashCode和toString方法。
 * </p>
 *
 * @author Team CBDDL
 * @version 1.0
 * @see User
 * @see Product
 */
@Entity
@Table(name = "carts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
    /**
     * 购物车商品ID
     * <p>
     * 系统自动生成的唯一标识符，作为购物车商品实体的主键。
     * 采用自增长策略，由数据库在插入记录时自动分配。
     * </p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_item_id")
    private Long id;

    /**
     * 商品数量
     * <p>
     * 用户添加到购物车中的商品数量，不允许为空，默认为1。
     * </p>
     */
    @Column(nullable = false)
    private Integer quantity;

    /**
     * 关联的用户
     * <p>
     * 与该购物车商品关联的用户实体，采用多对一关系映射。
     * 表示这个购物车商品所属的用户。
     * 通过外键user_id关联到users表，不允许为空。
     * </p>
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * 关联的商品
     * <p>
     * 与该购物车商品关联的商品实体，采用多对一关系映射。
     * 表示这个购物车商品对应的实际商品。
     * 通过外键product_id关联到products表，不允许为空。
     * </p>
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
}