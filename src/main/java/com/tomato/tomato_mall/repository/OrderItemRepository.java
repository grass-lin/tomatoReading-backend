package com.tomato.tomato_mall.repository;

import com.tomato.tomato_mall.entity.Order;
import com.tomato.tomato_mall.entity.OrderItem;
import com.tomato.tomato_mall.entity.OrderItem.OrderItemStatus;
import com.tomato.tomato_mall.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

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
     * 根据订单ID查找所有订单项
     */
    List<OrderItem> findByOrderId(Long orderId);
    
    /**
     * 根据商品查找所有订单项
     */
    List<OrderItem> findByProduct(Product product);
    
    /**
     * 根据商品ID查找所有订单项
     */
    List<OrderItem> findByProductId(Long productId);
    
    /**
     * 根据订单删除所有订单项
     */
    void deleteByOrder(Order order);
    
    /**
     * 根据订单ID删除所有订单项
     */
    void deleteByOrderId(Long orderId);

    /**
     * 查询指定商品的待支付订单项是否存在
     */
    boolean existsByProductIdAndStatus(Long productId, OrderItemStatus status);
}