package com.tomato.tomato_mall.service.impl;

import com.tomato.tomato_mall.dto.CartAddDTO;
import com.tomato.tomato_mall.entity.CartItem;
import com.tomato.tomato_mall.entity.CartItem.CartItemStatus;
import com.tomato.tomato_mall.entity.Product;
import com.tomato.tomato_mall.entity.Stockpile;
import com.tomato.tomato_mall.entity.User;
import com.tomato.tomato_mall.repository.CartRepository;
import com.tomato.tomato_mall.repository.ProductRepository;
import com.tomato.tomato_mall.repository.StockpileRepository;
import com.tomato.tomato_mall.repository.UserRepository;
import com.tomato.tomato_mall.service.CartService;
import com.tomato.tomato_mall.vo.CartItemVO;
import com.tomato.tomato_mall.vo.CartVO;

import com.tomato.tomato_mall.exception.BusinessException;
import com.tomato.tomato_mall.enums.ErrorTypeEnum;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 购物车服务实现类
 * <p>
 * 该类实现了{@link CartService}接口，提供购物车的添加、查询、更新和删除等核心功能。
 * 加入了购物车项生命周期管理机制。
 * </p>
 * 
 * @author Team CBDDL
 * @version 1.0
 */
@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final StockpileRepository stockpileRepository;

    /**
     * 构造函数，通过依赖注入初始化购物车服务组件
     * 
     * @param cartRepository      购物车数据访问对象
     * @param userRepository      用户数据访问对象
     * @param productRepository   商品数据访问对象
     * @param stockpileRepository 库存数据访问对象
     */
    public CartServiceImpl(CartRepository cartRepository,
            UserRepository userRepository,
            ProductRepository productRepository,
            StockpileRepository stockpileRepository) {
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.stockpileRepository = stockpileRepository;
    }

    @Override
    @Transactional
    public CartItemVO addToCart(String username, CartAddDTO cartAddDTO) {
        // 验证用户和商品是否存在
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ErrorTypeEnum.USER_NOT_FOUND));

        Product product = productRepository.findById(cartAddDTO.getProductId())
                .orElseThrow(() -> new BusinessException(ErrorTypeEnum.PRODUCT_NOT_FOUND));

        // 校验库存
        Stockpile stockpile = stockpileRepository.findByProductId(product.getId())
                .orElseThrow(() -> new BusinessException(ErrorTypeEnum.STOCKPILE_NOT_FOUND));
        if (stockpile.getAmount() < cartAddDTO.getQuantity()) {
            throw new BusinessException(ErrorTypeEnum.STOCKPILE_NOT_ENOUGH);
        }

        // 检查购物车是否已有该商品且状态为激活
        Optional<CartItem> existingCartItem = cartRepository.findByUserAndProductAndStatus(
                user, product, CartItemStatus.ACTIVE);

        CartItem cartItem;
        if (existingCartItem.isPresent()) {
            // 已有该商品，更新数量
            cartItem = existingCartItem.get();
            int newQuantity = cartItem.getQuantity() + cartAddDTO.getQuantity();

            // 再次校验库存
            if (stockpile.getAmount() < newQuantity) {
                throw new BusinessException(ErrorTypeEnum.STOCKPILE_NOT_ENOUGH);
            }

            cartItem.setQuantity(newQuantity);
        } else {
            // 新增商品到购物车
            cartItem = new CartItem();
            cartItem.setUser(user);
            cartItem.setProduct(product);
            cartItem.setQuantity(cartAddDTO.getQuantity());
            cartItem.setStatus(CartItemStatus.ACTIVE);
        }

        cartItem = cartRepository.save(cartItem);
        return convertToCartItemVO(cartItem);
    }

    @Override
    @Transactional
    public void removeFromCart(String username, Long cartItemId) {
        // 验证用户和购物车项是否存在
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ErrorTypeEnum.USER_NOT_FOUND));

        CartItem cartItem = cartRepository.findById(cartItemId)
                .orElseThrow(() -> new BusinessException(ErrorTypeEnum.CARTITEM_NOT_FOUND));

        // 验证购物车商品属于当前用户
        if (!cartItem.getUser().getId().equals(user.getId())) {
            throw new BusinessException(ErrorTypeEnum.CARTITEM_NOT_BELONG_TO_USER);
        }
        // 验证购物车商品状态为激活
        if (cartItem.getStatus() != CartItemStatus.ACTIVE) {
            throw new BusinessException(ErrorTypeEnum.CARTITEM_STATUS_ERROR);
        }
        cartRepository.delete(cartItem);
    }

    @Override
    @Transactional
    public CartItemVO updateCartItemQuantity(String username, Long cartItemId, Integer quantity) {
        // 验证用户和购物车项是否存在
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ErrorTypeEnum.USER_NOT_FOUND));

        CartItem cartItem = cartRepository.findById(cartItemId)
                .orElseThrow(() -> new BusinessException(ErrorTypeEnum.CARTITEM_NOT_FOUND));

        // 验证购物车商品属于当前用户
        if (!cartItem.getUser().getId().equals(user.getId())) {
            throw new BusinessException(ErrorTypeEnum.CARTITEM_NOT_BELONG_TO_USER);
        }

        if (cartItem.getStatus() != CartItemStatus.ACTIVE) {
            throw new BusinessException(ErrorTypeEnum.CARTITEM_STATUS_ERROR);
        }

        // 校验库存
        Stockpile stockpile = stockpileRepository.findByProductId(cartItem.getProduct().getId())
                .orElseThrow(() -> new BusinessException(ErrorTypeEnum.STOCKPILE_NOT_FOUND));

        if (stockpile.getAmount() < quantity) {
            throw new BusinessException(ErrorTypeEnum.STOCKPILE_NOT_ENOUGH);
        }

        cartItem.setQuantity(quantity);
        cartItem = cartRepository.save(cartItem);

        return convertToCartItemVO(cartItem);
    }

    @Override
    public CartVO getCartItems(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ErrorTypeEnum.USER_NOT_FOUND));

        // 只获取状态为激活的购物车项
        List<CartItem> cartItems = cartRepository.findByUserAndStatus(user, CartItemStatus.ACTIVE);

        List<CartItemVO> cartItemVOs = cartItems.stream()
                .map(this::convertToCartItemVO)
                .collect(Collectors.toList());

        // 计算商品种类总数
        int total = cartItemVOs.size();

        // 计算商品总金额
        BigDecimal totalAmount = cartItemVOs.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartVO.builder()
                .items(cartItemVOs)
                .total(total)
                .totalAmount(totalAmount)
                .build();
    }

    /**
     * 将购物车项实体转换为视图对象
     * <p>
     * 封装购物车项实体到前端展示层所需的数据结构，
     * 提取商品的标题、封面、价格等信息，方便前端展示。
     * </p>
     * 
     * @param cartItem 要转换的购物车项实体
     * @return 转换后的购物车项视图对象
     */
    private CartItemVO convertToCartItemVO(CartItem cartItem) {
        CartItemVO vo = new CartItemVO();
        BeanUtils.copyProperties(cartItem.getProduct(), vo);
        vo.setCartItemId(cartItem.getId());
        vo.setProductId(cartItem.getProduct().getId());
        vo.setQuantity(cartItem.getQuantity());

        return vo;
    }
}