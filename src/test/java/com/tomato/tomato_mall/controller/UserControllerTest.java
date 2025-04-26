package com.tomato.tomato_mall.controller;

import com.tomato.tomato_mall.dto.UserLoginDTO;
import com.tomato.tomato_mall.dto.UserRegisterDTO;
import com.tomato.tomato_mall.dto.UserUpdateDTO;
import com.tomato.tomato_mall.service.UserService;
import com.tomato.tomato_mall.service.AuthenticationService;
import com.tomato.tomato_mall.vo.ResponseVO;
import com.tomato.tomato_mall.vo.UserVO;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // 启用 Mockito 扩展
class UserControllerTest {

  @Mock // 模拟 UserService
  private UserService userService;

  @Mock // 模拟 AuthenticationService
  private AuthenticationService authenticationService;

  @Mock // 模拟 HttpServletResponse (用于 login 和 logout)
  private HttpServletResponse httpServletResponse;

  @InjectMocks // 创建 UserController 实例，并注入上面的 Mock 对象
  private UserController userController;

  private UserRegisterDTO registerDTO;
  private UserLoginDTO loginDTO;
  private UserUpdateDTO updateDTO;
  private UserVO userVO;

  @BeforeEach
  void setUp() {
    registerDTO = new UserRegisterDTO();
    registerDTO.setUsername("testuser");
    registerDTO.setPassword("password");
    registerDTO.setName("Test User");
    registerDTO.setRole("user");

    loginDTO = new UserLoginDTO();
    loginDTO.setUsername("testuser");
    loginDTO.setPassword("password");

    updateDTO = new UserUpdateDTO();
    updateDTO.setUsername("testuser");
    updateDTO.setName("Updated Name");

    userVO = new UserVO();
    userVO.setUsername("testuser");
    userVO.setName("Test User");
    userVO.setRole("user");
  }

  @Test
  void register_Success() {
    // --- Arrange ---
    when(userService.register(any(UserRegisterDTO.class))).thenReturn(userVO);

    // --- Act ---
    ResponseEntity<ResponseVO<String>> response = userController.register(registerDTO);

    // --- Assert ---
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    ResponseVO<String> body = response.getBody();
    assertNotNull(body);
    assertEquals(200, body.getCode()); // 验证业务码
    assertEquals("注册成功", body.getData()); // 验证响应消息
    verify(userService, times(1)).register(eq(registerDTO));
  }

  @Test
  void login_Success() {
    // --- Arrange ---
    String mockToken = "mockJwtToken";
    when(userService.login(any(UserLoginDTO.class))).thenReturn(mockToken);
    // 假设 setAuthenticationCookie 无返回值 (void)
    doNothing().when(authenticationService).setAuthenticationCookie(anyString(), any(HttpServletResponse.class));

    // --- Act ---
    ResponseEntity<ResponseVO<String>> response = userController.login(loginDTO, httpServletResponse);

    // --- Assert ---
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    ResponseVO<String> body = response.getBody();
    assertNotNull(body);
    assertEquals(200, body.getCode()); // 验证业务码
    assertEquals(mockToken, body.getData()); // 验证返回的 token

    // 验证 userService.login 被调用
    verify(userService, times(1)).login(eq(loginDTO));
    // 验证 authenticationService.setAuthenticationCookie 被调用
    verify(authenticationService, times(1)).setAuthenticationCookie(eq(mockToken), eq(httpServletResponse));
  }

  @Test
  void getUserDetail_Success() {
    // --- Arrange ---
    String username = "testuser";
    when(userService.getUserByUsername(username)).thenReturn(userVO);

    // --- Act ---
    ResponseEntity<ResponseVO<UserVO>> response = userController.getUserDetail(username, null); // 传入 null request

    // --- Assert ---
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    ResponseVO<UserVO> body = response.getBody();
    assertNotNull(body);
    assertEquals(200, body.getCode());
    assertEquals(userVO, body.getData()); // 验证返回的 UserVO

    // 验证 userService.getUserByUsername 被调用
    verify(userService, times(1)).getUserByUsername(eq(username));
  }

  @Test
  void updateUser_Success() {
    // --- Arrange ---
    when(userService.updateUser(any(UserUpdateDTO.class))).thenReturn(userVO); // 如果 Controller 返回 UserVO

    // --- Act ---
    ResponseEntity<ResponseVO<String>> response = userController.updateUser(updateDTO);

    // --- Assert ---
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    ResponseVO<String> body = response.getBody();
    assertNotNull(body);
    assertEquals(200, body.getCode());
    assertEquals("更新成功", body.getData());

    // 验证 userService.updateUser 被调用
    verify(userService, times(1)).updateUser(eq(updateDTO));
  }

  @Test
  void logout_Success() {
    // --- Arrange ---
    doNothing().when(authenticationService).clearAuthenticationCookie(any(HttpServletResponse.class));

    // --- Act ---
    ResponseEntity<ResponseVO<String>> response = userController.logout(httpServletResponse);

    // --- Assert ---
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    ResponseVO<String> body = response.getBody();
    assertNotNull(body);
    assertEquals(200, body.getCode());
    assertEquals("登出成功", body.getData());

    // 验证 authenticationService.clearAuthenticationCookie 被调用
    verify(authenticationService, times(1)).clearAuthenticationCookie(eq(httpServletResponse));
  }

  @Test
  void getUsers_Success() {
    // --- Arrange ---
    UserVO user1 = new UserVO();
    user1.setUsername("user1");
    UserVO user2 = new UserVO();
    user2.setUsername("user2");
    List<UserVO> userList = Arrays.asList(user1, user2);
    when(userService.getAllUsers()).thenReturn(userList);

    // --- Act ---
    ResponseEntity<ResponseVO<List<UserVO>>> response = userController.getUsers();

    // --- Assert ---
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    ResponseVO<List<UserVO>> body = response.getBody();
    assertNotNull(body);
    assertEquals(200, body.getCode());
    assertEquals(userList, body.getData()); // 验证返回的用户列表
    assertEquals(2, body.getData().size());

    // 验证 userService.getAllUsers 被调用
    verify(userService, times(1)).getAllUsers();
  }
}