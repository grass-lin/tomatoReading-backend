package com.tomato.tomato_mall.service.impl;

import com.tomato.tomato_mall.dto.StockpileUpdateDTO;
import com.tomato.tomato_mall.entity.Stockpile;
import com.tomato.tomato_mall.enums.ErrorTypeEnum;
import com.tomato.tomato_mall.exception.BusinessException;
import com.tomato.tomato_mall.repository.ProductRepository;
import com.tomato.tomato_mall.repository.StockpileRepository;
import com.tomato.tomato_mall.service.StockpileService;
import com.tomato.tomato_mall.vo.StockpileVO;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
     * @param stockpileRepository 库存数据访问对象
     * @param productRepository   商品数据访问对象
     */
    public StockpileServiceImpl(StockpileRepository stockpileRepository, ProductRepository productRepository) {
        this.stockpileRepository = stockpileRepository;
        this.productRepository = productRepository;
    }

    @Override
    public StockpileVO getStockpileByProductId(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new BusinessException(ErrorTypeEnum.PRODUCT_NOT_FOUND);
        }

        Stockpile stockpile = stockpileRepository.findByProductId(productId)
                .orElseThrow(() -> new BusinessException(ErrorTypeEnum.STOCKPILE_NOT_FOUND));

        return convertToStockpileVO(stockpile);
    }

    @Override
    @Transactional
    public StockpileVO updateStockpile(Long productId, StockpileUpdateDTO stockpileUpdateDTO) {
        productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorTypeEnum.PRODUCT_NOT_FOUND));

        Stockpile stockpile = stockpileRepository.findByProductId(productId)
                .orElseThrow(() -> new BusinessException(ErrorTypeEnum.STOCKPILE_NOT_FOUND));

        Integer amount = stockpileUpdateDTO.getAmount();

        // 验证库存数量是否合法
        if (amount < stockpile.getFrozen()) {
            throw new BusinessException(ErrorTypeEnum.STOCKPILE_NOT_ENOUGH);
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