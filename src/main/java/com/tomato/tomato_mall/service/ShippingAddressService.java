package com.tomato.tomato_mall.service;

import com.tomato.tomato_mall.dto.ShippingAddressDTO;
import com.tomato.tomato_mall.dto.ShippingAddressUpdateDTO;
import com.tomato.tomato_mall.vo.ShippingAddressVO;

import java.util.List;

/**
 * 收货地址服务接口
 * <p>
 * 该接口定义了收货地址管理的核心业务功能，包括收货地址的创建、查询、更新和删除等操作。
 * </p>
 *
 * @author Team CBDDL
 * @version 1.0
 */
public interface ShippingAddressService {

    /**
     * 获取用户的所有收货地址
     * 
     * @param username 用户名
     * @return 用户的收货地址列表
     */
    List<ShippingAddressVO> getUserAddresses(String username);

    /**
     * 创建新的收货地址
     * 
     * @param username  用户名
     * @param createDTO 创建收货地址数据传输对象
     * @return 创建的收货地址视图对象
     */
    ShippingAddressVO createAddress(String username, ShippingAddressDTO createDTO);

    /**
     * 更新收货地址
     * 
     * @param username  用户名
     * @param updateDTO 更新收货地址数据传输对象
     * @return 更新后的收货地址视图对象
     */
    ShippingAddressVO updateAddress(String username, ShippingAddressUpdateDTO updateDTO);

    /**
     * 删除收货地址
     * 
     * @param username  用户名
     * @param addressId 收货地址ID
     */
    void deleteAddress(String username, Long addressId);
}