package com.example.student.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户请求DTO
 */
@Data
public class UserRequest {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 2, max = 50, message = "用户名长度为2-50个字符")
    private String username;

    @Size(min = 6, max = 20, message = "密码长度为6-20个字符")
    private String password;

    private String realName;

    @Email(message = "邮箱格式不正确")
    private String email;

    private String phone;

    private String avatar;

    private Integer gender;

    private Integer status;

    private Integer userType;

    private Long roleId;
}
