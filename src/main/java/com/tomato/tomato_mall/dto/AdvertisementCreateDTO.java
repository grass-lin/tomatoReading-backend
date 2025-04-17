package com.tomato.tomato_mall.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 广告创建数据传输对象
 * <p>
 * 该DTO封装了创建广告时所需的全部信息，用于前端向后端传递创建广告请求的数据。
 * 所有必要字段都通过Bean Validation进行数据验证，确保数据的有效性。
 * </p>
 * <p>
 * 该对象通常用于：
 * 1. 接收前端广告创建表单提交的数据
 * 2. 在控制器和服务层之间传递广告创建数据
 * </p>
 *
 * @author Team CBDDL
 * @version 1.0
 */
@Data
public class AdvertisementCreateDTO {
    
    /**
     * 广告标题
     * <p>
     * 广告的标题或名称，必填字段。
     * 长度限制为2到50个字符。
     * </p>
     */
    @NotBlank(message = "广告标题不能为空")
    @Size(min = 2, max = 50, message = "广告标题长度必须在2到50个字符之间")
    private String title;
    
    /**
     * 广告内容
     * <p>
     * 广告的详细描述或文本内容，必填字段。
     * 最大长度为500个字符。
     * </p>
     */
    @NotBlank(message = "广告内容不能为空")
    @Size(max = 500, message = "广告内容不能超过500个字符")
    private String content;
    
    /**
     * 广告图片URL
     * <p>
     * 广告的图片链接地址，必填字段。
     * 最大长度为500个字符。
     * </p>
     */
    @NotBlank(message = "广告图片URL不能为空")
    @Size(max = 500, message = "广告图片URL不能超过500个字符")
    private String imgUrl;
    
    /**
     * 商品ID
     * <p>
     * 广告关联的商品ID，必填字段。
     * 用于将广告与特定商品关联。
     * </p>
     */
    @NotNull(message = "商品ID不能为空")
    private Long productId;
}