package com.tomato.tomato_mall.service.impl;

import com.tomato.tomato_mall.dto.AdvertisementCreateDTO;
import com.tomato.tomato_mall.dto.AdvertisementUpdateDTO;
import com.tomato.tomato_mall.entity.Advertisement;
import com.tomato.tomato_mall.entity.Product;
import com.tomato.tomato_mall.repository.AdvertisementRepository;
import com.tomato.tomato_mall.repository.ProductRepository;
import com.tomato.tomato_mall.service.AdvertisementService;
import com.tomato.tomato_mall.vo.AdvertisementVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
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
     * @param advertisementRepository 广告数据访问对象，负责广告数据的持久化操作
     * @param productRepository 商品数据访问对象，负责商品数据的持久化操作
     */
    public AdvertisementServiceImpl(
            AdvertisementRepository advertisementRepository,
            ProductRepository productRepository) {
        this.advertisementRepository = advertisementRepository;
        this.productRepository = productRepository;
    }

    /**
     * 获取所有广告
     * <p>
     * 查询所有广告信息，并转换为前端展示所需的视图对象列表。
     * 此方法不会过滤任何广告，返回数据库中的所有广告记录。
     * </p>
     * 
     * @return 所有广告的视图对象列表
     */
    @Override
    public List<AdvertisementVO> getAllAdvertisements() {
        List<Advertisement> advertisements = advertisementRepository.findAll();
        return advertisements.stream()
                .map(this::convertToAdvertisementVO)
                .collect(Collectors.toList());
    }

    /**
     * 根据ID获取广告
     * <p>
     * 查询指定ID的广告详细信息，包括关联的商品信息。
     * 此方法会检查广告是否存在，不存在则抛出异常。
     * </p>
     * 
     * @param id 要查询的广告ID
     * @return 广告的视图对象
     * @throws NoSuchElementException 当指定ID的广告不存在时抛出此异常
     */
    @Override
    public AdvertisementVO getAdvertisementById(Long id) {
        Advertisement advertisement = advertisementRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("广告不存在"));

        return convertToAdvertisementVO(advertisement);
    }

    /**
     * 创建新广告
     * <p>
     * 将DTO转换为实体，保存广告信息，同时验证关联商品的存在性。
     * 使用事务确保数据完整性，所有操作要么全部成功，要么全部失败。
     * </p>
     * 
     * @param createDTO 广告创建数据传输对象，包含创建所需的广告信息
     * @return 创建成功的广告视图对象
     * @throws NoSuchElementException 当关联的商品不存在时抛出此异常
     */
    @Override
    @Transactional
    public AdvertisementVO createAdvertisement(AdvertisementCreateDTO createDTO) {
        // 检查关联商品是否存在
        Product product = productRepository.findById(createDTO.getProductId())
                .orElseThrow(() -> new NoSuchElementException("商品不存在"));
        
        Advertisement advertisement = new Advertisement();
        BeanUtils.copyProperties(createDTO, advertisement);
        advertisement.setImageUrl(createDTO.getImgUrl());
        advertisement.setProduct(product);
        
        Advertisement savedAdvertisement = advertisementRepository.save(advertisement);
        
        return convertToAdvertisementVO(savedAdvertisement);
    }

    /**
     * 更新广告信息
     * <p>
     * 根据传入的更新数据，有选择地更新广告信息。
     * 只会更新非空字段，保持其他字段不变。
     * </p>
     * 
     * @param updateDTO 广告更新数据传输对象
     * @return 更新后的广告视图对象
     * @throws NoSuchElementException 当要更新的广告或关联的商品不存在时抛出此异常
     */
    @Override
    @Transactional
    public AdvertisementVO updateAdvertisement(AdvertisementUpdateDTO updateDTO) {
        Advertisement advertisement = advertisementRepository.findById(updateDTO.getId())
                .orElseThrow(() -> new NoSuchElementException("广告不存在"));
        
        // 如果更新了关联商品，需要验证商品是否存在
        if (updateDTO.getProductId() != null) {
            Product product = productRepository.findById(updateDTO.getProductId())
                    .orElseThrow(() -> new NoSuchElementException("商品不存在"));
            advertisement.setProduct(product);
        }
        
        // 有选择地更新广告字段
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

    /**
     * 删除广告
     * <p>
     * 删除指定ID的广告记录。
     * 此方法执行的是物理删除，会从数据库中彻底移除广告记录。
     * </p>
     * 
     * @param id 要删除的广告ID
     * @throws NoSuchElementException 当要删除的广告不存在时抛出此异常
     */
    @Override
    @Transactional
    public void deleteAdvertisement(Long id) {
        if (!advertisementRepository.existsById(id)) {
            throw new NoSuchElementException("广告不存在");
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