package com.tomato.tomato_mall.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 库存视图对象
 * <p>
 * 该类用于向前端返回商品库存相关信息，包括库存ID、可售数量、冻结数量和关联的商品ID。
 * 作为视图对象，它封装了前端所需的库存数据，剔除了内部实现细节，避免了实体类的直接暴露。
 * </p>
 * <p>
 * 该类通常用于：
 * 1. REST API的响应数据
 * 2. 前端库存展示和管理界面
 * 3. 商品详情页中的库存信息展示
 * </p>
 *
 * @author Team CBDDL
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockpileVO {
    /**
     * 库存ID
     * <p>
     * 库存记录的唯一标识符
     * </p>
     */
    private Long id;

    /**
     * 可售数量
     * <p>
     * 商品当前可以销售的库存数量
     * </p>
     */
    private Integer amount;

    /**
     * 冻结数量
     * <p>
     * 商品当前被锁定的库存数量，通常表示已下单但未完成支付的商品数量
     * </p>
     */
    private Integer frozen;

    /**
     * 商品ID
     * <p>
     * 该库存关联的商品ID，标识库存所属的商品
     * </p>
     */
    private Long productId;
}