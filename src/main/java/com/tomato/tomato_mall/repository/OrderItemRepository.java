package com.tomato.tomato_mall.repository;

import com.tomato.tomato_mall.entity.Order;
import com.tomato.tomato_mall.entity.OrderItem;
import com.tomato.tomato_mall.entity.OrderItem.OrderItemStatus;
import com.tomato.tomato_mall.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 订单项数据访问仓库
 */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    
    /**
     * 根据订单查找所有订单项
     */
    List<OrderItem> findByOrder(Order order);
    
    /**
     * 根据商品ID查找所有订单项
     */
    List<OrderItem> findByProductId(Long productId);
    
    /**
     * 查询指定商品的待支付订单项是否存在
     */
    boolean existsByProductIdAndStatus(Long productId, OrderItemStatus status);

    /**
     * 根据购物车项查找订单项
     * 
     * @param cartItem 购物车项
     * @return 关联的订单项
     */
    Optional<OrderItem> findByCartItem(CartItem cartItem);
}