package com.tomato.tomato_mall.service;

import com.tomato.tomato_mall.dto.CartAddDTO;
import com.tomato.tomato_mall.vo.CartItemVO;
import com.tomato.tomato_mall.vo.CartVO;

import java.util.List;

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
    
    /**
     * 将购物车项状态设置为已结算
     * <p>
     * 将指定的购物车项标记为已结算状态，并关联到特定订单。
     * 此操作一般在订单创建成功后调用。
     * </p>
     *
     * @param cartItemIds 要更新的购物车项ID列表
     * @param orderId 关联的订单ID
     * @throws IllegalStateException 当购物车项状态不是激活状态时抛出此异常
     */
    void markCartItemsAsCheckedOut(List<Long> cartItemIds, Long orderId);
    
    /**
     * 恢复已结算的购物车项到活跃状态
     * <p>
     * 将指定订单下的所有已结算购物车项恢复到活跃状态，并移除订单关联。
     * 此操作一般在订单取消或支付失败后调用。
     * </p>
     *
     * @param orderId 订单ID
     */
    void restoreCartItemsByOrderId(Long orderId);
    
    /**
     * 标记订单对应的购物车项为已完成
     * <p>
     * 将指定订单下的所有已结算购物车项标记为已完成状态。
     * 此操作一般在订单完成后调用。
     * </p>
     *
     * @param orderId 订单ID
     */
    void markCartItemsByOrderIdAsCompleted(Long orderId);
    
    /**
     * 标记订单对应的购物车项为已取消
     * <p>
     * 将指定订单下的所有已结算购物车项标记为已取消状态。
     * 此操作一般在订单取消后调用。
     * </p>
     *
     * @param orderId 订单ID
     */
    void markCartItemsByOrderIdAsCancelled(Long orderId);
}