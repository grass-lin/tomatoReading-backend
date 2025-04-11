package com.tomato.tomato_mall.controller;

import com.tomato.tomato_mall.dto.ProductCreateDTO;
import com.tomato.tomato_mall.dto.ProductUpdateDTO;
import com.tomato.tomato_mall.dto.StockpileUpdateDTO;
import com.tomato.tomato_mall.service.ProductService;
import com.tomato.tomato_mall.service.StockpileService;
import com.tomato.tomato_mall.vo.ProductVO;
import com.tomato.tomato_mall.vo.ResponseVO;
import com.tomato.tomato_mall.vo.StockpileVO;
import jakarta.validation.Valid;

// import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品控制器
 * <p>
 * 提供商品的增删改查以及库存管理的REST API接口
 * 所有接口返回统一的ResponseVO格式，包含状态码、消息和数据
 * 管理员角色拥有创建、更新和删除商品的权限
 * </p>
 * 
 * @author Team Tomato
 * @version 1.0
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final StockpileService stockpileService;

    /**
     * 构造函数，通过依赖注入初始化服务
     * 
     * @param productService   商品服务，处理商品相关业务逻辑
     * @param stockpileService 库存服务，处理商品库存相关业务逻辑
     */
    public ProductController(ProductService productService, StockpileService stockpileService) {
        this.productService = productService;
        this.stockpileService = stockpileService;
    }

    /**
     * 获取所有商品接口
     * <p>
     * 返回系统中所有可用商品的列表
     * </p>
     * 
     * @return 返回包含商品列表的响应体，状态码200
     */
    @GetMapping
    public ResponseEntity<ResponseVO<List<ProductVO>>> getAllProducts() {
        List<ProductVO> products = productService.getAllProducts();
        return ResponseEntity.ok(ResponseVO.success(products));
    }

    /**
     * 根据ID获取商品详情接口
     * <p>
     * 返回指定ID的商品详细信息
     * </p>
     * 
     * @param id 商品ID
     * @return 返回包含商品详情的响应体，状态码200
     */
    @GetMapping("/{id}")
    public ResponseEntity<ResponseVO<ProductVO>> getProductById(@PathVariable Long id) {
        ProductVO product = productService.getProductById(id);
        return ResponseEntity.ok(ResponseVO.success(product));
    }

    /**
     * 创建新商品接口
     * <p>
     * 创建一个新的商品记录，需要管理员权限
     * </p>
     * 
     * @param createDTO 商品创建数据传输对象，包含商品名称、价格、描述等信息
     * @return 返回包含新创建商品信息的响应体，状态码200
     * @throws org.springframework.security.access.AccessDeniedException 当用户没有管理员权限时抛出
     */
    @PostMapping
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<ResponseVO<ProductVO>> createProduct(@Valid @RequestBody ProductCreateDTO createDTO) {
        ProductVO newProduct = productService.createProduct(createDTO);
        // Bad practice
        return ResponseEntity.ok(ResponseVO.success(newProduct));
        // return ResponseEntity.status(HttpStatus.CREATED).body(ResponseVO.success(newProduct));
    }

    /**
     * 更新商品信息接口
     * <p>
     * 更新现有商品的信息，需要管理员权限
     * </p>
     * 
     * @param updateDTO 商品更新数据传输对象，包含需要更新的商品ID和信息
     * @return 返回成功消息的响应体，状态码200
     * @throws org.springframework.security.access.AccessDeniedException 当用户没有管理员权限时抛出
     */
    @PutMapping
    @PreAuthorize("hasRole('admin')")
    // Bad practice
    public ResponseEntity<ResponseVO<String>> updateProduct(@Valid @RequestBody ProductUpdateDTO updateDTO) {
        productService.updateProduct(updateDTO);
        return ResponseEntity.ok(ResponseVO.success("更新成功"));
    }
    // public ResponseEntity<ResponseVO<ProductVO>> updateProduct(@Valid @RequestBody ProductUpdateDTO updateDTO) {
    //     ProductVO updatedProduct = productService.updateProduct(updateDTO);
    //     return ResponseEntity.ok(ResponseVO.success(updatedProduct));
    // }

    /**
     * 删除商品接口
     * <p>
     * 删除指定ID的商品记录，需要管理员权限
     * </p>
     * 
     * @param id 要删除的商品ID
     * @return 返回成功消息的响应体，状态码200
     * @throws org.springframework.security.access.AccessDeniedException 当用户没有管理员权限时抛出
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<ResponseVO<String>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(ResponseVO.success("删除成功"));
    }

    /**
     * 获取商品库存接口
     * <p>
     * 返回指定商品ID的库存信息
     * </p>
     * 
     * @param productId 商品ID
     * @return 返回包含库存信息的响应体，状态码200
     */
    @GetMapping("/stockpile/{productId}")
    public ResponseEntity<ResponseVO<StockpileVO>> getProductStockpile(@PathVariable Long productId) {
        StockpileVO stockpile = stockpileService.getStockpileByProductId(productId);
        return ResponseEntity.ok(ResponseVO.success(stockpile));
    }

    /**
     * 更新商品库存接口
     * <p>
     * 调整指定商品的库存数量，需要管理员权限
     * </p>
     * 
     * @param productId 商品ID
     * @param stockpileUpdateDTO 库存更新数据传输对象，包含库存调整信息
     * @return 返回成功消息的响应体，状态码200
     * @throws java.util.NoSuchElementException 当商品不存在时抛出
     * @throws org.springframework.security.access.AccessDeniedException 当用户没有管理员权限时抛出
     */
    @PatchMapping("/stockpile/{productId}")
    @PreAuthorize("hasRole('admin')")
    // Bad practice
    public ResponseEntity<ResponseVO<String>> updateProductStock(
            @PathVariable Long productId,
            @Valid @RequestBody StockpileUpdateDTO stockpileUpdateDTO) {
        stockpileService.updateStockpile(productId, stockpileUpdateDTO);
        return ResponseEntity.ok(ResponseVO.success("调整库存成功"));
    }
    // public ResponseEntity<ResponseVO<StockpileVO>> updateProductStock(
    //         @PathVariable Long productId,
    //         @Valid @RequestBody StockpileUpdateDTO stockpileUpdateDTO) {
    //     StockpileVO updatedProduct = stockpileService.updateStockpile(productId, stockpileUpdateDTO);
    //     return ResponseEntity.ok(ResponseVO.success(updatedProduct));
    // }
}