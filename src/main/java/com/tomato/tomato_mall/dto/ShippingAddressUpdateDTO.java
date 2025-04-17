package com.tomato.tomato_mall.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 更新收货地址数据传输对象
 * <p>
 * 该DTO封装了更新收货地址所需的数据。
 * </p>
 *
 * @author Team CBDDL
 * @version 1.0
 */
@Data
public class ShippingAddressUpdateDTO {
    
    /**
     * 地址ID
     */
    @NotNull(message = "Address ID cannot be null")
    private Long id;
    
    /**
     * 收货人姓名
     */
    private String name;
    
    /**
     * 收货人电话
     */
    @Pattern(regexp = "^1\\d{10}$", message = "Phone number must be 11 digits")
    private String phone;
    
    /**
     * 详细地址
     */
    private String address;
    
    /**
     * 邮政编码
     */
    @Pattern(regexp = "^\\d{6}$", message = "Postal code must be 6 digits")
    private String postalCode;
}