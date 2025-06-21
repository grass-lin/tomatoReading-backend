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

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 * <p>
 * 提供用户注册、登录、查询和更新等功能的REST API接口
 * 所有接口返回统一的ResponseVO格式，包含状态码、消息和数据
 * 管理员拥有获取所有用户信息的权限
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
     */
    @PostMapping
    // Bad Practice
    public ResponseEntity<ResponseVO<String>> register(@Valid @RequestBody UserRegisterDTO registerDTO) {
        userService.register(registerDTO);
        return ResponseEntity.ok(ResponseVO.success("注册成功"));
    }
    // public ResponseEntity<ResponseVO<UserVO>> register(@Valid @RequestBody
    // UserRegisterDTO registerDTO) {
    // UserVO userVO = userService.register(registerDTO);
    // return ResponseEntity.status(HttpStatus.CREATED)
    // .body(ResponseVO.success(userVO));
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
     */
    @PostMapping("/login")
    // Bad Practice
    public ResponseEntity<ResponseVO<String>> login(
            @Valid @RequestBody UserLoginDTO loginDTO,
            HttpServletResponse response) {
        String token = userService.login(loginDTO);
        authenticationService.setAuthenticationCookie(token, response);
        return ResponseEntity.ok(ResponseVO.success(token));
    }
    // public ResponseEntity<ResponseVO<UserVO>> login(
    // @Valid @RequestBody UserLoginDTO loginDTO,
    // HttpServletResponse response) {
    // String token = userService.login(loginDTO);
    // authenticationService.setAuthenticationCookie(token, response);
    // UserVO userVO = userService.getUserByUsername(loginDTO.getUsername());
    // return ResponseEntity.ok(ResponseVO.success(userVO));
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
     */
    @GetMapping("/{username}")
    public ResponseEntity<ResponseVO<UserVO>> getUserDetail(@PathVariable String username,
            HttpServletRequest request) {
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
     */
    @PutMapping
    // Bad Practice
    public ResponseEntity<ResponseVO<String>> updateUser(
            @Valid @RequestBody UserUpdateDTO updateDTO) {
        userService.updateUser(updateDTO);
        return ResponseEntity.ok(ResponseVO.success("更新成功"));
    }
    // public ResponseEntity<ResponseVO<UserVO>> updateUser(
    // @Valid @RequestBody UserUpdateDTO updateDTO) {
    // Authentication authentication =
    // SecurityContextHolder.getContext().getAuthentication();
    // String currentUsername = authentication.getName();

    // if (!currentUsername.equals(updateDTO.getUsername())) {
    // return ResponseEntity.status(HttpStatus.FORBIDDEN)
    // .body(ResponseVO.error(403, "You can only update your own profile"));
    // }

    // UserVO updatedUser = userService.updateUser(updateDTO);
    // return ResponseEntity.ok(ResponseVO.success(updatedUser));
    // }

    /**
     * 用户登出接口
     * <p>
     * 清除用户的认证Cookie，使当前会话无效
     * </p>
     *
     * @param response HTTP响应对象，用于清除认证Cookie
     * @return 返回包含登出成功消息的响应体，状态码200
     */
    @PostMapping("/logout")
    public ResponseEntity<ResponseVO<String>> logout(HttpServletResponse response) {
        authenticationService.clearAuthenticationCookie(response);

        return ResponseEntity.ok(ResponseVO.success("登出成功"));
    }

    /**
     * 获取所有用户信息接口
     * <p>
     * 仅管理员访问
     * </p>
     *
     * @return 返回包含所有用户信息列表的响应体，状态码200
     */
    @GetMapping
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<ResponseVO<List<UserVO>>> getUsers() {
        List<UserVO> users = userService.getAllUsers();
        return ResponseEntity.ok(ResponseVO.success(users));
    }
}