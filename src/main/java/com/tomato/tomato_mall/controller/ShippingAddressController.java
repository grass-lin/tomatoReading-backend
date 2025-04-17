package com.tomato.tomato_mall.controller;

import com.tomato.tomato_mall.dto.ShippingAddressDTO;
import com.tomato.tomato_mall.dto.ShippingAddressUpdateDTO;
import com.tomato.tomato_mall.service.ShippingAddressService;
import com.tomato.tomato_mall.vo.ResponseVO;
import com.tomato.tomato_mall.vo.ShippingAddressVO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 收货地址控制器
 * <p>
 * 提供收货地址的增删改查REST API接口
 * 所有接口返回统一的ResponseVO格式，包含状态码、消息和数据
 * </p>
 *
 * @author Team CBDDL
 * @version 1.0
 */
@RestController
@RequestMapping("/api/address")
public class ShippingAddressController {

    private final ShippingAddressService shippingAddressService;

    /**
     * 构造函数，通过依赖注入初始化服务
     *
     * @param shippingAddressService 收货地址服务，处理收货地址相关业务逻辑
     */
    public ShippingAddressController(ShippingAddressService shippingAddressService) {
        this.shippingAddressService = shippingAddressService;
    }

    /**
     * 获取用户所有收货地址接口
     * <p>
     * 返回当前认证用户的所有收货地址列表
     * </p>
     *
     * @return 返回包含收货地址列表的响应体，状态码200
     */
    @GetMapping
    public ResponseEntity<ResponseVO<List<ShippingAddressVO>>> getUserAddresses() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<ShippingAddressVO> addresses = shippingAddressService.getUserAddresses(username);
        return ResponseEntity.ok(ResponseVO.success(addresses));
    }

    /**
     * 创建收货地址接口
     * <p>
     * 为当前用户创建新的收货地址
     * </p>
     *
     * @param createDTO 收货地址创建数据传输对象
     * @return 返回包含创建的收货地址信息的响应体，状态码200
     */
    @PostMapping
    public ResponseEntity<ResponseVO<ShippingAddressVO>> createAddress(
            @Valid @RequestBody ShippingAddressDTO createDTO) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        ShippingAddressVO createdAddress = shippingAddressService.createAddress(username, createDTO);
        return ResponseEntity.ok(ResponseVO.success(createdAddress));
    }

    /**
     * 更新收货地址接口
     * <p>
     * 更新用户的指定收货地址
     * </p>
     *
     * @param updateDTO 收货地址更新数据传输对象
     * @return 返回包含更新后的收货地址信息的响应体，状态码200
     */
    @PutMapping
    public ResponseEntity<ResponseVO<ShippingAddressVO>> updateAddress(
            @Valid @RequestBody ShippingAddressUpdateDTO updateDTO) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        ShippingAddressVO updatedAddress = shippingAddressService.updateAddress(username, updateDTO);
        return ResponseEntity.ok(ResponseVO.success(updatedAddress));
    }

    /**
     * 删除收货地址接口
     * <p>
     * 删除用户的指定收货地址
     * </p>
     *
     * @param addressId 收货地址ID
     * @return 返回操作成功的响应体，状态码200
     */
    @DeleteMapping("/{addressId}")
    public ResponseEntity<ResponseVO<String>> deleteAddress(@PathVariable Long addressId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        shippingAddressService.deleteAddress(username, addressId);
        return ResponseEntity.ok(ResponseVO.success("收货信息删除成功"));
    }
}