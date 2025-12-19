package com.example.student.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 教师实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "edu_teacher")
public class Teacher extends BaseEntity {

    @Column(name = "teacher_no", nullable = false, unique = true, length = 30)
    private String teacherNo; // 教师工号

    @Column(name = "name", nullable = false, length = 50)
    private String name; // 姓名

    @Column(name = "gender")
    private Integer gender; // 0: 未知, 1: 男, 2: 女

    @Column(name = "birth_date")
    private LocalDate birthDate; // 出生日期

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "department", length = 100)
    private String department; // 所属院系

    @Column(name = "title", length = 50)
    private String title; // 职称：教授、副教授、讲师等

    @Column(name = "education", length = 50)
    private String education; // 学历：博士、硕士等

    @Column(name = "entry_date")
    private LocalDate entryDate; // 入职日期

    @Column(name = "address", length = 200)
    private String address;

    @Column(name = "avatar", length = 500)
    private String avatar; // 头像

    @Column(name = "introduction", length = 1000)
    private String introduction; // 个人简介

    @Column(name = "status")
    private Integer status = 1; // 1: 在职, 0: 离职

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // 关联登录账号
}
