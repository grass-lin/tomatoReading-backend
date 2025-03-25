package com.tomato.tomato_mall.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户实体类
 * <p>
 * 该类定义了系统中用户的数据结构，用于存储用户的基本信息和认证信息。
 * 作为系统的核心实体之一，User 实体与用户相关的所有操作紧密关联，
 * 包括但不限于：用户注册、登录认证、个人资料管理等功能。
 * </p>
 * <p>
 * 该实体通过JPA注解映射到数据库中的"users"表，使用自增长的ID作为主键。
 * 用户名字段设置为唯一约束，确保系统中不存在重名用户。
 * </p>
 * <p>
 * 实体使用Lombok注解简化了代码，自动生成了getter、setter、equals、hashCode和toString方法。
 * </p>
 *
 * @author Team CBDDL
 * @version 1.0
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
  /**
   * 用户ID
   * <p>
   * 系统自动生成的唯一标识符，作为用户实体的主键。
   * 采用自增长策略，由数据库在插入记录时自动分配。
   * </p>
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * 用户名
   * <p>
   * 用户登录系统的唯一标识，不允许重复，不能为空。
   * 通常由字母、数字和特定符号组成，用于用户登录和身份识别。
   * </p>
   */
  @Column(unique = true, nullable = false)
  private String username;

  /**
   * 密码
   * <p>
   * 用户的登录凭证，以加密形式存储在数据库中。
   * 不允许为空，实际存储的是经过加密算法（如BCrypt）处理后的密文，而非明文密码。
   * </p>
   */
  @Column(nullable = false)
  private String password;

  /**
   * 姓名
   * <p>
   * 用户的真实姓名或昵称，用于在系统中显示和称呼用户。
   * 不允许为空，但可以与其他用户重名。
   * </p>
   */
  @Column(nullable = false)
  private String name;

  /**
   * 头像
   * <p>
   * 用户的头像图片地址，可以是相对路径或完整URL。
   * 允许为空，若为空则系统可能会显示默认头像。
   * </p>
   */
  private String avatar;

  /**
   * 角色
   * <p>
   * 用户在系统中的角色，用于确定用户的权限级别。
   * 不允许为空，通常由系统管理员分配。
   * </p>
   */
  @Column(nullable = false)
  private String role;

  /**
   * 电话
   * <p>
   * 用户的联系电话，可以为空。
   * </p>
   */
  private String telephone;

  /**
   * 邮箱
   * <p>
   * 用户的电子邮件地址，可以为空。
   * </p>
   */
  private String email;

  /**
   * 地址
   * <p>
   * 用户的居住地址，可以为空。
   * </p>
   */
  private String location;

}