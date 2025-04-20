package com.tomato.tomato_mall.dto;

import java.util.List;

import com.tomato.tomato_mall.entity.Logistics.LogisticsCompany;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 物流信息创建数据传输对象
 * <p>
 * 该DTO封装了创建物流信息时所需的数据，用于前端向后端传递物流信息。
 * 通过Bean Validation确保数据的有效性。
 * </p>
 * 
 * @author Team CBDDL
 * @version 1.0
 */
@Data
public class LogisticsCreateDTO {
    
    /**
     * 关联的订单项ID列表
     */
    @NotEmpty(message = "Order Item Ids cannot be empty")
    private List<Long> orderItemIds;
    
    /**
     * 物流公司编码
     */
    @NotNull(message = "Logistics Company cannot be null")
    private LogisticsCompany company;
    
    /**
     * 物流单号
     */
    @NotBlank(message = "Tracking Number cannot be blank")
    private String trackingNumber;
}