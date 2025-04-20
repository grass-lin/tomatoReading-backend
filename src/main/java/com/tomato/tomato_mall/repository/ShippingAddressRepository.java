package com.tomato.tomato_mall.repository;

import com.tomato.tomato_mall.entity.ShippingAddress;
import com.tomato.tomato_mall.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 收货地址数据访问仓库
 * <p>
 * 该接口负责ShippingAddress实体的数据库访问操作，提供基础的CRUD功能以及根据用户查询地址的方法。
 * </p>
 *
 * @author Team CBDDL
 * @version 1.0
 */
@Repository
public interface ShippingAddressRepository extends JpaRepository<ShippingAddress, Long> {
    
    /**
     * 根据用户查找所有收货地址
     * 
     * @param user 用户实体
     * @return 用户的所有收货地址列表
     */
    List<ShippingAddress> findByUser(User user);
}