package com.tomato.tomato_mall.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 收货地址视图对象
 * <p>
 * 用于前端展示的收货地址信息数据结构，不包含敏感字段。
 * </p>
 *
 * @author Team CBDDL
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShippingAddressVO {

    /**
     * 地址ID
     */
    private Long id;

    /**
     * 收货人姓名
     */
    private String name;

    /**
     * 收货人电话
     */
    private String phone;

    /**
     * 详细地址
     */
    private String address;

    /**
     * 邮政编码
     */
    private String postalCode;
}