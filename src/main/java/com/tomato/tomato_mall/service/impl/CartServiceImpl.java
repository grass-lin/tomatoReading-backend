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
     * @param cartRepository      购物车数据访问对象，负责购物车数据的持久化操作
     * @param userRepository      用户数据访问对象，负责用户数据的持久化操作
     * @param productRepository   商品数据访问对象，负责商品数据的持久化操作
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
     * 将商品添加到用户的购物车中，并检查库存是否充足。
     * 如果购物车中已存在相同商品且状态为激活，则增加商品数量；
     * 否则创建新的购物车项。使用事务确保数据一致性。
     * </p>
     * 
     * @param username   用户名，用于识别购物车所属用户
     * @param cartAddDTO 购物车添加数据传输对象，包含要添加的商品信息
     * @return 添加成功的购物车项视图对象
     * @throws NoSuchElementException   当用户或商品不存在时抛出此异常
     * @throws IllegalArgumentException 当商品库存不足时抛出此异常
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
                throw new IllegalArgumentException("商品库存不足");
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

    /**
     * 从购物车中移除商品
     * <p>
     * 移除指定ID的购物车项，移除前会验证该购物车项是否属于当前用户且状态为激活。
     * 此方法执行物理删除，会从数据库中彻底移除购物车项记录。
     * </p>
     * 
     * @param username   用户名，用于识别购物车所属用户
     * @param cartItemId 要移除的购物车项ID
     * @throws NoSuchElementException   当用户或购物车项不存在时抛出此异常
     * @throws IllegalArgumentException 当购物车项不属于当前用户或状态不为激活时抛出此异常
     */
    @Override
    @Transactional
    public void removeFromCart(String username, Long cartItemId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("用户不存在"));
        
        CartItem cartItem = cartRepository.findById(cartItemId)
                .orElseThrow(() -> new NoSuchElementException("购物车商品不存在"));
        
        // 验证购物车商品属于当前用户且状态为激活
        if (!cartItem.getUser().getId().equals(user.getId()) || 
            cartItem.getStatus() != CartItemStatus.ACTIVE) {
            throw new IllegalArgumentException("无权操作该购物车商品或商品状态不允许移除");
        }
        
        cartRepository.delete(cartItem);
    }

    /**
     * 更新购物车项数量
     * <p>
     * 更新指定购物车项的商品数量，更新前会验证该购物车项是否属于当前用户且状态为激活，
     * 同时校验更新后的数量是否超过库存。使用事务确保数据一致性。
     * </p>
     * 
     * @param username   用户名，用于识别购物车所属用户
     * @param cartItemId 要更新的购物车项ID
     * @param quantity   更新后的商品数量
     * @return 更新后的购物车项视图对象
     * @throws NoSuchElementException   当用户、购物车项或库存不存在时抛出此异常
     * @throws IllegalArgumentException 当购物车项不属于当前用户、状态不为激活或库存不足时抛出此异常
     */
    @Override
    @Transactional
    public CartItemVO updateCartItemQuantity(String username, Long cartItemId, Integer quantity) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("用户不存在"));
        
        CartItem cartItem = cartRepository.findById(cartItemId)
                .orElseThrow(() -> new NoSuchElementException("购物车商品不存在"));
        
        // 验证购物车商品属于当前用户且状态为激活
        if (!cartItem.getUser().getId().equals(user.getId()) || 
            cartItem.getStatus() != CartItemStatus.ACTIVE) {
            throw new IllegalArgumentException("无权操作该购物车商品或商品状态不允许更新");
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
     * 获取用户购物车内所有商品
     * <p>
     * 查询指定用户的所有状态为激活的购物车项，并转换为前端展示所需的视图对象。
     * 此方法还会计算购物车中的商品总数和总金额，方便前端展示。
     * </p>
     * 
     * @param username 用户名，用于识别购物车所属用户
     * @return 包含购物车项列表、商品总数和总金额的购物车视图对象
     * @throws NoSuchElementException 当用户不存在时抛出此异常
     */
    @Override
    public CartVO getCartItems(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("用户不存在"));
        
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
     * 将购物车项状态设置为已结算
     * <p>
     * 批量更新指定购物车项的状态为已结算，并关联到指定订单。
     * 只有状态为激活的购物车项才能被设置为已结算，否则会抛出异常。
     * 使用事务确保数据一致性。
     * </p>
     * 
     * @param cartItemIds 要更新的购物车项ID列表
     * @param orderId     关联的订单ID
     * @throws IllegalStateException 当购物车项状态不为激活时抛出此异常
     */
    @Transactional
    @Override
    public void markCartItemsAsCheckedOut(List<Long> cartItemIds, Long orderId) {
        List<CartItem> cartItems = cartRepository.findAllById(cartItemIds);
        for (CartItem cartItem : cartItems) {
            cartItem.setStatus(CartItemStatus.CHECKED_OUT);
            cartItem.setOrderId(orderId);
        }
        cartRepository.saveAll(cartItems);
    }
    
    /**
     * 恢复已结算的购物车项到活跃状态
     * <p>
     * 将指定订单关联的所有已结算状态的购物车项恢复为活跃状态，
     * 同时移除订单关联。此方法通常在订单取消时调用，用于恢复购物车项。
     * 使用事务确保数据一致性。
     * </p>
     * 
     * @param orderId 订单ID，用于查找关联的购物车项
     */
    @Transactional
    @Override
    public void restoreCartItemsByOrderId(Long orderId) {
        List<CartItem> cartItems = cartRepository.findByOrderIdAndStatus(
                orderId, CartItemStatus.CHECKED_OUT);
        for (CartItem cartItem : cartItems) {
            cartItem.setStatus(CartItemStatus.ACTIVE);
            cartItem.setOrderId(null);
        }
        cartRepository.saveAll(cartItems);
    }
    
    /**
     * 标记订单对应的购物车项为已完成
     * <p>
     * 将指定订单关联的所有已结算状态的购物车项标记为已完成状态。
     * 此方法通常在订单完成时调用，表示购物流程已完全结束。
     * 使用事务确保数据一致性。
     * </p>
     * 
     * @param orderId 订单ID，用于查找关联的购物车项
     */
    @Transactional
    @Override
    public void markCartItemsByOrderIdAsCompleted(Long orderId) {
        List<CartItem> cartItems = cartRepository.findByOrderIdAndStatus(
                orderId, CartItemStatus.CHECKED_OUT);
        for (CartItem cartItem : cartItems) {
            cartItem.setStatus(CartItemStatus.COMPLETED);
        }
        cartRepository.saveAll(cartItems);
    }
    
    /**
     * 标记订单对应的购物车项为已取消
     * <p>
     * 将指定订单关联的所有已结算状态的购物车项标记为已取消状态。
     * 此方法通常在订单取消但不恢复购物车时调用，用于记录购物历史。
     * 使用事务确保数据一致性。
     * </p>
     * 
     * @param orderId 订单ID，用于查找关联的购物车项
     */
    @Transactional
    @Override
    public void markCartItemsByOrderIdAsCancelled(Long orderId) {
        List<CartItem> cartItems = cartRepository.findByOrderIdAndStatus(
                orderId, CartItemStatus.CHECKED_OUT);
        for (CartItem cartItem : cartItems) {
            cartItem.setStatus(CartItemStatus.CANCELLED);
        }
        cartRepository.saveAll(cartItems);
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
        vo.setCartItemId(cartItem.getId());
        vo.setProductId(cartItem.getProduct().getId());
        vo.setTitle(cartItem.getProduct().getTitle());
        vo.setCover(cartItem.getProduct().getCover());
        vo.setPrice(cartItem.getProduct().getPrice());
        vo.setQuantity(cartItem.getQuantity());
        
        return vo;
    }
}