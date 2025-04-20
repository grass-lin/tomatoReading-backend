package com.tomato.tomato_mall.repository;

import com.tomato.tomato_mall.entity.Product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 商品数据访问仓库
 * <p>
 * 该接口负责Product实体的数据库访问操作，提供了基础的CRUD功能以及商品相关的查询方法。
 * 通过继承JpaRepository，自动获得了丰富的数据操作能力，如分页查询、排序等。
 * </p>
 * <p>
 * 作为数据访问层的核心组件，ProductRepository连接了业务层与数据库，所有与商品数据
 * 相关的持久化操作都通过此接口进行。
 * </p>
 *
 * @author Team CBDDL
 * @version 1.0
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}