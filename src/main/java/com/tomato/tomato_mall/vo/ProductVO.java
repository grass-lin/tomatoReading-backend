package com.tomato.tomato_mall.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * 商品视图对象
 * <p>
 * 该类用于向前端返回商品信息，包括商品基本信息和关联的规格列表。
 * 作为视图对象，它封装了前端所需的商品数据，剔除了敏感字段和内部实现细节，
 * 避免了实体类的直接暴露，从而增强系统的安全性和可维护性。
 * </p>
 * <p>
 * 该类通常用于：
 * 1. 商品列表页面的商品展示
 * 2. 商品详情页面的信息展示
 * 3. 购物车和订单中的商品信息展示
 * 4. 商品管理后台的数据展示和操作
 * </p>
 *
 * @author Team Tomato
 * @version 1.0
 * @see SpecificationVO
 */
@Data
public class ProductVO {

    /**
     * 商品ID
     * <p>
     * 商品的唯一标识符
     * </p>
     */
    private Long id;

    /**
     * 商品标题
     * <p>
     * 商品的名称或标题，用于在列表和详情页面展示
     * </p>
     */
    private String title;

    /**
     * 商品价格
     * <p>
     * 商品的销售价格，使用BigDecimal确保精确的货币计算
     * </p>
     */
    private BigDecimal price;

    /**
     * 商品评分
     * <p>
     * 商品的平均评分
     * </p>
     */
    private Double rate;

    /**
     * 商品描述
     * <p>
     * 商品的简短描述或摘要，用于在商品列表中展示
     * </p>
     */
    private String description;

    /**
     * 商品封面图
     * <p>
     * 商品的主图或封面图片的URL，用于在列表和详情页面展示
     * </p>
     */
    private String cover;

    /**
     * 商品详情
     * <p>
     * 商品的详细介绍，用于在商品详情页展示。
     * </p>
     */
    private String detail;

    /**
     * 商品规格列表
     * <p>
     * 商品的所有规格信息列表
     * </p>
     * 
     * @see SpecificationVO
     */
    private List<SpecificationVO> specifications;
}