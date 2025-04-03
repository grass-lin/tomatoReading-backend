package com.tomato.tomato_mall.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 收货地址数据传输对象
 * <p>
 * 该DTO封装了收货地址的详细信息，包括收货人姓名、联系电话、详细地址和邮政编码等。
 * 作为CheckoutDTO的一部分，用于在订单创建过程中传递收货信息。
 * </p>
 *
 * @author Team Tomato
 * @version 1.0
 */
@Data
public class ShippingAddressDTO {

    /**
     * 收货人姓名
     * <p>
     * 收件人的真实姓名
     * </p>
     */
    @NotBlank(message = "Recipient name must not be empty")
    private String name;

    /**
     * 联系电话
     * <p>
     * 收件人的联系电话，用于配送时联系
     * </p>
     */
    @NotBlank(message = "Phone number must not be empty")
    @Pattern(regexp = "^1\\d{10}$", message = "Phone number must be 11 digits and start with 1")
    private String phone;

    /**
     * 详细地址
     * <p>
     * 收件人的详细地址，包括省市区和街道门牌号
     * </p>
     */
    @NotBlank(message = "Address must not be empty")
    private String address;

    /**
     * 邮政编码
     * <p>
     * 收件地址的邮政编码
     * </p>
     */
    @Pattern(regexp = "^\\d{6}$", message = "Postal code must be 6 digits")
    private String postalCode;
}