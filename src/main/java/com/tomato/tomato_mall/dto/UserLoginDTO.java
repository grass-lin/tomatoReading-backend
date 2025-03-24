package com.tomato.tomato_mall.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 用户登录数据传输对象
 * <p>
 * 该DTO封装了用户登录所需的基本信息，用于前端向后端传递登录请求数据。
 * 包含用户名和密码两个必填字段，通过Bean Validation进行基本的数据验证。
 * </p>
 * <p>
 * 该对象通常用于：
 * 1. 接收前端登录表单提交的数据
 * 2. 在控制器和服务层之间传递登录凭据
 * 3. 作为身份验证过程的输入参数
 * </p>
 *
 * @author Team CBDDL
 * @version 1.0
 */
@Data
public class UserLoginDTO {
    /**
     * 用户名
     * <p>
     * 用户的唯一标识符，用于登录系统。不能为空。
     * </p>
     */
    @NotBlank(message = "Username is required")
    private String username;

    /**
     * 密码
     * <p>
     * 用户的登录密码，不能为空。
     * 密码在传输过程中为明文，但在验证时会与数据库中的加密密码进行匹配。
     * </p>
     */
    @NotBlank(message = "Password is required")
    private String password;
}