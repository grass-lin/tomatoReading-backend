package com.tomato.tomato_mall.repository;

import com.tomato.tomato_mall.entity.Order;
import com.tomato.tomato_mall.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单数据访问仓库
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
    
    /**
     * 根据用户查找所有订单
     */
    List<Order> findByUser(User user);
    
    /**
     * 查询指定时间之前的待支付订单
     */
    List<Order> findByCreateTimeLessThanAndStatus(LocalDateTime time, Order.OrderStatus status);
}