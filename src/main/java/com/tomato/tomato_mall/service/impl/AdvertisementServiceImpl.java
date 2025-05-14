package com.tomato.tomato_mall.service.impl;

import com.tomato.tomato_mall.dto.AdvertisementCreateDTO;
import com.tomato.tomato_mall.dto.AdvertisementUpdateDTO;
import com.tomato.tomato_mall.entity.Advertisement;
import com.tomato.tomato_mall.entity.Product;
import com.tomato.tomato_mall.enums.ErrorTypeEnum;
import com.tomato.tomato_mall.exception.BusinessException;
import com.tomato.tomato_mall.repository.AdvertisementRepository;
import com.tomato.tomato_mall.repository.ProductRepository;
import com.tomato.tomato_mall.service.AdvertisementService;
import com.tomato.tomato_mall.vo.AdvertisementVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 广告服务实现类
 * <p>
 * 该类实现了{@link AdvertisementService}接口，提供广告的创建、查询、更新和删除等核心功能。
 * 包含广告信息处理、商品关联验证和数据转换等业务逻辑的具体实现。
 * </p>
 * 
 * @author Team CBDDL
 * @version 1.0
 */
@Service
public class AdvertisementServiceImpl implements AdvertisementService {

  private final AdvertisementRepository advertisementRepository;
  private final ProductRepository productRepository;

  /**
   * 构造函数，通过依赖注入初始化广告服务组件
   * 
   * @param advertisementRepository 广告数据访问对象
   * @param productRepository       商品数据访问对象
   */
  public AdvertisementServiceImpl(
      AdvertisementRepository advertisementRepository,
      ProductRepository productRepository) {
    this.advertisementRepository = advertisementRepository;
    this.productRepository = productRepository;
  }

  @Override
  public List<AdvertisementVO> getAllAdvertisements() {
    List<Advertisement> advertisements = advertisementRepository.findAll();
    return advertisements.stream()
        .map(this::convertToAdvertisementVO)
        .collect(Collectors.toList());
  }

  @Override
  public AdvertisementVO getAdvertisementById(Long id) {
    Advertisement advertisement = advertisementRepository.findById(id)
        .orElseThrow(() -> new BusinessException(ErrorTypeEnum.ADVERTISEMENT_NOT_FOUND));

    return convertToAdvertisementVO(advertisement);
  }

  @Override
  @Transactional
  public AdvertisementVO createAdvertisement(AdvertisementCreateDTO createDTO) {
    // 检查关联商品是否存在
    Product product = productRepository.findById(createDTO.getProductId())
        .orElseThrow(() -> new BusinessException(ErrorTypeEnum.PRODUCT_NOT_FOUND));

    Advertisement advertisement = new Advertisement();
    BeanUtils.copyProperties(createDTO, advertisement);
    advertisement.setImageUrl(createDTO.getImgUrl());
    advertisement.setProduct(product);

    Advertisement savedAdvertisement = advertisementRepository.save(advertisement);

    return convertToAdvertisementVO(savedAdvertisement);
  }

  @Override
  @Transactional
  public AdvertisementVO updateAdvertisement(AdvertisementUpdateDTO updateDTO) {
    Advertisement advertisement = advertisementRepository.findById(updateDTO.getId())
        .orElseThrow(() -> new BusinessException(ErrorTypeEnum.ADVERTISEMENT_NOT_FOUND));

    // 如果更新了关联商品，需要验证商品是否存在
    if (updateDTO.getProductId() != null) {
      Product product = productRepository.findById(updateDTO.getProductId())
          .orElseThrow(() -> new BusinessException(ErrorTypeEnum.PRODUCT_NOT_FOUND));
      advertisement.setProduct(product);
    }

    // Bad Practice: 更新非空字段
    if (updateDTO.getTitle() != null) {
      advertisement.setTitle(updateDTO.getTitle());
    }

    if (updateDTO.getContent() != null) {
      advertisement.setContent(updateDTO.getContent());
    }

    if (updateDTO.getImgUrl() != null) {
      advertisement.setImageUrl(updateDTO.getImgUrl());
    }

    Advertisement updatedAdvertisement = advertisementRepository.save(advertisement);

    return convertToAdvertisementVO(updatedAdvertisement);
  }

  @Override
  @Transactional
  public void deleteAdvertisement(Long id) {
    if (!advertisementRepository.existsById(id)) {
      throw new BusinessException(ErrorTypeEnum.ADVERTISEMENT_NOT_FOUND);
    }

    advertisementRepository.deleteById(id);
  }

  /**
   * 将广告实体转换为视图对象
   * <p>
   * 封装广告实体到前端展示层所需的数据结构，
   * 同时转换关联的商品信息。此方法负责数据模型层到展示层的转换，
   * 确保实体内部结构不直接暴露给外部。
   * </p>
   * 
   * @param advertisement 要转换的广告实体
   * @return 转换后的广告视图对象
   */
  private AdvertisementVO convertToAdvertisementVO(Advertisement advertisement) {
    AdvertisementVO advertisementVO = new AdvertisementVO();
    BeanUtils.copyProperties(advertisement, advertisementVO);
    // advertisementVO.setId(advertisement.getId().toString());
    advertisementVO.setImgUrl(advertisement.getImageUrl());

    // 设置商品相关信息
    if (advertisement.getProduct() != null) {
      advertisementVO.setProductId(advertisement.getProduct().getId());
    }

    return advertisementVO;
  }
}