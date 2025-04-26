package com.tomato.tomato_mall.service.impl;

import com.tomato.tomato_mall.dto.UserLoginDTO;
import com.tomato.tomato_mall.dto.UserRegisterDTO;
import com.tomato.tomato_mall.dto.UserUpdateDTO;
import com.tomato.tomato_mall.entity.User;
import com.tomato.tomato_mall.enums.ErrorTypeEnum;
import com.tomato.tomato_mall.exception.BusinessException;
import com.tomato.tomato_mall.repository.UserRepository;
import com.tomato.tomato_mall.util.JwtUtils;
import com.tomato.tomato_mall.vo.UserVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // 启用 Mockito 扩展
class UserServiceImplTest {

    @Mock // 创建 UserRepository 的模拟对象
    private UserRepository userRepository;

    @Mock // 创建 PasswordEncoder 的模拟对象
    private PasswordEncoder passwordEncoder;

    @Mock // 创建 JwtUtils 的模拟对象
    private JwtUtils jwtUtils;

    @InjectMocks // 创建 UserServiceImpl 实例，并将上面 @Mock 注解的模拟对象注入进去
    private UserServiceImpl userService;

    private UserRegisterDTO registerDTO;
    private UserLoginDTO loginDTO;
    private UserUpdateDTO updateDTO;
    private User user;
    private User adminUser;

    @BeforeEach // 每个测试方法执行前运行
    void setUp() {
        // 清除可能存在的安全上下文
        SecurityContextHolder.clearContext();

        // 准备测试数据
        registerDTO = new UserRegisterDTO();
        registerDTO.setUsername("testuser");
        registerDTO.setPassword("password123");
        registerDTO.setName("Test User");
        registerDTO.setRole("user");

        loginDTO = new UserLoginDTO();
        loginDTO.setUsername("testuser");
        loginDTO.setPassword("password123");

        updateDTO = new UserUpdateDTO();
        updateDTO.setUsername("testuser");
        updateDTO.setName("Updated Name");
        updateDTO.setRole("user"); // 假设角色不变

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("encodedPassword"); // 模拟加密后的密码
        user.setName("Test User");
        user.setRole("user");

        adminUser = new User();
        adminUser.setId(2L);
        adminUser.setUsername("admin");
        adminUser.setPassword("encodedAdminPassword");
        adminUser.setName("Admin User");
        adminUser.setRole("admin");
    }

    // --- register 方法测试 ---
    @Test
    void register_Success() {
        // --- Arrange (准备) ---
        when(userRepository.existsByUsername(registerDTO.getUsername())).thenReturn(false);
        when(passwordEncoder.encode(registerDTO.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(1L); // 模拟数据库生成ID
            return savedUser;
        });

        // --- Act (执行) ---
        UserVO result = userService.register(registerDTO);

        // --- Assert (断言) ---
        assertNotNull(result);
        assertEquals(registerDTO.getUsername(), result.getUsername());
        assertEquals(registerDTO.getName(), result.getName());
        assertEquals(registerDTO.getRole(), result.getRole());

        verify(userRepository, times(1)).existsByUsername(registerDTO.getUsername());
        verify(passwordEncoder, times(1)).encode(registerDTO.getPassword());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void register_UsernameAlreadyExists() {
        // --- Arrange (准备) ---
        when(userRepository.existsByUsername(registerDTO.getUsername())).thenReturn(true);

        // --- Act & Assert (执行并断言) ---
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.register(registerDTO);
        });

        assertEquals(ErrorTypeEnum.USERNAME_ALREADY_EXISTS, exception.getErrorType());
        verify(userRepository, times(1)).existsByUsername(registerDTO.getUsername());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    // --- login 方法测试 ---
    @Test
    void login_Success() {
        // --- Arrange ---
        when(userRepository.findByUsername(loginDTO.getUsername())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtUtils.generateToken(user.getUsername(), user.getRole())).thenReturn("mockJwtToken");

        // --- Act ---
        String token = userService.login(loginDTO);

        // --- Assert ---
        assertEquals("mockJwtToken", token);
        verify(userRepository, times(1)).findByUsername(loginDTO.getUsername());
        verify(passwordEncoder, times(1)).matches(loginDTO.getPassword(), user.getPassword());
        verify(jwtUtils, times(1)).generateToken(user.getUsername(), user.getRole());
    }

    @Test
    void login_UserNotFound() {
        // --- Arrange ---
        when(userRepository.findByUsername(loginDTO.getUsername())).thenReturn(Optional.empty());

        // --- Act & Assert ---
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.login(loginDTO);
        });

        assertEquals(ErrorTypeEnum.USER_NOT_FOUND, exception.getErrorType());
        verify(userRepository, times(1)).findByUsername(loginDTO.getUsername());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtUtils, never()).generateToken(anyString(), anyString());
    }

    @Test
    void login_IncorrectPassword() {
        // --- Arrange ---
        when(userRepository.findByUsername(loginDTO.getUsername())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())).thenReturn(false);

        // --- Act & Assert ---
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.login(loginDTO);
        });

        assertEquals(ErrorTypeEnum.INCORRECT_PASSWORD, exception.getErrorType());
        verify(userRepository, times(1)).findByUsername(loginDTO.getUsername());
        verify(passwordEncoder, times(1)).matches(loginDTO.getPassword(), user.getPassword());
        verify(jwtUtils, never()).generateToken(anyString(), anyString());
    }

    // --- getUserByUsername 方法测试 ---
    @Test
    void getUserByUsername_Success_Self() {
        // --- Arrange ---
        // 模拟当前登录用户
        mockSecurityContext(user.getUsername(), "ROLE_user");
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

        // --- Act ---
        UserVO result = userService.getUserByUsername(user.getUsername());

        // --- Assert ---
        assertNotNull(result);
        assertEquals(user.getUsername(), result.getUsername());
        assertEquals(user.getName(), result.getName());
        verify(userRepository, times(1)).findByUsername(user.getUsername());
    }

    @Test
    void getUserByUsername_UserNotFound() {
        // --- Arrange ---
        mockSecurityContext("nonexistentuser", "ROLE_user");
        when(userRepository.findByUsername("nonexistentuser")).thenReturn(Optional.empty());

        // --- Act & Assert ---
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.getUserByUsername("nonexistentuser");
        });
        assertEquals(ErrorTypeEnum.USER_NOT_FOUND, exception.getErrorType());

        // 验证 userRepository.findByUsername 被调用了一次
        verify(userRepository, times(1)).findByUsername("nonexistentuser");
    }

    @Test
    void getUserByUsername_AccessDenied() {
        // --- Arrange ---
        mockSecurityContext("anotheruser", "ROLE_user"); // 当前登录用户是 anotheruser

        // --- Act & Assert ---
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.getUserByUsername(user.getUsername()); // 尝试获取其他用户的信息
        });

        assertEquals(ErrorTypeEnum.USER_NOT_BELONG_TO_USER, exception.getErrorType());
        // userRepository.findByUsername 不应该被调用，因为权限检查在前
        verify(userRepository, never()).findByUsername(user.getUsername());
    }

    // --- updateUser 方法测试 ---
    @Test
    void updateUser_Success_Self() {
        // --- Arrange ---
        mockSecurityContext(user.getUsername(), "ROLE_user");
        when(userRepository.findByUsername(updateDTO.getUsername())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0)); // 保存时返回传入的对象

        // --- Act ---
        UserVO result = userService.updateUser(updateDTO);

        // --- Assert ---
        assertNotNull(result);
        assertEquals(updateDTO.getUsername(), result.getUsername());
        assertEquals(updateDTO.getName(), result.getName()); // 验证姓名已更新
        assertEquals(updateDTO.getRole(), result.getRole());

        verify(userRepository, times(1)).findByUsername(updateDTO.getUsername());
        // 验证 save 被调用，并且传入的 User 对象包含了更新后的 name
        verify(userRepository, times(1)).save(argThat(savedUser -> savedUser.getName().equals(updateDTO.getName()) &&
                savedUser.getUsername().equals(updateDTO.getUsername())));
        verify(passwordEncoder, never()).encode(anyString()); // 验证密码未被更新
    }

    @Test
    void updateUser_Success_AdminUpdatesUser() {
        // --- Arrange ---
        mockSecurityContext(adminUser.getUsername(), "ROLE_admin"); // 当前是管理员
        when(userRepository.findByUsername(updateDTO.getUsername())).thenReturn(Optional.of(user)); // 管理员更新 testuser
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // --- Act ---
        UserVO result = userService.updateUser(updateDTO);

        // --- Assert ---
        assertNotNull(result);
        assertEquals(updateDTO.getUsername(), result.getUsername());
        assertEquals(updateDTO.getName(), result.getName());

        verify(userRepository, times(1)).findByUsername(updateDTO.getUsername());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUser_AdminUserNotFound() {
        // --- Arrange ---
        mockSecurityContext(adminUser.getUsername(), "ROLE_admin");
        updateDTO.setUsername("nonexistentuser"); // 尝试更新不存在的用户
        when(userRepository.findByUsername("nonexistentuser")).thenReturn(Optional.empty());

        // --- Act & Assert ---
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.updateUser(updateDTO);
        });

        assertEquals(ErrorTypeEnum.USER_NOT_FOUND, exception.getErrorType());
        verify(userRepository, times(1)).findByUsername("nonexistentuser");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUser_AccessDenied() {
        // --- Arrange ---
        mockSecurityContext("anotheruser", "ROLE_user"); // 当前用户是 anotheruser
        // updateDTO 的 username 是 "testuser"

        // --- Act & Assert ---
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.updateUser(updateDTO); // anotheruser 尝试更新 testuser
        });

        assertEquals(ErrorTypeEnum.USER_NOT_BELONG_TO_USER, exception.getErrorType());
        // userRepository.findByUsername 不应该被调用，因为权限检查在前
        verify(userRepository, never()).findByUsername(updateDTO.getUsername());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUser_WithPasswordChange() {
        // --- Arrange ---
        mockSecurityContext(user.getUsername(), "ROLE_user");
        updateDTO.setPassword("newPassword123"); // 设置新密码
        when(userRepository.findByUsername(updateDTO.getUsername())).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(updateDTO.getPassword())).thenReturn("encodedNewPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // --- Act ---
        UserVO result = userService.updateUser(updateDTO);

        // --- Assert ---
        assertNotNull(result);
        verify(userRepository, times(1)).findByUsername(updateDTO.getUsername());
        verify(passwordEncoder, times(1)).encode("newPassword123"); // 验证密码编码被调用
        // 验证保存的用户密码是新的加密密码
        verify(userRepository, times(1))
                .save(argThat(savedUser -> savedUser.getPassword().equals("encodedNewPassword")));
    }

    // --- Helper method to mock SecurityContext ---
    private void mockSecurityContext(String username, String role) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                username, "password", Collections.singletonList(() -> role));
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }
}