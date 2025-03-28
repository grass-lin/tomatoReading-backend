package com.tomato.tomato_mall.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 商品规格数据传输对象
 * <p>
 * 该DTO封装了商品规格的信息，用于前端向后端传递创建或更新商品规格的数据。
 * 包含规格项名称和规格值两个核心字段，通过Bean Validation确保数据的有效性。
 * </p>
 * <p>
 * 该对象通常用于：
 * 1. 作为商品创建或更新请求的一部分
 * 2. 在商品编辑表单中提交规格信息
 * 3. 在控制器和服务层之间传递规格数据
 * </p>
 * <p>
 * 所有字段都是必填的，确保规格信息的完整性。
 * </p>
 *
 * @author Team Tomato
 * @version 1.0
 * @see com.tomato.tomato_mall.dto.ProductCreateDTO
 * @see com.tomato.tomato_mall.dto.ProductUpdateDTO
 */
@Data
public class SpecificationDTO {
    /**
     * 规格项名称
     * <p>
     * 表示规格的类别，如"颜色"、"尺寸"、"材质"等。
     * 必须非空，且长度不超过50个字符。
     * </p>
     */
    @NotBlank(message = "Specification name cannot be empty")
    @Size(max = 50, message = "Specification name cannot exceed 50 characters")
    private String item;

    /**
     * 规格值
     * <p>
     * 表示规格项的具体值，如"红色"、"XL"、"棉"等。
     * 必须非空，且长度不超过255个字符。
     * </p>
     */
    @NotBlank(message = "Specification content cannot be empty")
    @Size(max = 255, message = "Specification content cannot exceed 255 characters")
    private String value;
}