package com.tomato.tomato_mall.service;

import com.tomato.tomato_mall.dto.StockpileUpdateDTO;
import com.tomato.tomato_mall.vo.StockpileVO;

/**
 * 库存服务接口
 * <p>
 * 该接口定义了商品库存管理的核心业务功能，包括库存查询和更新操作。
 * 作为系统库存管理的核心组件，提供了商品库存状态的监控与调整功能。
 * </p>
 * <p>
 * 接口的实现类通常需要与库存数据访问层、商品服务等组件协作，
 * 以完成库存信息的持久化和相关业务逻辑处理，如库存变更记录、库存预警等。
 * </p>
 *
 * @author Team Tomato
 * @version 1.0
 * @see com.tomato.tomato_mall.repository.StockpileRepository
 * @see com.tomato.tomato_mall.service.ProductService
 */
public interface StockpileService {

    /**
     * 根据商品ID获取库存信息
     * <p>
     * 查询指定商品ID的当前库存状态，返回包含库存数量、预警阈值等信息的视图对象。
     * 该方法通常用于商品详情页面的库存展示或后台库存管理页面。
     * </p>
     *
     * @param productId 要查询库存的商品ID
     * @return 商品库存视图对象
     * @throws java.util.NoSuchElementException 当指定商品不存在或没有对应库存记录时抛出此异常
     */
    StockpileVO getStockpileByProductId(Long productId);

    /**
     * 更新商品库存
     * <p>
     * 根据提供的更新数据调整指定商品的库存信息。可用于增加库存、减少库存、
     * 设置库存预警阈值等操作。更新成功后返回更新后的库存视图对象。
     * </p>
     * <p>
     * 库存调整操作通常会记录操作日志，包括调整时间、操作人、调整数量等信息，
     * 以便于后续审计和问题排查。
     * </p>
     *
     * @param productId 要更新库存的商品ID
     * @param stockpileUpdateDTO 库存更新数据传输对象，包含库存调整的具体信息
     * @return 更新后的库存视图对象
     * @throws java.util.NoSuchElementException 当指定商品不存在时抛出此异常
     * @throws IllegalArgumentException 当库存调整操作不合法时抛出此异常
     */
    StockpileVO updateStockpile(Long productId, StockpileUpdateDTO stockpileUpdateDTO);
}