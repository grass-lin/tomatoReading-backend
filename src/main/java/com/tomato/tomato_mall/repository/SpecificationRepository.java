package com.tomato.tomato_mall.repository;

import com.tomato.tomato_mall.entity.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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
}