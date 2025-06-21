package com.tomato.tomato_mall.service.impl;

import com.tomato.tomato_mall.dto.LogisticsCreateDTO;
import com.tomato.tomato_mall.entity.Logistics;
import com.tomato.tomato_mall.entity.Logistics.LogisticsCompany;
import com.tomato.tomato_mall.entity.OrderItem;
import com.tomato.tomato_mall.entity.OrderItem.OrderItemStatus;
import com.tomato.tomato_mall.enums.ErrorTypeEnum;
import com.tomato.tomato_mall.exception.BusinessException;
import com.tomato.tomato_mall.repository.LogisticsRepository;
import com.tomato.tomato_mall.repository.OrderItemRepository;
import com.tomato.tomato_mall.vo.LogisticsVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LogisticsServiceImplTest {

    @Mock
    private LogisticsRepository logisticsRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @InjectMocks
    private LogisticsServiceImpl logisticsService;

    private OrderItem orderItem;
    private Logistics logistics;
    private LogisticsCreateDTO createDTO;

    @BeforeEach
    void setUp() {
        // 准备测试数据
        orderItem = new OrderItem();
        orderItem.setId(1L);
        orderItem.setStatus(OrderItemStatus.PAID);

        logistics = new Logistics();
        logistics.setId(1L);
        logistics.setOrderItem(orderItem);
        logistics.setCompany(LogisticsCompany.SF);
        logistics.setTrackingNumber("SF123456789");
        logistics.setCreateTime(LocalDateTime.now());

        createDTO = new LogisticsCreateDTO();
        createDTO.setOrderItemIds(Arrays.asList(1L));
        createDTO.setCompany(LogisticsCompany.SF);
        createDTO.setTrackingNumber("SF123456789");
    }

    // --- createLogistics 方法测试 ---
    @Test
    void createLogistics_Success() {
        // --- Arrange ---
        when(orderItemRepository.findAllById(createDTO.getOrderItemIds())).thenReturn(Arrays.asList(orderItem));
        when(logisticsRepository.existsByOrderItem(orderItem)).thenReturn(false);
        when(logisticsRepository.saveAll(anyList())).thenAnswer(invocation -> {
            List<Logistics> logisticsList = invocation.getArgument(0);
            for (int i = 0; i < logisticsList.size(); i++) {
                logisticsList.get(i).setId((long) (i + 1));
                logisticsList.get(i).setCreateTime(LocalDateTime.now());
            }
            return logisticsList;
        });
        when(orderItemRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        // --- Act ---
        List<LogisticsVO> result = logisticsService.createLogistics(createDTO);

        // --- Assert ---
        assertNotNull(result);
        assertEquals(1, result.size());
        LogisticsVO logisticsVO = result.get(0);
        assertEquals(orderItem.getId(), logisticsVO.getOrderItemId());
        assertEquals(createDTO.getCompany().name(), logisticsVO.getCompanyCode());
        assertEquals(createDTO.getCompany().getDisplayName(), logisticsVO.getCompanyName());
        assertEquals(createDTO.getTrackingNumber(), logisticsVO.getTrackingNumber());

        verify(orderItemRepository, times(1)).findAllById(createDTO.getOrderItemIds());
        verify(logisticsRepository, times(1)).existsByOrderItem(orderItem);
        verify(logisticsRepository, times(1)).saveAll(anyList());
        verify(orderItemRepository, times(1)).saveAll(anyList());
        
        // 验证订单项状态被更新为已发货
        assertEquals(OrderItemStatus.SHIPPED, orderItem.getStatus());
    }

    @Test
    void createLogistics_OrderItemStatusError() {
        // --- Arrange ---
        orderItem.setStatus(OrderItemStatus.CANCELLED); // 错误状态
        when(orderItemRepository.findAllById(createDTO.getOrderItemIds())).thenReturn(Arrays.asList(orderItem));

        // --- Act & Assert ---
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            logisticsService.createLogistics(createDTO);
        });

        assertEquals(ErrorTypeEnum.ORDER_ITEM_STATUS_ERROR, exception.getErrorType());
        verify(orderItemRepository, times(1)).findAllById(createDTO.getOrderItemIds());
        verify(logisticsRepository, never()).saveAll(anyList());
    }

    @Test
    void createLogistics_LogisticsAlreadyExists() {
        // --- Arrange ---
        when(orderItemRepository.findAllById(createDTO.getOrderItemIds())).thenReturn(Arrays.asList(orderItem));
        when(logisticsRepository.existsByOrderItem(orderItem)).thenReturn(true);

        // --- Act & Assert ---
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            logisticsService.createLogistics(createDTO);
        });

        assertEquals(ErrorTypeEnum.LOGISTICS_ALREADY_EXISTS, exception.getErrorType());
        verify(orderItemRepository, times(1)).findAllById(createDTO.getOrderItemIds());
        verify(logisticsRepository, times(1)).existsByOrderItem(orderItem);
        verify(logisticsRepository, never()).saveAll(anyList());
    }

    @Test
    void createLogistics_MultipleOrderItems() {
        // --- Arrange ---
        OrderItem orderItem2 = new OrderItem();
        orderItem2.setId(2L);
        orderItem2.setStatus(OrderItemStatus.PAID);

        createDTO.setOrderItemIds(Arrays.asList(1L, 2L));
        List<OrderItem> orderItems = Arrays.asList(orderItem, orderItem2);

        when(orderItemRepository.findAllById(createDTO.getOrderItemIds())).thenReturn(orderItems);
        when(logisticsRepository.existsByOrderItem(any(OrderItem.class))).thenReturn(false);
        when(logisticsRepository.saveAll(anyList())).thenAnswer(invocation -> {
            List<Logistics> logisticsList = invocation.getArgument(0);
            for (int i = 0; i < logisticsList.size(); i++) {
                logisticsList.get(i).setId((long) (i + 1));
                logisticsList.get(i).setCreateTime(LocalDateTime.now());
            }
            return logisticsList;
        });
        when(orderItemRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        // --- Act ---
        List<LogisticsVO> result = logisticsService.createLogistics(createDTO);

        // --- Assert ---
        assertNotNull(result);
        assertEquals(2, result.size());

        verify(orderItemRepository, times(1)).findAllById(createDTO.getOrderItemIds());
        verify(logisticsRepository, times(2)).existsByOrderItem(any(OrderItem.class));
        verify(logisticsRepository, times(1)).saveAll(anyList());
        verify(orderItemRepository, times(1)).saveAll(anyList());
    }

    // --- getLogisticsByOrderItemId 方法测试 ---
    @Test
    void getLogisticsByOrderItemId_Success() {
        // --- Arrange ---
        when(logisticsRepository.findByOrderItemId(1L)).thenReturn(Optional.of(logistics));

        // --- Act ---
        LogisticsVO result = logisticsService.getLogisticsByOrderItemId(1L);

        // --- Assert ---
        assertNotNull(result);
        assertEquals(logistics.getId(), result.getId());
        assertEquals(orderItem.getId(), result.getOrderItemId());
        assertEquals(logistics.getCompany().name(), result.getCompanyCode());
        assertEquals(logistics.getCompany().getDisplayName(), result.getCompanyName());
        assertEquals(logistics.getTrackingNumber(), result.getTrackingNumber());

        verify(logisticsRepository, times(1)).findByOrderItemId(1L);
    }

    @Test
    void getLogisticsByOrderItemId_LogisticsNotFound() {
        // --- Arrange ---
        when(logisticsRepository.findByOrderItemId(1L)).thenReturn(Optional.empty());

        // --- Act & Assert ---
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            logisticsService.getLogisticsByOrderItemId(1L);
        });

        assertEquals(ErrorTypeEnum.LOGISTICS_NOT_FOUND, exception.getErrorType());
        verify(logisticsRepository, times(1)).findByOrderItemId(1L);
    }
}
