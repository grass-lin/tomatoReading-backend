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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.tomato.tomato_mall.enums.ErrorTypeEnum;

import java.util.List;
import java.util.stream.Collectors;

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
   * @param userRepository  用户数据访问对象
   * @param passwordEncoder 密码编码器
   * @param jwtUtils        JWT工具类
   */
  public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtils jwtUtils) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtUtils = jwtUtils;
  }

  @Override
  @Transactional
  public UserVO register(UserRegisterDTO registerDTO) {
    if (userRepository.existsByUsername(registerDTO.getUsername())) {
      throw new BusinessException(ErrorTypeEnum.USERNAME_ALREADY_EXISTS);
    }

    User user = new User();
    BeanUtils.copyProperties(registerDTO, user);
    user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));

    User savedUser = userRepository.save(user);

    return convertToUserVO(savedUser);
  }

  @Override
  public String login(UserLoginDTO loginDTO) {
    User user = userRepository.findByUsername(loginDTO.getUsername())
        .orElseThrow(() -> new BusinessException(ErrorTypeEnum.USER_NOT_FOUND));

    if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
      throw new BusinessException(ErrorTypeEnum.INCORRECT_PASSWORD);
    }

    return generateJwtToken(user);
  }

  @Override
  public UserVO getUserByUsername(String username) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUsername = authentication.getName();

    if (!currentUsername.equals(username)) {
      throw new BusinessException(ErrorTypeEnum.USER_NOT_BELONG_TO_USER);
    }

    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new BusinessException(ErrorTypeEnum.USER_NOT_FOUND));

    return convertToUserVO(user);
  }

  @Override
  public List<UserVO> getAllUsers() {
    List<User> users = userRepository.findAll();
    return users.stream().map(this::convertToUserVO)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional
  public UserVO updateUser(UserUpdateDTO updateDTO) {
    // 权限检查，仅管理员或用户本人可更新
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUsername = authentication.getName();

    boolean isAdmin = authentication.getAuthorities().stream()
        .anyMatch(authority -> authority.getAuthority().equals("ROLE_admin"));
    if (!isAdmin && !currentUsername.equals(updateDTO.getUsername())) {
      throw new BusinessException(ErrorTypeEnum.USER_NOT_BELONG_TO_USER);
    }

    User user = userRepository.findByUsername(updateDTO.getUsername())
        .orElseThrow(() -> new BusinessException(ErrorTypeEnum.USER_NOT_FOUND));
    // 全量更新, 符合 PUT 语义
    // BeanUtils.copyProperties(updateDTO, user);

    // Bad Practice: 只更新非空字段
    if (updateDTO.getName() != null) {
      user.setName(updateDTO.getName());
    }
    if (updateDTO.getAvatar() != null) {
      user.setAvatar(updateDTO.getAvatar());
    }
    if (updateDTO.getRole() != null) {
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
    // 更新密码
    if (updateDTO.getPassword() != null && !updateDTO.getPassword().isEmpty()) {
      user.setPassword(passwordEncoder.encode(updateDTO.getPassword()));
    }
    User updatedUser = userRepository.save(user);
    return convertToUserVO(updatedUser);
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

  /**
   * 将用户实体转换为视图对象
   * <p>
   * 封装用户实体到前端展示层所需的数据结构，剔除敏感字段。
   * 此方法负责数据模型层到展示层的转换，确保实体内部结构不直接暴露给外部。
   * </p>
   *
   * @param user 要转换的用户实体
   * @return 转换后的用户视图对象
   */
  private UserVO convertToUserVO(User user) {
    UserVO userVO = new UserVO();
    BeanUtils.copyProperties(user, userVO);
    return userVO;
  }
}