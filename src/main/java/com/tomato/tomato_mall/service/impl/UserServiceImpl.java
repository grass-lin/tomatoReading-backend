package com.tomato.tomato_mall.service.impl;

import com.tomato.tomato_mall.dto.UserLoginDTO;
import com.tomato.tomato_mall.dto.UserRegisterDTO;
import com.tomato.tomato_mall.dto.UserUpdateDTO;
import com.tomato.tomato_mall.entity.User;
import com.tomato.tomato_mall.exception.BusinessException;
import com.tomato.tomato_mall.repository.UserRepository;
import com.tomato.tomato_mall.service.UserService;
import com.tomato.tomato_mall.vo.UserVO;
import com.tomato.tomato_mall.util.JwtUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.tomato.tomato_mall.enums.ErrorTypeEnum;

import java.util.NoSuchElementException;

/**
 * 用户服务实现类
 * <p>
 * 该类实现了{@link UserService}接口，提供用户注册、登录、查询和更新等核心功能。
 * 包含用户身份验证、密码加密、JWT令牌生成等业务逻辑的具体实现。
 * </p>
 * 
 * @author Team CBDDL
 * @version 1.0
 */
@Service
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtUtils jwtUtils;

  /**
   * 构造函数，通过依赖注入初始化用户服务组件
   * 
   * @param userRepository  用户数据访问对象，负责用户数据的持久化操作
   * @param passwordEncoder 密码编码器，用于密码的加密和校验
   * @param jwtUtils        JWT工具类，提供令牌生成和解析功能
   */
  public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtils jwtUtils) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtUtils = jwtUtils;
  }

  /**
   * 用户注册方法
   * <p>
   * 创建新用户账户，对密码进行加密存储，并为用户分配默认角色。
   * </p>
   * 
   * @param registerDTO 用户注册数据传输对象，包含注册所需信息
   * @return 注册成功的用户视图对象，不包含敏感信息
   * @throws UsernameBusinessException 当用户名已被占用时抛出此异常
   */
  @Override
  @Transactional
  public UserVO register(UserRegisterDTO registerDTO) {
    if (userRepository.existsByUsername(registerDTO.getUsername())) {
      throw new BusinessException(ErrorTypeEnum.USERNAME_ALREADY_EXISTS);
      // throw new UsernameBusinessException("Username already exists");
    }

    User user = new User();
    BeanUtils.copyProperties(registerDTO, user);
    user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));

    User savedUser = userRepository.save(user);

    UserVO userVO = new UserVO();
    BeanUtils.copyProperties(savedUser, userVO);
    return userVO;
  }

  /**
   * 用户登录方法
   * <p>
   * 验证用户凭据，若验证成功则生成JWT令牌返回给客户端。
   * </p>
   * 
   * @param loginDTO 用户登录数据传输对象，包含用户名和密码
   * @return 认证成功后生成的JWT令牌
   * @throws NoSuchElementException  当用户不存在时抛出此异常
   * @throws BadCredentialsException 当密码不正确时抛出此异常
   */
  @Override
  public String login(UserLoginDTO loginDTO) {
    User user = userRepository.findByUsername(loginDTO.getUsername())
        .orElseThrow(() -> new BusinessException(ErrorTypeEnum.USER_NOT_FOUND));
        // .orElseThrow(() -> new NoSuchElementException("User not found"));

    if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
      throw new BusinessException(ErrorTypeEnum.INCORRECT_PASSWORD);
      // throw new BadCredentialsException("Invalid password");
    }

    return generateJwtToken(user);
  }

  /**
   * 根据用户名获取用户信息
   * 
   * @param username 要查询的用户名
   * @return 用户视图对象，包含用户公开信息
   * @throws NoSuchElementException 当用户不存在时抛出此异常
   */
  @Override
  public UserVO getUserByUsername(String username) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUsername = authentication.getName();

    if (!currentUsername.equals(username)) {
      throw new AccessDeniedException("");
    }

    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new BusinessException(ErrorTypeEnum.USER_NOT_FOUND));
        // .orElseThrow(() -> new NoSuchElementException("User not found"));

    UserVO userVO = new UserVO();
    BeanUtils.copyProperties(user, userVO);
    return userVO;
  }

  /**
   * 更新用户信息
   * <p>
   * 根据传入的更新数据，有选择地更新用户信息。
   * 只会更新非空字段，保持其他字段不变。
   * 若更新密码，会对新密码进行加密处理。
   * </p>
   * 
   * @param updateDTO 用户更新数据传输对象
   * @return 更新后的用户视图对象
   * @throws NoSuchElementException 当要更新的用户不存在时抛出此异常
   */
  @Override
  @Transactional
  public UserVO updateUser(UserUpdateDTO updateDTO) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUsername = authentication.getName();

    if (!currentUsername.equals(updateDTO.getUsername())) {
      throw new AccessDeniedException("");
    }
    User user = userRepository.findByUsername(updateDTO.getUsername())
        .orElseThrow(() -> new BusinessException(ErrorTypeEnum.USER_NOT_FOUND));
        // .orElseThrow(() -> new NoSuchElementException("User not found"));

    if (updateDTO.getPassword() != null && !updateDTO.getPassword().isEmpty()) {
      user.setPassword(passwordEncoder.encode(updateDTO.getPassword()));
    }

    if (updateDTO.getName() != null) {
      user.setName(updateDTO.getName());
    }

    if (updateDTO.getAvatar() != null) {
      user.setAvatar(updateDTO.getAvatar());
    }

    if(updateDTO.getRole() != null) {
      user.setRole(updateDTO.getRole());
    }

    if (updateDTO.getTelephone() != null) {
      user.setTelephone(updateDTO.getTelephone());
    }

    if (updateDTO.getEmail() != null) {
      user.setEmail(updateDTO.getEmail());
    }

    if (updateDTO.getLocation() != null) {
      user.setLocation(updateDTO.getLocation());
    }

    User updatedUser = userRepository.save(user);

    UserVO userVO = new UserVO();
    BeanUtils.copyProperties(updatedUser, userVO);
    return userVO;
  }

  /**
   * 生成JWT令牌
   * <p>
   * 基于用户信息生成包含用户名和角色的JWT认证令牌。
   * </p>
   * 
   * @param user 用户实体对象
   * @return 生成的JWT令牌字符串
   */
  private String generateJwtToken(User user) {
    return jwtUtils.generateToken(user.getUsername(), user.getRole());
  }
}