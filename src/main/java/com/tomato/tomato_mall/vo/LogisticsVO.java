package com.tomato.tomato_mall.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 物流信息视图对象
 * <p>
 * 该类用于向前端返回物流信息，包括物流公司、物流单号、发货时间等。
 * 作为视图对象，它封装了前端所需的物流数据，隐藏了内部实现细节。
 * </p>
 * 
 * @author Team CBDDL
 * @version 1.0
 */
@Data
@Builder
public class LogisticsVO {
    
    /**
     * 物流信息ID
     */
    private Long id;
    
    /**
     * 订单项ID
     */
    private Long orderItemId;
    
    /**
     * 物流公司编码
     */
    private String companyCode;
    
    /**
     * 物流公司名称
     */
    private String companyName;
    
    /**
     * 物流单号
     */
    private String trackingNumber;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}