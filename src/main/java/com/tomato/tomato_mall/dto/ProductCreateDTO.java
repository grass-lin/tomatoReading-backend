package com.tomato.tomato_mall.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 商品创建数据传输对象
 * <p>
 * 该DTO封装了创建商品时所需的全部信息，用于前端向后端传递创建商品请求的数据。
 * 包含商品基本信息、规格信息和初始库存信息。
 * 所有字段都通过Bean Validation进行数据验证，确保数据的有效性。
 * </p>
 * <p>
 * 该对象通常用于：
 * 1. 接收前端商品创建表单提交的完整商品信息
 * 2. 在控制器和服务层之间传递商品创建数据
 * 3. 作为商品创建过程的输入参数
 * </p>
 *
 * @author Team CBDDL
 * @version 1.0
 */
@Data
public class ProductCreateDTO {
    /**
     * 商品标题
     * <p>
     * 商品的名称或标题，必填字段。
     * 长度限制为2到50个字符。
     * </p>
     */
    @NotBlank(message = "Product title cannot be blank")
    @Size(min = 2, max = 50, message = "Product title must be between 2 and 50 characters")
    private String title;

    /**
     * 商品价格
     * <p>
     * 商品的销售价格，必填字段。
     * 必须大于等于0。
     * </p>
     */
    @NotNull(message = "Product price cannot be null")
    @DecimalMin(value = "0.00", message = "Product price must be greater than or equal to 0")
    private BigDecimal price;

    /**
     * 商品评分
     * <p>
     * 商品的初始评分，必填字段。
     * 有效范围为0到10。
     * </p>
     */
    @NotNull(message = "Product rate cannot be null")
    @Min(value = 0, message = "Product rate cannot be less than 0")
    @Max(value = 10, message = "Product rate cannot be greater than 10")
    private Double rate;

    /**
     * 商品描述
     * <p>
     * 商品的简要描述，可选字段。
     * 最大长度为255个字符。
     * </p>
     */
    @Size(max = 255, message = "Product description cannot exceed 255 characters")
    private String description;

    /**
     * 商品封面图片URL
     * <p>
     * 商品的封面图片链接地址，可选字段。
     * 最大长度为500个字符。
     * </p>
     */
    @Size(max = 500, message = "Product cover image URL cannot exceed 500 characters")
    private String cover;

    /**
     * 商品详细说明
     * <p>
     * 商品的详细介绍，可选字段。
     * 最大长度为500个字符。
     * </p>
     */
    @Size(max = 500, message = "Product detail cannot exceed 500 characters")
    private String detail;

    /**
     * 商品规格列表
     * <p>
     * 商品的规格信息列表，每个规格包含名称和值。
     * 可选字段，但列表中的每个元素都需要验证。
     * </p>
     */
    @Valid
    private List<SpecificationDTO> specifications;
}