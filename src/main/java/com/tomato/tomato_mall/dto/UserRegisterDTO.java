package com.tomato.tomato_mall.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户注册数据传输对象
 * <p>
 * 该DTO封装了用户注册过程中所需的全部信息，用于前端向后端传递注册请求数据。
 * 包含必填字段（用户名、密码、姓名）和可选字段（头像、电话、邮箱、地址）。
 * 所有字段都通过Bean Validation进行数据验证，确保数据的有效性。
 * </p>
 * <p>
 * 该对象通常用于：
 * 1. 接收前端注册表单提交的完整用户信息
 * 2. 在控制器和服务层之间传递用户创建数据
 * 3. 作为用户账号创建过程的输入参数
 * </p>
 *
 * @author Team CBDDL
 * @version 1.0
 */
@Data
public class UserRegisterDTO {
    /**
     * 用户名
     * <p>
     * 用户的唯一标识符，用于登录系统。
     * 必须介于3到20个字符之间，且不能与现有用户重复。
     * </p>
     */
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    private String username;

    /**
     * 密码
     * <p>
     * 用户的登录密码。
     * 必须介于6到40个字符之间，建议使用包含字母、数字和特殊字符的强密码。
     * 密码会在保存前进行安全加密处理。
     * </p>
     */
    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 40, message = "Password must be between 6 and 40 characters")
    private String password;

    /**
     * 姓名
     * <p>
     * 用户的真实姓名或昵称，用于显示和称呼。
     * 为必填字段，但没有特定的长度限制。
     * </p>
     */
    @NotBlank(message = "Name is required")
    private String name;

    /**
     * 头像
     * <p>
     * 用户的头像URL地址，可选字段。
     * 通常存储为图片完整URL。
     * </p>
     */
    private String avatar;
    
    /**
     * 电话号码
     * <p>
     * 用户的联系电话，可选字段。
     * 可用于账号找回或系统通知。
     * </p>
     */
    private String telephone;
    
    /**
     * 电子邮箱
     * <p>
     * 用户的电子邮箱地址，可选字段但需符合邮箱格式。
     * 可用于账号验证、找回或系统通知。
     * </p>
     */
    @Email(message = "Email should be valid")
    private String email;
    
    /**
     * 地址
     * <p>
     * 用户的地理位置或地址信息，可选字段。
     * 可用于配送、区域服务等功能。
     * </p>
     */
    private String location;
}