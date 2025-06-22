package com.tomato.tomato_mall.repository;

import com.tomato.tomato_mall.entity.CartItem;
import com.tomato.tomato_mall.entity.User;
import com.tomato.tomato_mall.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 购物车数据访问仓库
 * <p>
 * 该接口负责CartItem实体的数据库访问操作，提供了基础的CRUD功能以及购物车相关的查询方法。
 * 通过继承JpaRepository，自动获得了丰富的数据操作能力，如分页查询、排序等。
 * </p>
 * <p>
 * 作为数据访问层的核心组件，CartRepository连接了业务层与数据库，所有与购物车数据
 * 相关的持久化操作都通过此接口进行。
 * </p>
 *
 * @author Team CBDDL
 * @version 1.0
 */
@Repository
public interface CartRepository extends JpaRepository<CartItem, Long> {
    /**
     * 根据用户和状态查找购物车商品
     * <p>
     * 查询指定用户的购物车中所有指定状态的商品。该方法基于Spring Data的命名查询约定，
     * 会自动转换为相应的SQL查询。
     * </p>
     *
     * @param user   要查询的用户
     * @param status 要查询的状态
     * @return 用户购物车中指定状态的商品列表
     */
    List<CartItem> findByUserAndStatus(User user, CartItem.CartItemStatus status);

    /**
     * 根据用户、商品和状态查找购物车商品
     * <p>
     * 查询指定用户的购物车中是否存在指定商品和状态。该方法基于Spring Data的命名查询约定，
     * 会自动转换为相应的SQL查询。
     * </p>
     *
     * @param user    要查询的用户
     * @param product 要查询的商品
     * @param status  要查询的状态
     * @return 封装在Optional中的购物车商品实体；如果不存在则返回空Optional
     */
    Optional<CartItem> findByUserAndProductAndStatus(User user, Product product, CartItem.CartItemStatus status);

    /**
     * 根据商品ID查找删除购物车项
     * 
     * @param productId
     */
    void deleteByProductId(Long productId);
}