package com.tomato.tomato_mall.service.impl;

import com.tomato.tomato_mall.dto.CartAddDTO;
import com.tomato.tomato_mall.entity.CartItem;
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

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 购物车服务实现类
 * <p>
 * 该类实现了{@link CartService}接口，提供购物车的添加、查询、更新和删除等核心功能。
 * 包含购物车商品管理和库存校验等业务逻辑的具体实现。
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
     * @param cartRepository 购物车数据访问对象，负责购物车数据的持久化操作
     * @param userRepository 用户数据访问对象，负责用户数据的持久化操作
     * @param productRepository 商品数据访问对象，负责商品数据的持久化操作
     * @param stockpileRepository 库存数据访问对象，负责库存数据的持久化操作
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

    /**
     * 添加商品到购物车
     * <p>
     * 将商品添加到用户购物车，如果已存在则更新数量。添加前会验证库存是否充足。
     * 使用事务确保数据一致性。
     * </p>
     *
     * @param username 用户名
     * @param cartAddDTO 购物车添加数据传输对象
     * @return 添加后的购物车商品视图对象
     * @throws NoSuchElementException 当用户或商品不存在时抛出此异常
     * @throws IllegalArgumentException 当库存不足时抛出此异常
     */
    @Override
    @Transactional
    public CartItemVO addToCart(String username, CartAddDTO cartAddDTO) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("用户不存在"));
        
        Product product = productRepository.findById(cartAddDTO.getProductId())
                .orElseThrow(() -> new NoSuchElementException("商品不存在"));
        
        // 校验库存
        Stockpile stockpile = stockpileRepository.findByProductId(product.getId())
                .orElseThrow(() -> new NoSuchElementException("商品库存不存在"));
        
        if (stockpile.getAmount() < cartAddDTO.getQuantity()) {
            throw new IllegalArgumentException("商品库存不足");
        }
        
        // 检查购物车是否已有该商品
        Optional<CartItem> existingCartItem = cartRepository.findByUserAndProduct(user, product);
        
        CartItem cartItem;
        if (existingCartItem.isPresent()) {
            // 已有该商品，更新数量
            cartItem = existingCartItem.get();
            int newQuantity = cartItem.getQuantity() + cartAddDTO.getQuantity();
            
            // 再次校验库存
            if (stockpile.getAmount() < newQuantity) {
                throw new IllegalArgumentException("商品库存不足");
            }
            
            cartItem.setQuantity(newQuantity);
        } else {
            // 新增商品到购物车
            cartItem = new CartItem();
            cartItem.setUser(user);
            cartItem.setProduct(product);
            cartItem.setQuantity(cartAddDTO.getQuantity());
        }
        
        cartItem = cartRepository.save(cartItem);
        return convertToCartItemVO(cartItem);
    }

    /**
     * 从购物车中删除商品
     * <p>
     * 删除指定的购物车商品项。删除前会验证购物车商品是否属于当前用户。
     * 使用事务确保数据一致性。
     * </p>
     *
     * @param username 用户名
     * @param cartItemId 购物车商品ID
     * @throws NoSuchElementException 当用户或购物车商品不存在时抛出此异常
     * @throws IllegalArgumentException 当购物车商品不属于当前用户时抛出此异常
     */
    @Override
    @Transactional
    public void removeFromCart(String username, Long cartItemId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("用户不存在"));
        
        CartItem cartItem = cartRepository.findById(cartItemId)
                .orElseThrow(() -> new NoSuchElementException("购物车商品不存在"));
        
        // 验证购物车商品属于当前用户
        if (!cartItem.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("无权操作该购物车商品");
        }
        
        cartRepository.delete(cartItem);
    }

    /**
     * 更新购物车商品数量
     * <p>
     * 修改购物车中指定商品的数量。更新前会验证购物车商品是否属于当前用户，
     * 并且会检查新的数量是否超出库存限制。使用事务确保数据一致性。
     * </p>
     *
     * @param username 用户名
     * @param cartItemId 购物车商品ID
     * @param quantity 新的商品数量
     * @return 更新后的购物车商品视图对象
     * @throws NoSuchElementException 当用户或购物车商品不存在时抛出此异常
     * @throws IllegalArgumentException 当购物车商品不属于当前用户或库存不足时抛出此异常
     */
    @Override
    @Transactional
    public CartItemVO updateCartItemQuantity(String username, Long cartItemId, Integer quantity) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("用户不存在"));
        
        CartItem cartItem = cartRepository.findById(cartItemId)
                .orElseThrow(() -> new NoSuchElementException("购物车商品不存在"));
        
        // 验证购物车商品属于当前用户
        if (!cartItem.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("无权操作该购物车商品");
        }
        
        // 校验库存
        Stockpile stockpile = stockpileRepository.findByProductId(cartItem.getProduct().getId())
                .orElseThrow(() -> new NoSuchElementException("商品库存不存在"));
        
        if (stockpile.getAmount() < quantity) {
            throw new IllegalArgumentException("商品库存不足");
        }
        
        cartItem.setQuantity(quantity);
        cartItem = cartRepository.save(cartItem);
        
        return convertToCartItemVO(cartItem);
    }

    /**
     * 获取用户的购物车信息
     * <p>
     * 查询指定用户的购物车信息，包括商品列表、商品种类总数和总金额。
     * </p>
     *
     * @param username 用户名
     * @return 包含商品列表、商品种类总数和总金额的购物车视图对象
     * @throws NoSuchElementException 当用户不存在时抛出此异常
     */
    @Override
    public CartVO getCartItems(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("用户不存在"));
        
        List<CartItem> cartItems = cartRepository.findByUser(user);
        
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
     * 将购物车商品实体转换为视图对象
     * <p>
     * 封装购物车商品实体到前端展示层所需的数据结构，包括计算商品总价。
     * </p>
     *
     * @param cartItem 购物车商品实体
     * @return 购物车商品视图对象
     */
    private CartItemVO convertToCartItemVO(CartItem cartItem) {
        CartItemVO vo = new CartItemVO();
        vo.setCartItemId(cartItem.getId());
        vo.setProductId(cartItem.getProduct().getId());
        vo.setTitle(cartItem.getProduct().getTitle());
        vo.setCover(cartItem.getProduct().getCover());
        vo.setPrice(cartItem.getProduct().getPrice());
        vo.setQuantity(cartItem.getQuantity());
        
        return vo;
    }
}