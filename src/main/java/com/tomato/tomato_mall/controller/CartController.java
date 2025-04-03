package com.tomato.tomato_mall.controller;

import com.tomato.tomato_mall.dto.CartAddDTO;
import com.tomato.tomato_mall.dto.CartUpdateDTO;
import com.tomato.tomato_mall.dto.CheckoutDTO;
import com.tomato.tomato_mall.service.CartService;
import com.tomato.tomato_mall.service.OrderService;
import com.tomato.tomato_mall.vo.CartItemVO;
import com.tomato.tomato_mall.vo.CartVO;
import com.tomato.tomato_mall.vo.OrderVO;
import com.tomato.tomato_mall.vo.ResponseVO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * 购物车控制器
 * <p>
 * 提供购物车的增删改查REST API接口
 * 所有接口返回统一的ResponseVO格式，包含状态码、消息和数据
 * </p>
 *
 * @author Team CBDDL
 * @version 1.0
 */
@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;
    private final OrderService orderService;

    /**
     * 构造函数，通过依赖注入初始化服务
     *
     * @param cartService  购物车服务，处理购物车相关业务逻辑
     * @param orderService 订单服务，处理订单相关业务逻辑
     */
    public CartController(CartService cartService, OrderService orderService) {
        this.cartService = cartService;
        this.orderService = orderService;
    }

    /**
     * 添加商品到购物车接口
     * <p>
     * 将指定商品添加到当前登录用户的购物车中
     * </p>
     *
     * @param cartAddDTO 购物车添加数据传输对象，包含商品ID和数量
     * @return 返回包含添加后购物车商品信息的响应体，状态码200
     */
    @PostMapping
    public ResponseEntity<ResponseVO<CartItemVO>> addToCart(@Valid @RequestBody CartAddDTO cartAddDTO) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        CartItemVO cartItemVO = cartService.addToCart(username, cartAddDTO);
        return ResponseEntity.ok(ResponseVO.success(cartItemVO));
    }

    /**
     * 删除购物车商品接口
     * <p>
     * 从当前登录用户的购物车中删除指定商品
     * </p>
     *
     * @param cartItemId 购物车商品ID
     * @return 返回成功消息的响应体，状态码200
     */
    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<ResponseVO<String>> removeFromCart(@PathVariable Long cartItemId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        cartService.removeFromCart(username, cartItemId);
        return ResponseEntity.ok(ResponseVO.success("删除成功"));
    }

    /**
     * 修改购物车商品数量接口
     * <p>
     * 更新当前登录用户购物车中指定商品的数量
     * </p>
     *
     * @param cartItemId    购物车商品ID
     * @param cartUpdateDTO 购物车更新数据传输对象，包含新的商品数量
     * @return 返回包含更新后购物车商品信息的响应体，状态码200
     */
    @PatchMapping("/{cartItemId}")
    // Bad practice
    public ResponseEntity<ResponseVO<String>> updateCartItemQuantity(
            @PathVariable Long cartItemId,
            @Valid @RequestBody CartUpdateDTO cartUpdateDTO) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        cartService.updateCartItemQuantity(username, cartItemId, cartUpdateDTO.getQuantity());
        return ResponseEntity.ok(ResponseVO.success("修改数量成功"));
    }
    // public ResponseEntity<ResponseVO<CartItemVO>> updateCartItemQuantity(
    // @PathVariable Long cartItemId,
    // @Valid @RequestBody CartUpdateDTO cartUpdateDTO) {
    // String username =
    // SecurityContextHolder.getContext().getAuthentication().getName();
    // CartItemVO cartItemVO = cartService.updateCartItemQuantity(username,
    // cartItemId, cartUpdateDTO.getQuantity());
    // return ResponseEntity.ok(ResponseVO.success(cartItemVO));
    // }

    /**
     * 获取购物车信息接口
     * <p>
     * 返回当前登录用户的购物车信息，包括商品列表、商品种类总数和总金额
     * </p>
     *
     * @return 返回包含购物车信息的响应体，状态码200
     */
    @GetMapping
    public ResponseEntity<ResponseVO<CartVO>> getCartItems() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        CartVO cartVO = cartService.getCartItems(username);
        return ResponseEntity.ok(ResponseVO.success(cartVO));
    }

    /**
     * 结算购物车接口（创建订单）
     * <p>
     * 从购物车中选择商品创建订单，验证库存，锁定库存，计算金额
     * 支付流程的第一步
     * </p>
     * 
     * @param checkoutDTO 结算信息数据传输对象，包含购物车商品ID、配送地址和支付方式
     * @return 返回包含订单信息的响应体，状态码200
     * @throws IllegalArgumentException         当库存不足或数据无效时抛出
     * @throws java.util.NoSuchElementException 当用户或商品不存在时抛出
     */
    @PostMapping("/checkout")
    public ResponseEntity<ResponseVO<OrderVO>> checkout(@Valid @RequestBody CheckoutDTO checkoutDTO) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        OrderVO orderVO = orderService.createOrder(username, checkoutDTO);
        return ResponseEntity.ok(ResponseVO.success(orderVO));
    }
}