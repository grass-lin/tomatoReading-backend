package com.tomato.tomato_mall.controller;

import com.tomato.tomato_mall.dto.UserLoginDTO;
import com.tomato.tomato_mall.dto.UserRegisterDTO;
import com.tomato.tomato_mall.dto.UserUpdateDTO;
import com.tomato.tomato_mall.service.UserService;
import com.tomato.tomato_mall.vo.ResponseVO;
import com.tomato.tomato_mall.vo.UserVO;
import com.tomato.tomato_mall.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * 用户账户控制器
 * <p>
 * 提供用户注册、登录、查询和更新等功能的REST API接口
 * 所有接口返回统一的ResponseVO格式，包含状态码、消息和数据
 * </p>
 * 
 * @author Team CBDDL
 * @version 1.0
 */
@RestController
@RequestMapping("/api/accounts")
public class UserController {

    private final UserService userService;
    private final AuthenticationService authenticationService;

    /**
     * 构造函数，通过依赖注入初始化服务
     * 
     * @param userService           用户服务，处理用户相关业务逻辑
     * @param authenticationService 认证服务，处理令牌和Cookie相关操作
     */
    public UserController(UserService userService, AuthenticationService authenticationService) {
        this.userService = userService;
        this.authenticationService = authenticationService;
    }

    /**
     * 用户注册接口
     * <p>
     * 处理用户注册请求，验证输入数据并创建新用户
     * </p>
     * 
     * @param registerDTO 注册信息数据传输对象，包含用户名、密码等注册信息
     * @return 返回包含新创建用户信息的响应体，状态码201
     * @throws UsernameAlreadyExistsException 当用户名已存在时抛出
     */
    @PostMapping
    public ResponseEntity<ResponseVO<String>> register(@Valid @RequestBody UserRegisterDTO registerDTO) {
        userService.register(registerDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseVO.success("注册成功"));
    }
    // public ResponseEntity<ResponseVO<UserVO>> register(@Valid @RequestBody UserRegisterDTO registerDTO) {
    //     UserVO userVO = userService.register(registerDTO);
    //     return ResponseEntity.status(HttpStatus.CREATED)
    //             .body(ResponseVO.success(userVO));
    // }

    /**
     * 用户登录接口
     * <p>
     * 验证用户凭据，生成JWT令牌并设置到Cookie中
     * </p>
     * 
     * @param loginDTO 登录信息数据传输对象，包含用户名和密码
     * @param response HTTP响应对象，用于设置认证Cookie
     * @return 返回包含用户信息的响应体，状态码200
     * @throws BadCredentialsException 当用户名或密码不正确时抛出
     * @throws NoSuchElementException  当用户不存在时抛出
     */
    @PostMapping("/login")
    public ResponseEntity<ResponseVO<String>> login(
            @Valid @RequestBody UserLoginDTO loginDTO,
            HttpServletResponse response) {
        String token = userService.login(loginDTO);
        authenticationService.setAuthenticationCookie(token, response);
        return ResponseEntity.ok(ResponseVO.success(token));
    }
    // public ResponseEntity<ResponseVO<UserVO>> login(
    //         @Valid @RequestBody UserLoginDTO loginDTO,
    //         HttpServletResponse response) {
    //     String token = userService.login(loginDTO);
    //     authenticationService.setAuthenticationCookie(token, response);
    //     UserVO userVO = userService.getUserByUsername(loginDTO.getUsername());
    //     return ResponseEntity.ok(ResponseVO.success(userVO));
    // }

    /**
     * 获取用户详情接口
     * <p>
     * 根据用户名查询并返回用户详细信息
     * </p>
     * 
     * @param username 要查询的用户名
     * @param request  HTTP请求对象
     * @return 返回包含用户信息的响应体，状态码200
     * @throws NoSuchElementException 当用户不存在时抛出
     */
    @GetMapping("/{username}")
    public ResponseEntity<ResponseVO<UserVO>> getUserDetail(@PathVariable String username,
            HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        if (!currentUsername.equals(username)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ResponseVO.error(403, "You can only get your own profile"));
        }

        UserVO userVO = userService.getUserByUsername(username);
        return ResponseEntity.ok(ResponseVO.success(userVO));
    }

    /**
     * 更新用户信息接口
     * <p>
     * 更新当前登录用户的个人信息，包括密码、头像等
     * 执行安全检查确保用户只能更新自己的信息
     * </p>
     * 
     * @param updateDTO 用户更新信息数据传输对象
     * @return 返回包含更新后用户信息的响应体，状态码200；或返回错误信息，状态码403
     * @throws NoSuchElementException 当用户不存在时抛出
     */
    @PutMapping
    public ResponseEntity<ResponseVO<String>> updateUser(
            @Valid @RequestBody UserUpdateDTO updateDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        if (!currentUsername.equals(updateDTO.getUsername())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ResponseVO.error(403, "You can only update your own profile"));
        }

        userService.updateUser(updateDTO);
        return ResponseEntity.ok(ResponseVO.success("更新成功"));
    }
    // public ResponseEntity<ResponseVO<UserVO>> updateUser(
    //         @Valid @RequestBody UserUpdateDTO updateDTO) {
    //     Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    //     String currentUsername = authentication.getName();

    //     if (!currentUsername.equals(updateDTO.getUsername())) {
    //         return ResponseEntity.status(HttpStatus.FORBIDDEN)
    //                 .body(ResponseVO.error(403, "You can only update your own profile"));
    //     }

    //     UserVO updatedUser = userService.updateUser(updateDTO);
    //     return ResponseEntity.ok(ResponseVO.success(updatedUser));
    // }
}