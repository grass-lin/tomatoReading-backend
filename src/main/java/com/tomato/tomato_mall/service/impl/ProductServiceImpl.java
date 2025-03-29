package com.tomato.tomato_mall.service.impl;

import com.tomato.tomato_mall.dto.ProductCreateDTO;
import com.tomato.tomato_mall.dto.ProductUpdateDTO;
import com.tomato.tomato_mall.dto.SpecificationDTO;
import com.tomato.tomato_mall.entity.Product;
import com.tomato.tomato_mall.entity.Specification;
import com.tomato.tomato_mall.entity.Stockpile;
import com.tomato.tomato_mall.repository.ProductRepository;
import com.tomato.tomato_mall.repository.SpecificationRepository;
import com.tomato.tomato_mall.repository.StockpileRepository;
import com.tomato.tomato_mall.service.ProductService;
import com.tomato.tomato_mall.vo.ProductVO;
import com.tomato.tomato_mall.vo.SpecificationVO;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
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
    private final SpecificationRepository specificationRepository;
    private final StockpileRepository stockpileRepository;

    /**
     * 构造函数，通过依赖注入初始化商品服务组件
     * 
     * @param productRepository       商品数据访问对象，负责商品数据的持久化操作
     * @param specificationRepository 规格数据访问对象，负责规格数据的持久化操作
     * @param stockpileRepository     库存数据访问对象，负责库存数据的持久化操作
     */
    public ProductServiceImpl(
            ProductRepository productRepository,
            SpecificationRepository specificationRepository,
            StockpileRepository stockpileRepository) {
        this.productRepository = productRepository;
        this.specificationRepository = specificationRepository;
        this.stockpileRepository = stockpileRepository;
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
     * @throws IllegalArgumentException 当商品标题已存在时抛出此异常，确保商品标题在系统中的唯一性
     */
    @Override
    @Transactional
    public ProductVO createProduct(ProductCreateDTO createDTO) {
        // 检查商品标题是否已存在
        if (productRepository.existsByTitle(createDTO.getTitle())) {
            throw new IllegalArgumentException("Product title already exists");
        }

        Product product = new Product();
        BeanUtils.copyProperties(createDTO, product);

        Product saveProduct = productRepository.save(product);

        // 处理规格信息
        if (createDTO.getSpecifications() != null && !createDTO.getSpecifications().isEmpty()) {
            Set<Specification> specifications =  createDTO.getSpecifications().stream()
                    .map(specDTO -> {
                        Specification spec = new Specification();
                        BeanUtils.copyProperties(specDTO, spec);
                        spec.setProduct(saveProduct);
                        return specificationRepository.save(spec);
                    })
                    .collect(Collectors.toSet());
            saveProduct.setSpecifications(specifications);
        } else {
            saveProduct.setSpecifications(new HashSet<>());
        }

        // 初始化库存
        Stockpile stockpile = new Stockpile();
        stockpile.setProduct(saveProduct);
        stockpile.setAmount(0);
        stockpile.setFrozen(0);
        stockpileRepository.save(stockpile);

        return convertToProductVO(saveProduct);
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
        List<Product> products = productRepository.findAll();
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
                .orElseThrow(() -> new NoSuchElementException("Product not found"));

        return convertToProductVO(product);
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
                .orElseThrow(() -> new NoSuchElementException("Product not found"));

        // 检查更新的标题是否与其他商品重复
        if (updateDTO.getTitle() != null && !updateDTO.getTitle().equals(product.getTitle())) {
            if (productRepository.existsByTitle(updateDTO.getTitle())) {
                throw new IllegalArgumentException("Product title already exists");
            }
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

        // 处理规格更新
        if (updateDTO.getSpecifications() != null) {
            updateProductSpecifications(product, updateDTO.getSpecifications());
        }

        Product updateProduct = productRepository.save(product);
        return convertToProductVO(updateProduct);
    }

    /**
     * 更新商品规格信息
     * <p>
     * 根据传入的规格信息更新商品的规格。
     * 如果规格已存在（根据item判断），则更新其值；
     * 如果规格不存在，则创建新规格。
     * 此方法实现了规格的增量更新，不会删除现有规格。
     * </p>
     * 
     * @param product        要更新规格的商品
     * @param specifications 规格信息列表
     */
    private void updateProductSpecifications(Product product, List<SpecificationDTO> specifications) {
        Set<Specification> existingSpecs = product.getSpecifications();

        for (SpecificationDTO specDTO : specifications) {
            // 查找匹配的现有规格
            Specification matchingSpec = existingSpecs.stream()
                    .filter(spec -> spec.getItem().equals(specDTO.getItem()))
                    .findFirst()
                    .orElse(null);

            if (matchingSpec != null) {
                // 更新现有规格
                if (specDTO.getValue() != null) {
                    matchingSpec.setValue(specDTO.getValue());
                    specificationRepository.save(matchingSpec);
                }
            } else {
                // 创建新规格
                Specification newSpec = new Specification();
                BeanUtils.copyProperties(specDTO, newSpec);
                newSpec.setProduct(product);
                Specification savedSpec = specificationRepository.save(newSpec);
                existingSpecs.add(savedSpec);
            }
        }
    }

    /**
     * 删除商品
     * <p>
     * 删除指定ID的商品记录及其相关联的规格和库存信息。
     * 使用事务确保数据一致性，防止部分删除导致的数据异常。
     * 此方法执行的是物理删除，会从数据库中彻底移除商品记录。
     * </p>
     * 
     * @param id 要删除的商品ID
     * @throws NoSuchElementException 当要删除的商品不存在时抛出此异常
     */
    @Override
    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new NoSuchElementException("Product not found");
        }

        // 删除商品（级联删除规格和库存）
        productRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteSpecification(Long specificationId) {
        if (!specificationRepository.existsById(specificationId)) {
            throw new NoSuchElementException("Specification not found");
        }
        specificationRepository.deleteById(specificationId);
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

        // 转换商品规格信息
        if (product.getSpecifications() != null) {
            List<SpecificationVO> specVOList = convertSpecificationsToVOs(product.getSpecifications());
            productVO.setSpecifications(specVOList);
        }

        return productVO;
    }

    /**
     * 将规格实体集合转换为规格视图对象列表
     * <p>
     * 将商品的规格实体对象转换为前端展示所需的规格视图对象。
     * 此方法处理规格的数据结构转换，并为每个规格设置关联的商品ID。
     * </p>
     * 
     * @param specifications 规格实体集合
     * @return 规格视图对象列表
     */
    private List<SpecificationVO> convertSpecificationsToVOs(Set<Specification> specifications) {
        if (specifications == null) {
            return new ArrayList<>();
        }

        return specifications.stream()
                .map(spec -> {
                    SpecificationVO specVO = new SpecificationVO();
                    BeanUtils.copyProperties(spec, specVO);
                    // 设置商品ID
                    if (spec.getProduct() != null) {
                        specVO.setProductId(spec.getProduct().getId());
                    }
                    return specVO;
                })
                .collect(Collectors.toList());
    }
}