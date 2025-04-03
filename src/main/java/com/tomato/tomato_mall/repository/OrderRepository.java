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
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    /**
     * 根据用户查找所有订单
     */
    List<Order> findByUser(User user);
    
    /**
     * 根据用户ID查找所有订单
     */
    List<Order> findByUserId(Long userId);
    
    /**
     * 根据订单状态查找所有订单
     */
    List<Order> findByStatus(Order.OrderStatus status);
    
    /**
     * 根据用户和订单状态查找订单
     */
    List<Order> findByUserAndStatus(User user, Order.OrderStatus status);
    
    /**
     * 根据用户ID和订单状态查找订单
     */
    List<Order> findByUserIdAndStatus(Long userId, Order.OrderStatus status);
    
    /**
     * 根据第三方交易号查找订单
     */
    Order findByTradeNo(String tradeNo);
    
    /**
     * 查询指定时间之前的待支付订单
     */
    List<Order> findByCreateTimeLessThanAndStatus(LocalDateTime time, Order.OrderStatus status);
}