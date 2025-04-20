package com.tomato.tomato_mall.repository;

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
     * 根据商品ID查找所有订单项
     */
    List<OrderItem> findByProduct(Product product);
    
    /**
     * 查询指定商品的待支付订单项是否存在
     */
    boolean existsByProductAndStatus(Product product, OrderItemStatus status);
}