package com.tomato.tomato_mall.service;

import com.tomato.tomato_mall.dto.ProductCreateDTO;
import com.tomato.tomato_mall.dto.ProductUpdateDTO;
import com.tomato.tomato_mall.vo.ProductVO;

import java.util.List;

/**
 * 商品服务接口
 * <p>
 * 该接口定义了商品管理的核心业务功能，包括商品的创建、查询、更新和删除等操作。
 * 作为系统商品管理的核心组件，提供了商品全生命周期的管理功能。
 * </p>
 * <p>
 * 接口的实现类通常需要与商品数据访问层、库存服务等组件协作，
 * 以完成商品信息的持久化和相关业务逻辑处理。
 * </p>
 *
 * @author Team CBDDL
 * @version 1.0
 * @see com.tomato.tomato_mall.repository.ProductRepository
 * @see com.tomato.tomato_mall.service.StockpileService
 */
public interface ProductService {

    /**
     * 创建新商品
     * <p>
     * 根据提供的创建数据传输对象创建新的商品记录，包括初始化商品信息、
     * 分配商品ID等操作。创建成功后返回包含完整商品信息的视图对象。
     * </p>
     *
     * @param createDTO 商品创建数据传输对象，包含商品名称、价格、描述等信息
     * @return 创建成功的商品视图对象
     * @throws IllegalArgumentException 当商品标题已存在时抛出此异常
     */
    ProductVO createProduct(ProductCreateDTO createDTO);

    /**
     * 获取所有商品
     * <p>
     * 检索系统中所有可用的商品记录，返回商品视图对象的列表。
     * 该方法通常用于商品列表页面的数据获取。
     * </p>
     *
     * @return 包含所有商品信息的视图对象列表
     */
    List<ProductVO> getAllProducts();

    /**
     * 根据ID获取商品
     * <p>
     * 根据提供的商品ID查询商品详细信息，返回对应的商品视图对象。
     * 该方法通常用于商品详情页面的数据获取。
     * </p>
     *
     * @param id 要查询的商品ID
     * @return 商品视图对象
     * @throws java.util.NoSuchElementException 当指定ID的商品不存在时抛出此异常
     */
    ProductVO getProductById(Long id);

    /**
     * 更新商品信息
     * <p>
     * 根据提供的更新数据对商品信息进行更新。遵循部分更新原则，只更新提供的非空字段。
     * 更新成功后返回包含更新后商品信息的视图对象。
     * </p>
     *
     * @param updateDTO 商品更新数据传输对象，包含要更新的字段
     * @return 更新后的商品视图对象
     * @throws java.util.NoSuchElementException 当要更新的商品不存在时抛出此异常
     * @throws IllegalArgumentException 当商品标题已存在时抛出此异常
     */
    ProductVO updateProduct(ProductUpdateDTO updateDTO);

    /**
     * 删除商品
     * <p>
     * 根据提供的商品ID删除对应的商品记录。根据业务需求，可能是物理删除或逻辑删除。
     * 如果是逻辑删除，通常会保留商品记录但将其标记为已删除状态。
     * </p>
     *
     * @param id 要删除的商品ID
     * @throws java.util.NoSuchElementException 当指定ID的商品不存在时抛出此异常
     */
    void deleteProduct(Long id);

    void deleteSpecification(Long specificationId);
}