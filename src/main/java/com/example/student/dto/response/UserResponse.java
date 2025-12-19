package com.example.student.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Long id;
    private String username;
    private String realName;
    private String email;
    private String phone;
    private String avatar;
    private Integer gender;
    private Integer status;
    private Integer userType;
    private Long roleId;
    private String roleName;
    private LocalDateTime lastLoginTime;
    private LocalDateTime createdAt;
}
