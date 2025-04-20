package com.tomato.tomato_mall.repository;

import com.tomato.tomato_mall.entity.Logistics;
import com.tomato.tomato_mall.entity.OrderItem;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 物流信息数据访问仓库
 * <p>
 * 该接口负责Logistics实体的数据库访问操作，提供了基础的CRUD功能以及物流相关的查询方法。
 * </p>
 * 
 * @author Team CBDDL
 * @version 1.0
 */
@Repository
public interface LogisticsRepository extends JpaRepository<Logistics, Long> {
    /**
     * 通过订单项检查物流信息是否存在
     * 
     * @param orderItem
     */
    boolean existsByOrderItem(OrderItem orderItem);

    /**
     * 通过订单项ID查找物流信息
     * 
     * @param orderItemId 订单项ID
     * @return 可选的物流信息
     */
    Optional<Logistics> findByOrderItemId(Long orderItemId);
}