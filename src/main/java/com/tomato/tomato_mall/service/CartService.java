package com.tomato.tomato_mall.service;

import com.tomato.tomato_mall.dto.CartAddDTO;
import com.tomato.tomato_mall.vo.CartItemVO;
import com.tomato.tomato_mall.vo.CartVO;

/**
 * 购物车服务接口
 * <p>
 * 该接口定义了购物车管理的核心业务功能，包括购物车商品的添加、查询、更新和删除等操作。
 * 作为系统购物车管理的核心组件，提供了购物车全生命周期的管理功能。
 * </p>
 *
 * @author Team CBDDL
 * @version 1.0
 */
public interface CartService {
    /**
     * 添加商品到购物车
     * <p>
     * 将指定商品添加到用户的购物车中。如果购物车中已存在该商品，则增加数量。
     * 添加前会验证商品库存是否充足，确保不会超出库存数量。
     * </p>
     *
     * @param username 用户名
     * @param cartAddDTO 购物车添加数据传输对象，包含商品ID和数量
     * @return 添加后的购物车商品视图对象
     * @throws IllegalArgumentException 当商品库存不足时抛出此异常
     * @throws java.util.NoSuchElementException 当用户或商品不存在时抛出此异常
     */
    CartItemVO addToCart(String username, CartAddDTO cartAddDTO);
    
    /**
     * 删除购物车商品
     * <p>
     * 从用户的购物车中删除指定的商品。删除前会验证购物车商品是否属于当前用户。
     * </p>
     *
     * @param username 用户名
     * @param cartItemId 购物车商品ID
     * @throws IllegalArgumentException 当购物车商品不属于该用户时抛出此异常
     * @throws java.util.NoSuchElementException 当购物车商品不存在时抛出此异常
     */
    void removeFromCart(String username, Long cartItemId);
    
    /**
     * 更新购物车商品数量
     * <p>
     * 修改用户购物车中指定商品的数量。更新前会验证购物车商品是否属于当前用户，
     * 并且会检查新的数量是否超出库存限制。
     * </p>
     *
     * @param username 用户名
     * @param cartItemId 购物车商品ID
     * @param quantity 新的商品数量
     * @return 更新后的购物车商品视图对象
     * @throws IllegalArgumentException 当商品库存不足或购物车商品不属于该用户时抛出此异常
     * @throws java.util.NoSuchElementException 当购物车商品不存在时抛出此异常
     */
    CartItemVO updateCartItemQuantity(String username, Long cartItemId, Integer quantity);
    
    /**
     * 获取用户的购物车商品列表
     * <p>
     * 查询指定用户的所有购物车商品，返回包含详细信息的视图对象列表。
     * </p>
     *
     * @param username 用户名
     * @return 购物车商品视图对象列表
     * @throws java.util.NoSuchElementException 当用户不存在时抛出此异常
     */
    CartVO getCartItems(String username);
}