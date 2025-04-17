package com.tomato.tomato_mall.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 广告视图对象
 * <p>
 * 该类用于向前端返回广告信息，包括广告ID、标题、内容、图片URL、关联商品ID和时间戳等。
 * 作为视图对象，它封装了前端所需的广告数据，剔除了内部实现细节，避免了实体类的直接暴露。
 * </p>
 * <p>
 * 该类通常用于：
 * 1. REST API的响应数据
 * 2. 前端广告展示和管理界面
 * </p>
 *
 * @author Team CBDDL
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdvertisementVO {
    
    /**
     * 广告ID
     * <p>
     * 广告记录的唯一标识符
     * </p>
     */
    private Long id;
    
    /**
     * 广告标题
     * <p>
     * 广告的标题或名称
     * </p>
     */
    private String title;
    
    /**
     * 广告内容
     * <p>
     * 广告的详细描述或文本内容
     * </p>
     */
    private String content;
    
    /**
     * 广告图片URL
     * <p>
     * 广告的图片链接地址
     * </p>
     */
    private String imgUrl;
    
    /**
     * 商品ID
     * <p>
     * 该广告关联的商品ID，标识广告所属的商品
     * </p>
     */
    private Long productId;
}