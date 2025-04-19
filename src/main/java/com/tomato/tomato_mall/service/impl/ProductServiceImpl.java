package com.tomato.tomato_mall.service.impl;

import com.tomato.tomato_mall.dto.ProductCreateDTO;
import com.tomato.tomato_mall.dto.ProductUpdateDTO;
import com.tomato.tomato_mall.entity.CartItem;
import com.tomato.tomato_mall.entity.Product;
import com.tomato.tomato_mall.entity.Product.ProductStatus;
import com.tomato.tomato_mall.entity.Specification;
import com.tomato.tomato_mall.entity.Stockpile;
import com.tomato.tomato_mall.entity.CartItem.CartItemStatus;
import com.tomato.tomato_mall.entity.OrderItem;
import com.tomato.tomato_mall.entity.OrderItem.OrderItemStatus;
import com.tomato.tomato_mall.repository.AdvertisementRepository;
import com.tomato.tomato_mall.repository.CartRepository;
import com.tomato.tomato_mall.repository.OrderItemRepository;
import com.tomato.tomato_mall.enums.ErrorTypeEnum;
import com.tomato.tomato_mall.exception.BusinessException;
import com.tomato.tomato_mall.repository.ProductRepository;
import com.tomato.tomato_mall.repository.SpecificationRepository;
import com.tomato.tomato_mall.repository.StockpileRepository;
import com.tomato.tomato_mall.service.ProductService;
import com.tomato.tomato_mall.vo.ProductVO;
import com.tomato.tomato_mall.vo.SpecificationVO;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
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

    /**
     * 构造函数，通过依赖注入初始化商品服务组件
     * 
     * @param productRepository       商品数据访问对象，负责商品数据的持久化操作
     * @param cartRepository          购物车项数据访问对象，负责购物车数据的持久化操作
     * @param orderItemRepository     订单项数据访问对象，负责订单项数据的持久化操作
     * @param advertisementRepository 广告数据访问对象，负责广告数据的持久化操作
     */
    public ProductServiceImpl(
            ProductRepository productRepository,
            SpecificationRepository specificationRepository,
            StockpileRepository stockpileRepository,
            CartRepository cartRepository,
            OrderItemRepository orderItemRepository,
            AdvertisementRepository advertisementRepository) {
        this.productRepository = productRepository;
        this.stockpileRepository = stockpileRepository;
        this.cartRepository = cartRepository;
        this.orderItemRepository = orderItemRepository;
        this.advertisementRepository = advertisementRepository;
    }

    /**
     * 创建新商品
     * <p>
     * 将DTO转换为实体，保存商品信息，同时创建相关的规格和初始库存。
     * 使用事务确保数据完整性，所有操作要么全部成功，要么全部失败。
     * </p>
     * 
     * @param createDTO 商品创建数据传输对象，包含创建所需的商品信息
     * @return 创建成功的商品视图对象
     */
    @Override
    @Transactional
    public ProductVO createProduct(ProductCreateDTO createDTO) {
        Product product = new Product();
        BeanUtils.copyProperties(createDTO, product);

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

        return convertToProductVO(savedProduct);
    }

    /**
     * 删除商品
     * <p>
     * 删除指定ID的商品记录及其相关联的规格和库存信息。
     * 使用事务确保数据一致性，防止部分删除导致的数据异常。
     * 此方法执行的是逻辑删除。
     * </p>
     * 
     * @param id 要删除的商品ID
     * @throws NoSuchElementException 当要删除的商品不存在时抛出此异常
     */
    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorTypeEnum.PRODUCT_NOT_FOUND));

        if (product.getStatus().equals(ProductStatus.DELETED)) {
            throw new BusinessException(ErrorTypeEnum.PRODUCT_NOT_FOUND);
        }

        if (orderItemRepository.existsByProductIdAndStatus(id, OrderItemStatus.PENDING)) {
            throw new BusinessException(ErrorTypeEnum.PRODUCT_OCCUPIED_BY_ORDER);
        }

        // 处理购物车项
        List<CartItem> cartItems = cartRepository.findByProductIdAndStatusNot(
                id, CartItemStatus.COMPLETED);
        cartItems.forEach(item -> {
            item.setStatus(CartItemStatus.CANCELLED);
        });
        cartRepository.saveAll(cartItems);

        // 处理订单项
        List<OrderItem> orderItems = orderItemRepository.findByProductId(id);
        orderItems.forEach(item -> {
            item.setProduct(null);
        });
        orderItemRepository.saveAll(orderItems);

        // 删除规格
        product.getSpecifications().clear();

        // 删除库存
        Stockpile stockpile = product.getStockpile();
        if (stockpile != null) {
            product.setStockpile(null);
            stockpileRepository.delete(stockpile);
        }

        // 删除广告
        advertisementRepository.deleteAllByProduct(product);

        product.setStatus(ProductStatus.DELETED);
    }

    /**
     * 更新商品信息
     * <p>
     * 根据传入的更新数据，有选择地更新商品信息。
     * 只会更新非空字段，保持其他字段不变。
     * 更新规格信息时, 根据item搜索原有规格, 为空则新增, 否则更新。
     * </p>
     * 
     * @param updateDTO 商品更新数据传输对象
     * @return 更新后的商品视图对象
     * @throws NoSuchElementException   当要更新的商品不存在时抛出此异常
     * @throws IllegalArgumentException 当更新的商品标题已被其他商品使用时抛出此异常，确保商品标题在系统中的唯一性
     */
    @Override
    @Transactional
    public ProductVO updateProduct(ProductUpdateDTO updateDTO) {
        Product product = productRepository.findById(updateDTO.getId())
                .orElseThrow(() -> new BusinessException(ErrorTypeEnum.PRODUCT_NOT_FOUND));
        if (!product.getStatus().equals(ProductStatus.ACTIVE)) {
            throw new BusinessException(ErrorTypeEnum.PRODUCT_NOT_FOUND);
        }

        // 有选择地更新商品字段
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
            product.getSpecifications().clear();
            List<Specification> specifications = updateDTO.getSpecifications().stream()
                    .map(specDTO -> {
                        Specification spec = new Specification();
                        BeanUtils.copyProperties(specDTO, spec);
                        spec.setProduct(product);
                        return spec;
                    })
                    .collect(Collectors.toList());
            product.setSpecifications(specifications);
        }

        Product updateProduct = productRepository.save(product);
        return convertToProductVO(updateProduct);
    }

    /**
     * 获取所有商品
     * <p>
     * 查询所有商品信息，并转换为前端展示所需的视图对象列表。
     * 此方法不会过滤任何商品，返回数据库中的所有商品记录。
     * </p>
     * 
     * @return 所有商品的视图对象列表
     */
    @Override
    public List<ProductVO> getAllProducts() {
        List<Product> products = productRepository.findByStatus(ProductStatus.ACTIVE);
        return products.stream()
                .map(this::convertToProductVO)
                .collect(Collectors.toList());
    }

    /**
     * 根据ID获取商品
     * <p>
     * 查询指定ID的商品详细信息，包括规格信息。
     * 此方法会检查商品是否存在，不存在则抛出异常。
     * </p>
     * 
     * @param id 要查询的商品ID
     * @return 商品的视图对象
     * @throws NoSuchElementException 当指定ID的商品不存在时抛出此异常
     */
    @Override
    public ProductVO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorTypeEnum.PRODUCT_NOT_FOUND));
        if (!product.getStatus().equals(ProductStatus.ACTIVE)) {
            throw new BusinessException(ErrorTypeEnum.PRODUCT_NOT_FOUND);
        }
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