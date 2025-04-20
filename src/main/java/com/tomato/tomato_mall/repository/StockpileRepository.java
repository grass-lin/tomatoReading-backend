package com.tomato.tomato_mall.repository;

import com.tomato.tomato_mall.entity.Stockpile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 商品库存数据访问仓库
 * <p>
 * 该接口负责Stockpile实体的数据库访问操作，提供了基础的CRUD功能以及库存相关的查询方法。
 * 通过继承JpaRepository，自动获得了丰富的数据操作能力。
 * </p>
 * <p>
 * 作为数据访问层的组件，StockpileRepository主要处理与商品库存相关的数据持久化操作。
 * </p>
 *
 * @author Team CBDDL
 * @version 1.0
 */
@Repository
public interface StockpileRepository extends JpaRepository<Stockpile, Long> {
    /**
     * 根据商品ID查找库存
     * <p>
     * 查询指定商品的库存信息。该方法基于Spring Data的命名查询约定，
     * 会自动转换为相应的SQL查询。
     * </p>
     *
     * @param productId 要查询库存的商品ID
     * @return 封装在Optional中的库存实体；如果库存不存在则返回空Optional
     */
    Optional<Stockpile> findByProductId(Long productId);
}