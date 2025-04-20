package com.tomato.tomato_mall.controller;

import com.tomato.tomato_mall.dto.LogisticsCreateDTO;
import com.tomato.tomato_mall.service.LogisticsService;
import com.tomato.tomato_mall.vo.LogisticsVO;
import com.tomato.tomato_mall.vo.ResponseVO;
import jakarta.validation.Valid;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 物流控制器
 * <p>
 * 提供物流信息管理的REST API接口
 * 所有接口返回统一的ResponseVO格式，包含状态码、消息和数据
 * </p>
 * 
 * @author Team CBDDL
 * @version 1.0
 */
@RestController
@RequestMapping("/api/logistics")
public class LogisticsController {
    
    private final LogisticsService logisticsService;
    
    /**
     * 构造函数，通过依赖注入初始化服务
     */
    public LogisticsController(LogisticsService logisticsService) {
        this.logisticsService = logisticsService;
    }
    
    /**
     * 创建物流信息接口
     * <p>
     * 管理员为已支付的订单项列表添加物流信息，标记订单项为已发货
     * </p>
     * 
     * @param createDTO 物流信息创建数据传输对象
     * @return 返回包含物流信息的响应体，状态码200
     */
    @PostMapping
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<ResponseVO<List<LogisticsVO>>> createLogistics(
            @Valid @RequestBody LogisticsCreateDTO createDTO) {
        List<LogisticsVO> logisticsVOs = logisticsService.createLogistics(createDTO);
        return ResponseEntity.ok(ResponseVO.success(logisticsVOs));
    }
    
    /**
     * 查询物流信息接口
     * <p>
     * 根据订单项ID查询对应的物流信息
     * </p>
     * 
     * @param orderItemId 订单项ID
     * @return 返回包含物流信息的响应体，状态码200
     */
    @GetMapping("/{orderItemId}")
    public ResponseEntity<ResponseVO<LogisticsVO>> getLogisticsByOrderItemId(
            @PathVariable Long orderItemId) {
        LogisticsVO logisticsVO = logisticsService.getLogisticsByOrderItemId(orderItemId);
        return ResponseEntity.ok(ResponseVO.success(logisticsVO));
    }
}