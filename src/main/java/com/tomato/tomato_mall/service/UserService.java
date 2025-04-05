package com.tomato.tomato_mall.service;

import com.tomato.tomato_mall.dto.UserLoginDTO;
import com.tomato.tomato_mall.dto.UserRegisterDTO;
import com.tomato.tomato_mall.dto.UserUpdateDTO;
import com.tomato.tomato_mall.vo.UserVO;
import java.util.NoSuchElementException;
import org.springframework.security.authentication.BadCredentialsException;

/**
 * 用户服务接口
 * <p>
 * 该接口定义了用户账户相关的核心业务功能，包括用户注册、登录认证、信息查询和更新等。
 * 作为系统用户管理的核心组件，提供了用户全生命周期的管理功能。
 * </p>
 * <p>
 * 接口的实现类通常需要与用户数据访问层、加密服务和JWT令牌生成工具等组件协作，
 * 以完成用户信息的持久化和安全认证等功能。
 * </p>
 *
 * @author Team CBDDL
 * @version 1.0
 * @see com.tomato.tomato_mall.repository.UserRepository
 * @see com.tomato.tomato_mall.util.JwtUtils
 */
public interface UserService {

    /**
     * 用户注册
     * <p>
     * 创建新用户账户，包括验证用户名唯一性、加密密码、分配默认角色等操作。
     * 注册成功后返回不包含敏感信息的用户视图对象。
     * </p>
     *
     * @param registerDTO 用户注册数据传输对象，包含注册所需的用户信息
     * @return 注册成功的用户视图对象
     * @throws UsernameBusinessException 当用户名已被占用时抛出此异常
     */
    UserVO register(UserRegisterDTO registerDTO);

    /**
     * 用户登录
     * <p>
     * 验证用户提供的凭据（用户名和密码），若验证成功则生成JWT令牌作为认证凭证。
     * 该方法不直接处理令牌的存储（如设置Cookie），仅负责生成有效的令牌字符串。
     * </p>
     *
     * @param loginDTO 用户登录数据传输对象，包含用户名和密码
     * @return 生成的JWT令牌字符串
     * @throws NoSuchElementException 当用户不存在时抛出此异常
     * @throws BadCredentialsException 当密码不正确时抛出此异常
     */
    String login(UserLoginDTO loginDTO);

    /**
     * 获取用户信息
     * <p>
     * 根据用户名查询用户详细信息，返回不包含敏感数据的用户视图对象。
     * 该方法通常用于个人资料页面的数据获取。
     * </p>
     *
     * @param username 要查询的用户名
     * @return 用户视图对象
     * @throws NoSuchElementException 当用户不存在时抛出此异常
     */
    UserVO getUserByUsername(String username);

    /**
     * 更新用户信息
     * <p>
     * 根据提供的更新数据对用户信息进行部分更新。遵循部分更新原则，只更新提供的非空字段。
     * 若更新密码，会对密码进行加密处理后再存储。
     * </p>
     *
     * @param updateDTO 用户更新数据传输对象，包含要更新的字段
     * @return 更新后的用户视图对象
     * @throws NoSuchElementException 当要更新的用户不存在时抛出此异常
     */
    UserVO updateUser(UserUpdateDTO updateDTO);
}