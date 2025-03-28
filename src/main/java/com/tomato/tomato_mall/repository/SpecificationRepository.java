package com.tomato.tomato_mall.repository;

import com.tomato.tomato_mall.entity.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 商品规格数据访问仓库
 * <p>
 * 该接口负责Specification实体的数据库访问操作，提供了基础的CRUD功能以及规格相关的查询方法。
 * 通过继承JpaRepository，自动获得了丰富的数据操作能力。
 * </p>
 * <p>
 * 作为数据访问层的组件，SpecificationRepository主要处理与商品规格相关的数据持久化操作。
 * </p>
 *
 * @author Team CBDDL
 * @version 1.0
 */
@Repository
public interface SpecificationRepository extends JpaRepository<Specification, Long> {
    /**
     * 根据商品ID查找所有规格
     * <p>
     * 查询指定商品的所有规格信息。该方法基于Spring Data的命名查询约定，
     * 会自动转换为相应的SQL查询。
     * </p>
     *
     * @param productId 要查询规格的商品ID
     * @return 该商品的所有规格列表
     */
    List<Specification> findByProductId(Long productId);
    
    /**
     * 根据商品ID删除所有规格
     * <p>
     * 删除指定商品的所有规格信息。该方法通常用于商品更新时，先删除旧的规格，然后添加新的规格。
     * </p>
     *
     * @param productId 要删除规格的商品ID
     */
    void deleteByProductId(Long productId);
}