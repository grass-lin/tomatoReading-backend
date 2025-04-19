package com.tomato.tomato_mall.vo;

import lombok.Data;

/**
 * 商品规格视图对象
 * <p>
 * 该类用于向前端返回商品规格相关信息，包括规格ID、规格项名称、规格值和关联的商品ID。
 * 作为视图对象，它封装了前端所需的规格数据，避免了实体类的直接暴露，增强了系统的安全性和灵活性。
 * </p>
 * <p>
 * 该类通常用于：
 * 1. 商品详情页面中规格信息的展示
 * 2. 商品编辑页面中规格信息的回显
 * 3. 购物车和订单中商品规格的选择和展示
 * </p>
 *
 * @author Team CBDDL
 * @version 1.0
 * @see ProductVO
 */
@Data
public class SpecificationVO {

    /**
     * 规格ID
     * <p>
     * 规格记录的唯一标识符
     * </p>
     */
    private Long id;

    /**
     * 规格项名称
     * <p>
     * 规格的分类或名称
     * </p>
     */
    private String item;

    /**
     * 规格值
     * <p>
     * 规格项的具体值
     * </p>
     */
    private String value;

    /**
     * 商品ID
     * <p>
     * 该规格关联的商品ID，标识规格所属的商品
     * </p>
     */
    private Long productId;
}