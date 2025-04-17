package com.tomato.tomato_mall.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * 广告实体类
 * <p>
 * 该类定义了系统中广告的数据结构，用于存储广告的基本信息。
 * 作为系统的营销组件之一，Advertisement 实体与商品推广和营销活动紧密关联，
 * 包括但不限于：广告展示、广告管理、商品推广等功能。
 * </p>
 * <p>
 * 该实体通过JPA注解映射到数据库中的"advertisements"表，使用自增长的ID作为主键。
 * </p>
 * <p>
 * 实体使用Lombok注解简化了代码，自动生成了getter、setter、equals、hashCode和toString方法。
 * </p>
 *
 * @author Team CBDDL
 * @version 1.0
 */
@Entity
@Table(name = "advertisements")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Advertisement {
    /**
     * 广告ID
     * <p>
     * 系统自动生成的唯一标识符，作为广告实体的主键。
     * 采用自增长策略，由数据库在插入记录时自动分配。
     * </p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 广告标题
     * <p>
     * 广告的标题或名称，不允许为空。
     * 最大长度为50个字符。
     * </p>
     */
    @Column(nullable = false, length = 50)
    private String title;

    /**
     * 广告内容
     * <p>
     * 广告的详细描述或内容，不允许为空。
     * 最大长度为500个字符。
     * </p>
     */
    @Column(nullable = false, length = 500)
    private String content;

    /**
     * 广告图片URL
     * <p>
     * 广告的图片链接地址，不允许为空。
     * 最大长度为500个字符。
     * </p>
     */
    @Column(name = "image_url", nullable = false, length = 500)
    private String imageUrl;

    /**
     * 所属商品
     * <p>
     * 广告关联的商品，采用多对一关系映射。
     * 不允许为空，必须关联到一个有效的商品。
     * </p>
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    /**
     * 创建时间
     * <p>
     * 广告记录的创建时间，系统自动生成。
     * </p>
     */
    @Column(name = "create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     * <p>
     * 广告记录的最后更新时间，系统自动更新。
     * </p>
     */
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    /**
     * 预创建方法
     * <p>
     * 在实体创建时自动设置时间戳。
     * </p>
     */
    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
    }

    /**
     * 预更新方法
     * <p>
     * 在实体更新时自动设置更新时间戳。
     * </p>
     */
    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }
}