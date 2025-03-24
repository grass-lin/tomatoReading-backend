package com.tomato.tomato_mall.vo;

import lombok.Data;

/**
 * 用户视图对象
 * <p>
 * 该类封装了要展示给前端的用户信息，不包含敏感数据（如密码、ID等）。
 * 作为控制器层返回给客户端的数据载体，提供用户公开信息的视图表示。
 * </p>
 * <p>
 * 相比于User实体，UserVO移除了敏感字段并优化了数据结构，更适合在API接口中传输。
 * 主要用于用户信息展示、个人资料查询等场景。
 * </p>
 *
 * @author Team CBDDL
 * @version 1.0
 */
@Data
public class UserVO {
    /**
     * 用户名
     * <p>
     * 用户的唯一标识符，用于登录和显示。
     * </p>
     */
    private String username;

    /**
     * 姓名
     * <p>
     * 用户的真实姓名或昵称，用于在系统中显示。
     * </p>
     */
    private String name;

    /**
     * 头像
     * <p>
     * 用户的头像图片地址，可以是相对路径或完整URL。
     * </p>
     */
    private String avatar;

    /**
     * 角色
     * <p>
     * 用户在系统中的角色，用于前端进行权限控制。
     * 常见值包括："USER"（普通用户）、"ADMIN"（管理员）等。
     * </p>
     */
    private String role;

    /**
     * 电话号码
     * <p>
     * 用户的联系电话。
     * </p>
     */
    private String telephone;

    /**
     * 电子邮箱
     * <p>
     * 用户的电子邮件地址。
     * </p>
     */
    private String email;

    /**
     * 地址
     * <p>
     * 用户的物理地址或所在地信息。
     * </p>
     */
    private String location;
}