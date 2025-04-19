package com.tomato.tomato_mall.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 广告更新数据传输对象
 * <p>
 * 该DTO封装了更新广告时所需的信息，用于前端向后端传递更新广告请求的数据。
 * 设计遵循部分更新原则，除ID外的其他字段均为可选，仅更新提供的非空字段。
 * 所有字段都通过Bean Validation进行数据验证，确保数据的有效性。
 * </p>
 * <p>
 * 该对象通常用于：
 * 1. 接收前端广告编辑表单提交的数据
 * 2. 在控制器和服务层之间传递广告更新数据
 * </p>
 *
 * @author Team CBDDL
 * @version 1.0
 */
@Data
public class AdvertisementUpdateDTO {
    
    /**
     * 广告ID
     * <p>
     * 要更新的广告ID，必填字段。
     * 用于标识待更新的广告记录。
     * </p>
     */
    @NotNull(message = "广告ID不能为空")
    private Long id;
    
    /**
     * 广告标题
     * <p>
     * 广告的标题或名称，可选字段。
     * 如果提供，长度限制为2到50个字符。
     * </p>
     */
    @Size(min = 2, max = 50, message = "广告标题长度必须在2到50个字符之间")
    private String title;
    
    /**
     * 广告内容
     * <p>
     * 广告的详细描述或文本内容，可选字段。
     * 如果提供，最大长度为500个字符。
     * </p>
     */
    @Size(max = 500, message = "广告内容不能超过500个字符")
    private String content;
    
    /**
     * 广告图片URL
     * <p>
     * 广告的图片链接地址，可选字段。
     * 如果提供，最大长度为500个字符。
     * </p>
     */
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