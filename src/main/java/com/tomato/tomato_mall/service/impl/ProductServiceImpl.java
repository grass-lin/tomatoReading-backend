package com.tomato.tomato_mall.service.impl;

import com.tomato.tomato_mall.dto.ProductCreateDTO;
import com.tomato.tomato_mall.dto.ProductUpdateDTO;
import com.tomato.tomato_mall.entity.Product;
import com.tomato.tomato_mall.entity.Specification;
import com.tomato.tomato_mall.entity.Stockpile;
import com.tomato.tomato_mall.entity.OrderItem;
import com.tomato.tomato_mall.entity.OrderItem.OrderItemStatus;
import com.tomato.tomato_mall.repository.AdvertisementRepository;
import com.tomato.tomato_mall.repository.CartRepository;
import com.tomato.tomato_mall.repository.OrderItemRepository;
import com.tomato.tomato_mall.enums.ErrorTypeEnum;
import com.tomato.tomato_mall.exception.BusinessException;
import com.tomato.tomato_mall.repository.ProductRepository;
import com.tomato.tomato_mall.repository.StockpileRepository;
import com.tomato.tomato_mall.service.ProductIngestionService;
import com.tomato.tomato_mall.service.ProductService;
import com.tomato.tomato_mall.vo.ProductVO;
import com.tomato.tomato_mall.vo.SpecificationVO;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 商品服务实现类
 * <p>
 * 该类实现了{@link ProductService}接口，提供商品的创建、查询、更新和删除等核心功能。
 * 包含商品信息处理、规格管理和数据转换等业务逻辑的具体实现。
 * </p>
 * 
 * @author Team CBDDL
 * @version 1.0
 */
@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final StockpileRepository stockpileRepository;
    private final CartRepository cartRepository;
    private final OrderItemRepository orderItemRepository;
    private final AdvertisementRepository advertisementRepository;
    private final ProductIngestionService productIngestionService;

    /**
     * 构造函数，通过依赖注入初始化商品服务组件
     * 
     * @param productRepository       商品数据访问对象
     * @param stockpileRepository     库存数据访问对象
     * @param cartRepository          购物车项数据访问对象
     * @param orderItemRepository     订单项数据访问对象
     * @param advertisementRepository 广告数据访问对象
     */
    public ProductServiceImpl(
            ProductRepository productRepository,
            StockpileRepository stockpileRepository,
            CartRepository cartRepository,
            OrderItemRepository orderItemRepository,
            AdvertisementRepository advertisementRepository,
            ProductIngestionService productIngestionService) {
        this.productRepository = productRepository;
        this.stockpileRepository = stockpileRepository;
        this.cartRepository = cartRepository;
        this.orderItemRepository = orderItemRepository;
        this.advertisementRepository = advertisementRepository;
        this.productIngestionService = productIngestionService;
    }

    @Override
    @Transactional
    public ProductVO createProduct(ProductCreateDTO createDTO) {
        Product product = new Product();
        BeanUtils.copyProperties(createDTO, product);

        if (createDTO.getDetail() != null && createDTO.getDetail().length() > 500) {
            product.setDetail(createDTO.getDetail().substring(0, 490));
        }

        if (createDTO.getSpecifications() != null) {
            List<Specification> specifications = createDTO.getSpecifications().stream()
                    .map(specDTO -> {
                        Specification spec = new Specification();
                        BeanUtils.copyProperties(specDTO, spec);
                        spec.setProduct(product);
                        return spec;
                    })
                    .collect(Collectors.toList());
            product.setSpecifications(specifications);
        }

        Stockpile stockpile = new Stockpile();
        stockpile.setProduct(product);
        product.setStockpile(stockpile);

        Product savedProduct = productRepository.save(product);
        // productIngestionService.ingestProduct(savedProduct);

        return convertToProductVO(savedProduct);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorTypeEnum.PRODUCT_NOT_FOUND));

        // 未支付订单项占用商品时禁止删除
        if (orderItemRepository.existsByProductAndStatus(product, OrderItemStatus.PENDING)) {
            throw new BusinessException(ErrorTypeEnum.PRODUCT_OCCUPIED_BY_ORDER);
        }

        // 删除关联购物车项
        cartRepository.deleteByProductId(id);

        // 处理关联订单项
        List<OrderItem> orderItems = orderItemRepository.findByProduct(product);
        orderItems.forEach(item -> {
            item.setProduct(null);
        });
        orderItemRepository.saveAll(orderItems);

        // 删除规格
        product.getSpecifications().clear();

        // 删除关联库存
        Stockpile stockpile = product.getStockpile();
        if (stockpile != null) {
            product.setStockpile(null);
            stockpileRepository.delete(stockpile);
        }

        // 删除关联广告
        advertisementRepository.deleteAllByProduct(product);

        productRepository.delete(product);
        // productIngestionService.removeProduct(id);
    }

    @Override
    @Transactional
    public ProductVO updateProduct(ProductUpdateDTO updateDTO) {
        Product product = productRepository.findById(updateDTO.getId())
                .orElseThrow(() -> new BusinessException(ErrorTypeEnum.PRODUCT_NOT_FOUND));

        // 全量更新, 符合 PUT 语义
        // BeanUtils.copyProperties(updateDTO, product);

        // Bad Practice: 只更新非空字段
        if (updateDTO.getTitle() != null) {
            product.setTitle(updateDTO.getTitle());
        }
        if (updateDTO.getPrice() != null) {
            product.setPrice(updateDTO.getPrice());
        }
        if (updateDTO.getRate() != null) {
            product.setRate(updateDTO.getRate());
        }
        if (updateDTO.getDescription() != null) {
            product.setDescription(updateDTO.getDescription());
        }
        if (updateDTO.getCover() != null) {
            product.setCover(updateDTO.getCover());
        }
        if (updateDTO.getDetail() != null) {
            product.setDetail(updateDTO.getDetail());
        }

        // 处理规格更新, 替换更新
        if (updateDTO.getSpecifications() != null) {
            List<Specification> existingSpecs = product.getSpecifications();
            existingSpecs.clear();
            updateDTO.getSpecifications().forEach(specDTO -> {
                Specification spec = new Specification();
                BeanUtils.copyProperties(specDTO, spec);
                spec.setProduct(product);
                existingSpecs.add(spec);
            });
        }

        Product updateProduct = productRepository.save(product);
        // productIngestionService.updateProduct(updateProduct);
        return convertToProductVO(updateProduct);
    }

    @Override
    public List<ProductVO> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream()
                .map(this::convertToProductVO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<ProductVO> getProductsByPage(int page, int size) {
        int validSize = size > 0 ? size : 20;
        int validPage = Math.max(page, 0);

        Pageable pageable = PageRequest.of(validPage, validSize);
        Page<Product> productPage = productRepository.findAll(pageable);
        return productPage.map(this::convertToProductVO);
    }

    @Override
    public ProductVO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorTypeEnum.PRODUCT_NOT_FOUND));
        return convertToProductVO(product);
    }

    /**
     * 将商品实体转换为视图对象
     * <p>
     * 封装商品实体到前端展示层所需的数据结构，剔除敏感字段，
     * 同时转换关联的规格信息。此方法负责数据模型层到展示层的转换，
     * 确保实体内部结构不直接暴露给外部。
     * </p>
     * 
     * @param product 要转换的商品实体
     * @return 转换后的商品视图对象
     */
    private ProductVO convertToProductVO(Product product) {
        ProductVO productVO = new ProductVO();
        BeanUtils.copyProperties(product, productVO);
        List<SpecificationVO> specVOList = product.getSpecifications().stream().map(this::convertToSpecificationVO)
                .collect(Collectors.toList());
        productVO.setSpecifications(specVOList);
        return productVO;
    }

    /**
     * 将规格实体转换为视图对象
     * <p>
     * 封装规格实体到前端展示层所需的数据结构，剔除敏感字段，
     * 同时转换关联的商品ID。此方法负责数据模型层到展示层的转换，
     * 确保实体内部结构不直接暴露给外部。
     * </p>
     *
     * @param specification 要转换的规格实体
     * @return 转换后的规格视图对象
     */
    private SpecificationVO convertToSpecificationVO(Specification specification) {
        SpecificationVO specVO = new SpecificationVO();
        BeanUtils.copyProperties(specification, specVO);
        if (specification.getProduct() != null) {
            specVO.setProductId(specification.getProduct().getId());
        }
        return specVO;
    }
}