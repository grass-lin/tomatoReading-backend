package com.tomato.tomato_mall.repository;

import com.tomato.tomato_mall.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 用户数据访问仓库
 * <p>
 * 该接口负责User实体的数据库访问操作，提供了基础的CRUD功能以及用户名相关的查询方法。
 * 通过继承JpaRepository，自动获得了丰富的数据操作能力，如分页查询、排序等。
 * </p>
 * <p>
 * 作为数据访问层的核心组件，UserRepository连接了业务层与数据库，所有与用户数据
 * 相关的持久化操作都通过此接口进行。
 * </p>
 *
 * @author Team CBDDL
 * @version 1.0
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * 根据用户名查找用户
     * <p>
     * 通过唯一的用户名查询对应的用户实体。该方法基于Spring Data的命名查询约定，
     * 会自动转换为相应的SQL查询。
     * </p>
     *
     * @param username 要查询的用户名
     * @return 封装在Optional中的用户实体；如果用户不存在则返回空Optional
     */
    Optional<User> findByUsername(String username);
    
    /**
     * 检查指定用户名是否已存在
     * <p>
     * 用于验证用户名唯一性的方法，多用于用户注册环节的用户名查重。
     * 比直接查询用户实体更高效，因为只需要检查记录是否存在。
     * </p>
     *
     * @param username 要检查的用户名
     * @return 如果用户名已被占用则返回true，否则返回false
     */
    boolean existsByUsername(String username);
}