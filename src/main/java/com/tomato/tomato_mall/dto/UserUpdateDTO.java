package com.tomato.tomato_mall.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户信息更新数据传输对象
 * <p>
 * 该DTO封装了用户信息更新所需的数据，用于前端向后端传递更新请求。
 * 设计遵循部分更新原则，除用户名外的其他字段均为可选，仅更新提供的非空字段。
 * 所有字段都通过Bean Validation进行数据验证，确保数据的有效性。
 * </p>
 * <p>
 * 该对象通常用于：
 * 1. 接收前端用户资料编辑表单提交的数据
 * 2. 在控制器和服务层之间传递用户更新数据
 * 3. 作为用户信息更新过程的输入参数
 * </p>
 * <p>
 * 用户名字段是必填的，主要用于标识要更新的用户，而非作为可更新的内容。
 * </p>
 *
 * @author Team CBDDL
 * @version 1.0
 */
@Data
public class UserUpdateDTO {
    /**
     * 用户名
     * <p>
     * 用户的唯一标识符，用于定位要更新的用户账号。
     * 该字段为必填且不可修改，仅用于用户识别。
     * </p>
     */
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    private String username;

    /**
     * 姓名
     * <p>
     * 用户的显示名称，可选更新字段。
     * 如果提供则更新，否则保持原值不变。
     * </p>
     */
    private String name;

    /**
     * 头像
     * <p>
     * 用户的头像URL地址，可选更新字段。
     * 通常存储为图片的相对路径或完整URL。
     * </p>
     */
    private String avatar;
    
    /**
     * 电话号码
     * <p>
     * 用户的联系电话，可选更新字段。
     * 可用于账号找回或系统通知。
     * </p>
     */
    private String telephone;

    /**
     * 电子邮箱
     * <p>
     * 用户的电子邮箱地址，可选更新字段但需符合邮箱格式。
     * 可用于账号验证、找回或系统通知。
     * </p>
     */
    @Email(message = "Email should be valid")
    private String email;

    /**
     * 地址
     * <p>
     * 用户的地理位置或地址信息，可选更新字段。
     * 可用于配送、区域服务等功能。
     * </p>
     */
    private String location;
    
    /**
     * 密码
     * <p>
     * 用户的新登录密码，可选更新字段。
     * 如果提供则更新密码，否则保持原密码不变。
     * 新密码会在保存前进行安全加密处理。
     * </p>
     */
    private String password;
}