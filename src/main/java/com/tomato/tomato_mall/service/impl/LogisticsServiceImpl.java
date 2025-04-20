package com.tomato.tomato_mall.service.impl;

import com.tomato.tomato_mall.dto.LogisticsCreateDTO;
import com.tomato.tomato_mall.entity.Logistics;
import com.tomato.tomato_mall.entity.OrderItem;
import com.tomato.tomato_mall.enums.ErrorTypeEnum;
import com.tomato.tomato_mall.exception.BusinessException;
import com.tomato.tomato_mall.repository.LogisticsRepository;
import com.tomato.tomato_mall.repository.OrderItemRepository;
import com.tomato.tomato_mall.service.LogisticsService;
import com.tomato.tomato_mall.vo.LogisticsVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 物流服务实现类
 * <p>
 * 该类实现了{@link LogisticsService}接口，提供物流信息的创建、查询等核心功能。
 * 包含物流信息处理和状态更新等业务逻辑的具体实现。
 * </p>
 * 
 * @author Team CBDDL
 * @version 1.0
 */
@Service
public class LogisticsServiceImpl implements LogisticsService {

    private final LogisticsRepository logisticsRepository;
    private final OrderItemRepository orderItemRepository;

    /**
     * 构造函数，通过依赖注入初始化物流服务组件
     */
    public LogisticsServiceImpl(
            LogisticsRepository logisticsRepository,
            OrderItemRepository orderItemRepository) {
        this.logisticsRepository = logisticsRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @Override
    @Transactional
    public List<LogisticsVO> createLogistics(LogisticsCreateDTO createDTO) {

        // 获取订单项列表
        List<OrderItem> orderItems = orderItemRepository.findAllById(createDTO.getOrderItemIds());
        List<Logistics> logisticsList = orderItems.stream()
                .map(orderItem -> {
                    // 验证订单项状态
                    if (!orderItem.getStatus().equals(OrderItem.OrderItemStatus.PAID)) {
                        throw new BusinessException(ErrorTypeEnum.ORDER_ITEM_STATUS_ERROR);
                    }
                    // 检查是否已存在物流信息
                    if (logisticsRepository.existsByOrderItem(orderItem)) {
                        throw new BusinessException(ErrorTypeEnum.LOGISTICS_ALREADY_EXISTS);
                    }
                    // 更新订单项状态
                    orderItem.setStatus(OrderItem.OrderItemStatus.SHIPPED);
                    // 创建物流信息
                    Logistics logistics = new Logistics();
                    logistics.setOrderItem(orderItem);
                    logistics.setCompany(createDTO.getCompany());
                    logistics.setTrackingNumber(createDTO.getTrackingNumber());
                    return logistics;
                })
                .collect(Collectors.toList());
        List<Logistics> savedLogisticsList = logisticsRepository.saveAll(logisticsList);
        orderItemRepository.saveAll(orderItems);
        return savedLogisticsList.stream()
                .map(this::convertToLogisticsVO)
                .collect(Collectors.toList());
    }

    @Override
    public LogisticsVO getLogisticsByOrderItemId(Long orderItemId) {
        Logistics logistics = logisticsRepository.findByOrderItemId(orderItemId)
                .orElseThrow(() -> new BusinessException(ErrorTypeEnum.LOGISTICS_NOT_FOUND));
        return convertToLogisticsVO(logistics);
    }

    /**
     * 将物流实体转换为视图对象
     */
    private LogisticsVO convertToLogisticsVO(Logistics logistics) {
        return LogisticsVO.builder()
                .id(logistics.getId())
                .orderItemId(logistics.getOrderItem().getId())
                .companyCode(logistics.getCompany().name())
                .companyName(logistics.getCompany().getDisplayName())
                .trackingNumber(logistics.getTrackingNumber())
                .createTime(logistics.getCreateTime())
                .build();
    }
}