package com.tomato.tomato_mall.controller;

import com.tomato.tomato_mall.dto.LogisticsCreateDTO;
import com.tomato.tomato_mall.entity.Logistics.LogisticsCompany;
import com.tomato.tomato_mall.service.LogisticsService;
import com.tomato.tomato_mall.vo.LogisticsVO;
import com.tomato.tomato_mall.vo.ResponseVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LogisticsControllerTest {

    @Mock
    private LogisticsService logisticsService;

    @InjectMocks
    private LogisticsController logisticsController;

    private LogisticsCreateDTO createDTO;
    private LogisticsVO logisticsVO;

    @BeforeEach
    void setUp() {
        createDTO = new LogisticsCreateDTO();
        createDTO.setOrderItemIds(Arrays.asList(1L, 2L));
        createDTO.setCompany(LogisticsCompany.YTO);
        createDTO.setTrackingNumber("YT2024031200001");

        logisticsVO = LogisticsVO.builder()
                .id(1L)
                .orderItemId(1L)
                .companyCode("YTO")
                .companyName("圆通速递")
                .trackingNumber("YT2024031200001")
                .createTime(LocalDateTime.now())
                .build();
    }

    @Test
    void testCreateLogistics_Success() {
        // --- Arrange ---
        LogisticsVO logistics1 = LogisticsVO.builder()
                .id(1L)
                .orderItemId(1L)
                .companyCode("YTO")
                .companyName("圆通速递")
                .trackingNumber("YT2024031200001")
                .createTime(LocalDateTime.now())
                .build();
        LogisticsVO logistics2 = LogisticsVO.builder()
                .id(2L)
                .orderItemId(2L)
                .companyCode("YTO")
                .companyName("圆通速递")
                .trackingNumber("YT2024031200002")
                .createTime(LocalDateTime.now())
                .build();
        List<LogisticsVO> logisticsList = Arrays.asList(logistics1, logistics2);
        when(logisticsService.createLogistics(any(LogisticsCreateDTO.class))).thenReturn(logisticsList);

        // --- Act ---
        ResponseEntity<ResponseVO<List<LogisticsVO>>> response = logisticsController.createLogistics(createDTO);

        // --- Assert ---
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ResponseVO<List<LogisticsVO>> body = response.getBody();
        assertNotNull(body);
        assertEquals(200, body.getCode());
        assertEquals(logisticsList, body.getData());
        assertEquals(2, body.getData().size());

        verify(logisticsService, times(1)).createLogistics(eq(createDTO));
    }

    @Test
    void testGetLogisticsByOrderItemId_Success() {
        // --- Arrange ---
        Long orderItemId = 1L;
        when(logisticsService.getLogisticsByOrderItemId(orderItemId)).thenReturn(logisticsVO);

        // --- Act ---
        ResponseEntity<ResponseVO<LogisticsVO>> response = logisticsController.getLogisticsByOrderItemId(orderItemId);

        // --- Assert ---
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ResponseVO<LogisticsVO> body = response.getBody();
        assertNotNull(body);
        assertEquals(200, body.getCode());
        assertEquals(logisticsVO, body.getData());

        verify(logisticsService, times(1)).getLogisticsByOrderItemId(eq(orderItemId));
    }
}
