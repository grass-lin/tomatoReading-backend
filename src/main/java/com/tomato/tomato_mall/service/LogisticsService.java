package com.tomato.tomato_mall.service;

import java.util.List;

import com.tomato.tomato_mall.dto.LogisticsCreateDTO;
import com.tomato.tomato_mall.vo.LogisticsVO;

/**
 * 物流服务接口
 * <p>
 * 该接口定义了物流信息管理的核心业务功能，包括物流信息的创建、查询等操作。
 * 作为系统物流管理的核心组件，提供了物流信息全生命周期的管理功能。
 * </p>
 * 
 * @author Team CBDDL
 * @version 1.0
 */
public interface LogisticsService {
    
    /**
     * 创建物流信息
     * <p>
     * 根据提供的数据创建新的物流信息，同时更新关联订单项的状态为已发货。
     * </p>
     * 
     * @param createDTO 物流信息创建数据
     * @return 创建成功的物流信息视图对象列表
     */
    List<LogisticsVO> createLogistics(LogisticsCreateDTO createDTO);
    
    /**
     * 查询物流信息
     * <p>
     * 通过订单项ID查询对应的物流信息
     * </p>
     * 
     * @param orderItemId 订单项ID
     * @return 物流信息视图对象
     */
    LogisticsVO getLogisticsByOrderItemId(Long orderItemId);
}