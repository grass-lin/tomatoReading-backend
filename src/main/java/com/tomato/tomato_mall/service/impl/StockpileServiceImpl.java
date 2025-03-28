package com.tomato.tomato_mall.service.impl;

import com.tomato.tomato_mall.dto.StockpileUpdateDTO;
import com.tomato.tomato_mall.entity.Stockpile;
import com.tomato.tomato_mall.repository.ProductRepository;
import com.tomato.tomato_mall.repository.StockpileRepository;
import com.tomato.tomato_mall.service.StockpileService;
import com.tomato.tomato_mall.vo.StockpileVO;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

/**
 * 库存服务实现类
 * <p>
 * 该类实现了{@link StockpileService}接口，提供商品库存的查询和更新功能。
 * 包含库存信息处理和库存更新等业务逻辑的具体实现。
 * </p>
 * 
 * @author Team CBDDL
 * @version 1.0
 */
@Service
public class StockpileServiceImpl implements StockpileService {

    private final StockpileRepository stockpileRepository;
    private final ProductRepository productRepository;

    /**
     * 构造函数，通过依赖注入初始化库存服务组件
     * 
     * @param stockpileRepository 库存数据访问对象，负责库存数据的持久化操作
     * @param productRepository   商品数据访问对象，负责商品数据的持久化操作
     */
    public StockpileServiceImpl(StockpileRepository stockpileRepository, ProductRepository productRepository) {
        this.stockpileRepository = stockpileRepository;
        this.productRepository = productRepository;
    }

    /**
     * 根据商品ID获取库存信息
     * <p>
     * 查询指定商品ID的库存详情，包括可售数量和冻结数量。
     * </p>
     * 
     * @param productId 要查询库存的商品ID
     * @return 库存视图对象，包含库存ID、可售数量、冻结数量和商品ID
     * @throws NoSuchElementException 当商品不存在或该商品没有库存记录时抛出此异常
     */
    @Override
    public StockpileVO getStockpileByProductId(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new NoSuchElementException("Product not found");
        }
        
        Stockpile stockpile = stockpileRepository.findByProductId(productId)
                .orElseThrow(() -> new NoSuchElementException("Stockpile not found"));
        
        return convertToStockpileVO(stockpile);
    }

    /**
     * 更新商品库存数量
     * <p>
     * 根据商品ID和新的数量值更新库存。
     * 使用事务和乐观锁确保在并发更新时的数据一致性。
     * </p>
     * 
     * @param productId 要更新库存的商品ID
     * @param amount    新的库存数量，必须大于等于冻结数量
     * @return 更新后的库存视图对象
     * @throws NoSuchElementException 当商品或库存记录不存在时抛出此异常
     * @throws IllegalArgumentException 当新库存量小于冻结数量时抛出此异常
     */
    @Override
    @Transactional
    public StockpileVO updateStockpile(Long productId, StockpileUpdateDTO stockpileUpdateDTO) {
        productRepository.findById(productId)
                .orElseThrow(() -> new NoSuchElementException("Product not found"));
        
        Stockpile stockpile = stockpileRepository.findByProductId(productId)
                .orElseThrow(() -> new NoSuchElementException("Stockpile not found"));

        Integer amount = stockpileUpdateDTO.getAmount();
        
        // 验证库存数量是否合法
        if (amount < stockpile.getFrozen()) {
            throw new IllegalArgumentException("Amount cannot be less than frozen amount");
        }
        
        stockpile.setAmount(amount);
        stockpile = stockpileRepository.save(stockpile);
        
        return convertToStockpileVO(stockpile);
    }

    /**
     * 将库存实体转换为视图对象
     * <p>
     * 封装库存实体到前端展示层所需的数据结构。
     * </p>
     * 
     * @param stockpile 要转换的库存实体
     * @return 转换后的库存视图对象
     */
    private StockpileVO convertToStockpileVO(Stockpile stockpile) {
        return StockpileVO.builder()
                .id(stockpile.getId())
                .amount(stockpile.getAmount())
                .frozen(stockpile.getFrozen())
                .productId(stockpile.getProduct().getId())
                .build();
    }
}