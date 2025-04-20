package com.tomato.tomato_mall.repository;

import com.tomato.tomato_mall.entity.Order;
import com.tomato.tomato_mall.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 支付记录数据访问仓库
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    /**
     * 根据订单和支付状态查找支付记录
     */
    List<Payment> findByOrderAndStatus(Order order, Payment.PaymentStatus status);
}