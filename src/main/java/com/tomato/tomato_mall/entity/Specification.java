package com.tomato.tomato_mall.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 商品规格实体类
 * <p>
 * 该类定义了系统中商品规格的数据结构，用于存储商品的各种规格信息，如尺寸、颜色等。
 * 作为Product实体的关联实体，每条Specification记录都必须关联到一个具体商品。
 * </p>
 * <p>
 * 该实体通过JPA注解映射到数据库中的"specifications"表，使用自增长的ID作为主键。
 * </p>
 * <p>
 * 实体使用Lombok注解简化了代码，自动生成了getter、setter、equals、hashCode和toString方法。
 * </p>
 *
 * @author Team CBDDL
 * @version 1.0
 */
@Entity
@Table(name = "specifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Specification {
    /**
     * 规格ID
     * <p>
     * 系统自动生成的唯一标识符，作为规格实体的主键。
     * 采用自增长策略，由数据库在插入记录时自动分配。
     * </p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 规格名称
     * <p>
     * 规格的名称或类型，如"颜色"、"尺寸"等，不允许为空。
     * 最大长度为50个字符。
     * </p>
     */
    @Column(nullable = false, length = 50)
    private String item;

    /**
     * 规格内容
     * <p>
     * 规格的具体内容或值，如"红色"、"XL"等，不允许为空。
     * 最大长度为255个字符。
     * </p>
     */
    @Column(nullable = false)
    private String value;

    /**
     * 所属商品
     * <p>
     * 该规格所属的商品，采用多对一关系映射。
     * 不允许为空，必须关联到一个有效的商品。
     * </p>
     * <p>
     * 在JSON序列化时忽略此字段，以避免循环引用。
     * </p>
     */
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
}