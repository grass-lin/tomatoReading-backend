package com.tomato.tomato_mall.service.impl;

import com.tomato.tomato_mall.dto.ShippingAddressDTO;
import com.tomato.tomato_mall.dto.ShippingAddressUpdateDTO;
import com.tomato.tomato_mall.entity.ShippingAddress;
import com.tomato.tomato_mall.entity.User;
import com.tomato.tomato_mall.repository.ShippingAddressRepository;
import com.tomato.tomato_mall.repository.UserRepository;
import com.tomato.tomato_mall.service.ShippingAddressService;
import com.tomato.tomato_mall.vo.ShippingAddressVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * 收货地址服务实现类
 * <p>
 * 该类实现了{@link ShippingAddressService}接口，提供收货地址的创建、查询、更新和删除等核心功能。
 * </p>
 * 
 * @author Team CBDDL
 * @version 1.0
 */
@Service
public class ShippingAddressServiceImpl implements ShippingAddressService {

    private final ShippingAddressRepository shippingAddressRepository;
    private final UserRepository userRepository;

    /**
     * 构造函数，通过依赖注入初始化服务组件
     * 
     * @param shippingAddressRepository 收货地址数据访问对象
     * @param userRepository            用户数据访问对象
     */
    public ShippingAddressServiceImpl(
            ShippingAddressRepository shippingAddressRepository,
            UserRepository userRepository) {
        this.shippingAddressRepository = shippingAddressRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<ShippingAddressVO> getUserAddresses(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("用户不存在"));

        List<ShippingAddress> addresses = shippingAddressRepository.findByUser(user);

        return addresses.stream()
                .map(this::convertToShippingAddressVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ShippingAddressVO createAddress(String username, ShippingAddressDTO createDTO) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("用户不存在"));

        ShippingAddress address = new ShippingAddress();
        BeanUtils.copyProperties(createDTO, address);
        address.setUser(user);

        ShippingAddress savedAddress = shippingAddressRepository.save(address);

        return convertToShippingAddressVO(savedAddress);
    }

    @Override
    @Transactional
    public ShippingAddressVO updateAddress(String username, ShippingAddressUpdateDTO updateDTO) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("用户不存在"));

        ShippingAddress address = shippingAddressRepository.findById(updateDTO.getId())
                .orElseThrow(() -> new NoSuchElementException("收货信息不存在"));

        if (!address.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("无权修改该收货信息");
        }

        // 有选择地更新地址字段
        if (updateDTO.getName() != null) {
            address.setName(updateDTO.getName());
        }

        if (updateDTO.getPhone() != null) {
            address.setPhone(updateDTO.getPhone());
        }

        if (updateDTO.getAddress() != null) {
            address.setAddress(updateDTO.getAddress());
        }

        if (updateDTO.getPostalCode() != null) {
            address.setPostalCode(updateDTO.getPostalCode());
        }

        ShippingAddress updatedAddress = shippingAddressRepository.save(address);

        return convertToShippingAddressVO(updatedAddress);
    }

    @Override
    @Transactional
    public void deleteAddress(String username, Long addressId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("用户不存在"));

        ShippingAddress address = shippingAddressRepository.findById(addressId)
                .orElseThrow(() -> new NoSuchElementException("收货信息不存在"));

        if (!address.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("无权删除该收货地址");
        }

        shippingAddressRepository.delete(address);
    }

    /**
     * 将收货地址实体转换为视图对象
     * 
     * @param address 收货地址实体
     * @return 收货地址视图对象
     */
    private ShippingAddressVO convertToShippingAddressVO(ShippingAddress address) {
        ShippingAddressVO vo = new ShippingAddressVO();
        BeanUtils.copyProperties(address, vo);
        return vo;
    }
}