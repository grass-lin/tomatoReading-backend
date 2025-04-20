package com.tomato.tomato_mall.service;

import com.tomato.tomato_mall.dto.AdvertisementCreateDTO;
import com.tomato.tomato_mall.dto.AdvertisementUpdateDTO;
import com.tomato.tomato_mall.vo.AdvertisementVO;
import java.util.List;

/**
 * 广告服务接口
 * <p>
 * 该接口定义了广告管理的核心业务功能，包括广告的创建、查询、更新和删除等操作。
 * 作为系统广告管理的核心组件，提供了广告全生命周期的管理功能。
 * </p>
 * <p>
 * 接口的实现类通常需要与广告数据访问层、商品服务等组件协作，
 * 以完成广告信息的持久化和相关业务逻辑处理。
 * </p>
 *
 * @author Team CBDDL
 * @version 1.0
 * @see com.tomato.tomato_mall.repository.AdvertisementRepository
 * @see com.tomato.tomato_mall.service.ProductService
 */
public interface AdvertisementService {

    /**
     * 获取所有广告
     * <p>
     * 检索系统中所有可用的广告记录，返回广告视图对象的列表。
     * 该方法通常用于广告管理页面的数据获取。
     * </p>
     *
     * @return 包含所有广告信息的视图对象列表
     */
    List<AdvertisementVO> getAllAdvertisements();

    /**
     * 根据ID获取广告
     * <p>
     * 根据提供的广告ID查询广告详细信息，返回对应的广告视图对象。
     * 该方法通常用于广告详情页面的数据获取或编辑前的数据加载。
     * </p>
     *
     * @param id 要查询的广告ID
     * @return 广告视图对象
     */
    AdvertisementVO getAdvertisementById(Long id);

    /**
     * 创建新广告
     * <p>
     * 根据提供的创建数据传输对象创建新的广告记录，包括初始化广告信息、
     * 关联商品、分配广告ID等操作。创建成功后返回包含完整广告信息的视图对象。
     * </p>
     *
     * @param createDTO 广告创建数据传输对象，包含广告标题、内容、图片URL和商品ID等信息
     * @return 创建成功的广告视图对象
     */
    AdvertisementVO createAdvertisement(AdvertisementCreateDTO createDTO);

    /**
     * 更新广告信息
     * <p>
     * 根据提供的更新数据对广告信息进行更新。遵循部分更新原则，只更新提供的非空字段。
     * 更新成功后返回包含更新后广告信息的视图对象。
     * </p>
     *
     * @param updateDTO 广告更新数据传输对象，包含要更新的字段
     * @return 更新后的广告视图对象
     */
    AdvertisementVO updateAdvertisement(AdvertisementUpdateDTO updateDTO);

    /**
     * 删除广告
     * <p>
     * 根据提供的广告ID删除对应的广告记录。此方法执行的是物理删除，
     * 会从数据库中彻底移除广告记录。
     * </p>
     *
     * @param id 要删除的广告ID
     */
    void deleteAdvertisement(Long id);
}