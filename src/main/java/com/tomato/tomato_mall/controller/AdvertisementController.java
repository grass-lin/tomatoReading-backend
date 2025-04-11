package com.tomato.tomato_mall.controller;

import com.tomato.tomato_mall.dto.AdvertisementCreateDTO;
import com.tomato.tomato_mall.dto.AdvertisementUpdateDTO;
import com.tomato.tomato_mall.service.AdvertisementService;
import com.tomato.tomato_mall.vo.AdvertisementVO;
import com.tomato.tomato_mall.vo.ResponseVO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 广告控制器
 * <p>
 * 提供广告的增删改查的REST API接口
 * 所有接口返回统一的ResponseVO格式，包含状态码、消息和数据
 * 管理员角色拥有创建、更新和删除广告的权限
 * </p>
 * 
 * @author Team Tomato
 * @version 1.0
 */
@RestController
@RequestMapping("/api/advertisements")
public class AdvertisementController {

    private final AdvertisementService advertisementService;

    /**
     * 构造函数，通过依赖注入初始化服务
     * 
     * @param advertisementService 广告服务，处理广告相关业务逻辑
     */
    public AdvertisementController(AdvertisementService advertisementService) {
        this.advertisementService = advertisementService;
    }

    /**
     * 获取所有广告接口
     * <p>
     * 返回系统中所有广告的列表
     * </p>
     * 
     * @return 返回包含广告列表的响应体，状态码200
     */
    @GetMapping
    public ResponseEntity<ResponseVO<List<AdvertisementVO>>> getAllAdvertisements() {
        List<AdvertisementVO> advertisements = advertisementService.getAllAdvertisements();
        return ResponseEntity.ok(ResponseVO.success(advertisements));
    }

    /**
     * 根据ID获取广告详情接口
     * <p>
     * 返回指定ID的广告详细信息
     * </p>
     * 
     * @param id 广告ID
     * @return 返回包含广告详情的响应体，状态码200
     */
    @GetMapping("/{id}")
    public ResponseEntity<ResponseVO<AdvertisementVO>> getAdvertisementById(@PathVariable Long id) {
        AdvertisementVO advertisement = advertisementService.getAdvertisementById(id);
        return ResponseEntity.ok(ResponseVO.success(advertisement));
    }

    /**
     * 创建新广告接口
     * <p>
     * 创建一个新的广告记录，需要管理员权限
     * </p>
     * 
     * @param createDTO 广告创建数据传输对象，包含广告标题、内容、图片URL和商品ID等信息
     * @return 返回包含新创建广告信息的响应体，状态码200
     */
    @PostMapping
    public ResponseEntity<ResponseVO<AdvertisementVO>> createAdvertisement(@Valid @RequestBody AdvertisementCreateDTO createDTO) {
        AdvertisementVO newAdvertisement = advertisementService.createAdvertisement(createDTO);
        return ResponseEntity.ok(ResponseVO.success(newAdvertisement));
    }

    /**
     * 更新广告信息接口
     * <p>
     * 更新现有广告的信息，需要管理员权限
     * </p>
     * 
     * @param updateDTO 广告更新数据传输对象，包含需要更新的广告ID和信息
     * @return 返回包含更新后广告信息的响应体，状态码200
     */
    @PutMapping
    public ResponseEntity<ResponseVO<AdvertisementVO>> updateAdvertisement(@Valid @RequestBody AdvertisementUpdateDTO updateDTO) {
        AdvertisementVO updatedAdvertisement = advertisementService.updateAdvertisement(updateDTO);
        return ResponseEntity.ok(ResponseVO.success(updatedAdvertisement));
    }

    /**
     * 删除广告接口
     * <p>
     * 删除指定ID的广告记录，需要管理员权限
     * </p>
     * 
     * @param id 要删除的广告ID
     * @return 返回成功消息的响应体，状态码200
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseVO<String>> deleteAdvertisement(@PathVariable Long id) {
        advertisementService.deleteAdvertisement(id);
        return ResponseEntity.ok(ResponseVO.success("删除成功"));
    }
}