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
     * 根据用户查找所有购物车商品
     * <p>
     * 查询指定用户的所有购物车商品。该方法基于Spring Data的命名查询约定，
     * 会自动转换为相应的SQL查询。
     * </p>
     *
     * @param user 要查询的用户
     * @return 用户购物车中的所有商品列表
     */
    List<CartItem> findByUser(User user);
    
    /**
     * 根据用户ID查找所有购物车商品
     * <p>
     * 查询指定用户ID的所有购物车商品。该方法基于Spring Data的命名查询约定，
     * 会自动转换为相应的SQL查询。
     * </p>
     *
     * @param userId 要查询的用户ID
     * @return 用户购物车中的所有商品列表
     */
    List<CartItem> findByUserId(Long userId);
    
    /**
     * 根据用户和商品查找购物车商品
     * <p>
     * 查询指定用户的购物车中是否存在指定商品。该方法基于Spring Data的命名查询约定，
     * 会自动转换为相应的SQL查询。
     * </p>
     *
     * @param user 要查询的用户
     * @param product 要查询的商品
     * @return 封装在Optional中的购物车商品实体；如果不存在则返回空Optional
     */
    Optional<CartItem> findByUserAndProduct(User user, Product product);
    
    /**
     * 根据用户ID和商品ID查找购物车商品
     * <p>
     * 查询指定用户ID的购物车中是否存在指定商品ID。该方法基于Spring Data的命名查询约定，
     * 会自动转换为相应的SQL查询。
     * </p>
     *
     * @param userId 要查询的用户ID
     * @param productId 要查询的商品ID
     * @return 封装在Optional中的购物车商品实体；如果不存在则返回空Optional
     */
    Optional<CartItem> findByUserIdAndProductId(Long userId, Long productId);
    
    /**
     * 删除用户购物车中的指定商品
     * <p>
     * 从指定用户的购物车中删除指定商品。该方法基于Spring Data的命名查询约定，
     * 会自动转换为相应的SQL查询。
     * </p>
     *
     * @param user 用户
     * @param product 要删除的商品
     */
    void deleteByUserAndProduct(User user, Product product);
    
    /**
     * 删除用户购物车中的指定商品
     * <p>
     * 从指定用户ID的购物车中删除指定商品ID。该方法基于Spring Data的命名查询约定，
     * 会自动转换为相应的SQL查询。
     * </p>
     *
     * @param userId 用户ID
     * @param productId 要删除的商品ID
     */
    void deleteByUserIdAndProductId(Long userId, Long productId);
    
    /**
     * 删除用户购物车中的所有商品
     * <p>
     * 清空指定用户的购物车。该方法基于Spring Data的命名查询约定，
     * 会自动转换为相应的SQL查询。
     * </p>
     *
     * @param user 用户
     */
    void deleteByUser(User user);
    
    /**
     * 删除用户购物车中的所有商品
     * <p>
     * 清空指定用户ID的购物车。该方法基于Spring Data的命名查询约定，
     * 会自动转换为相应的SQL查询。
     * </p>
     *
     * @param userId 用户ID
     */
    void deleteByUserId(Long userId);
}