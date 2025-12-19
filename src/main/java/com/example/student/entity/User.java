package com.example.student.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;

/**
 * 用户实体 - 系统登录账号
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "sys_user", indexes = {
    @Index(name = "idx_username", columnList = "username"),
    @Index(name = "idx_email", columnList = "email")
})
public class User extends BaseEntity {

    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Column(name = "real_name", length = 50)
    private String realName;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "avatar", length = 500)
    private String avatar; // 头像URL

    @Column(name = "gender")
    private Integer gender; // 0: 未知, 1: 男, 2: 女

    @Column(name = "status")
    private Integer status = 1; // 1: 正常, 0: 禁用

    @Column(name = "user_type")
    private Integer userType = 1; // 1: 管理员, 2: 教师, 3: 学生

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    private Role role;

    @Column(name = "last_login_time")
    private java.time.LocalDateTime lastLoginTime;

    @Column(name = "last_login_ip", length = 50)
    private String lastLoginIp;

    // 验证码相关（用于密码重置）
    @Column(name = "verify_code", length = 10)
    private String verifyCode;

    @Column(name = "verify_code_expire_time")
    private java.time.LocalDateTime verifyCodeExpireTime;
}
