package com.tomato.tomato_mall.repository;

import com.tomato.tomato_mall.entity.Order;
import com.tomato.tomato_mall.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 支付记录数据访问仓库
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    /**
     * 根据订单查找所有支付记录
     */
    List<Payment> findByOrder(Order order);
    
    /**
     * 根据订单ID查找所有支付记录
     */
    List<Payment> findByOrderId(Long orderId);
    
    /**
     * 根据支付状态查找所有支付记录
     */
    List<Payment> findByStatus(Payment.PaymentStatus status);
    
    /**
     * 根据订单和支付状态查找支付记录
     */
    List<Payment> findByOrderAndStatus(Order order, Payment.PaymentStatus status);
    
    /**
     * 根据第三方交易号查找支付记录
     */
    Optional<Payment> findByTradeNo(String tradeNo);
    
    /**
     * 根据订单和支付方式查找最新的支付记录
     */
    List<Payment> findByOrderAndPaymentMethodOrderByCreateTimeDesc(Order order, String paymentMethod);
}