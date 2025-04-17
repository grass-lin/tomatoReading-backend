package com.tomato.tomato_mall.repository;

import com.tomato.tomato_mall.entity.Advertisement;
import com.tomato.tomato_mall.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 广告数据访问仓库
 * <p>
 * 该接口负责Advertisement实体的数据库访问操作，提供了基础的CRUD功能以及广告相关的查询方法。
 * 通过继承JpaRepository，自动获得了丰富的数据操作能力，如分页查询、排序等。
 * </p>
 * <p>
 * 作为数据访问层的核心组件，AdvertisementRepository连接了业务层与数据库，所有与广告数据
 * 相关的持久化操作都通过此接口进行。
 * </p>
 *
 * @author Team CBDDL
 * @version 1.0
 */
@Repository
public interface AdvertisementRepository extends JpaRepository<Advertisement, Long> {
    /**
     * 根据商品查询所有关联的广告
     * <p>
     * 用于获取特定商品关联的所有广告信息，通常用于商品详情页展示相关广告。
     * </p>
     *
     * @param product 要查询广告的商品实体
     * @return 与该商品关联的广告列表
     */
    List<Advertisement> findByProduct(Product product);

    /**
     * 根据标题模糊查询广告
     * <p>
     * 用于通过关键词搜索广告，支持模糊匹配广告标题。
     * </p>
     *
     * @param keyword 要搜索的标题关键词
     * @return 匹配关键词的广告列表
     */
    List<Advertisement> findByTitleContaining(String keyword);

    /**
     * 检查指定商品的广告数量
     * <p>
     * 用于统计特定商品关联的广告数量，可用于限制每个商品的最大广告数。
     * </p>
     *
     * @param product 要查询的商品实体
     * @return 该商品关联的广告数量
     */
    long countByProduct(Product product);
}