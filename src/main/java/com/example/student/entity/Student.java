package com.example.student.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 学生实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "edu_student", indexes = {
    @Index(name = "idx_student_no", columnList = "student_no"),
    @Index(name = "idx_class_id", columnList = "class_id")
})
public class Student extends BaseEntity {

    @Column(name = "student_no", nullable = false, unique = true, length = 30)
    private String studentNo; // 学号

    @Column(name = "name", nullable = false, length = 50)
    private String name; // 姓名

    @Column(name = "gender")
    private Integer gender; // 0: 未知, 1: 男, 2: 女

    @Column(name = "birth_date")
    private LocalDate birthDate; // 出生日期

    @Column(name = "id_card", length = 20)
    private String idCard; // 身份证号

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "address", length = 200)
    private String address; // 家庭住址

    @Column(name = "native_place", length = 100)
    private String nativePlace; // 籍贯

    @Column(name = "nation", length = 30)
    private String nation; // 民族

    @Column(name = "political_status", length = 30)
    private String politicalStatus; // 政治面貌

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id")
    private Clazz clazz; // 所属班级

    @Column(name = "enrollment_date")
    private LocalDate enrollmentDate; // 入学日期

    @Column(name = "graduation_date")
    private LocalDate graduationDate; // 毕业日期

    @Column(name = "avatar", length = 500)
    private String avatar; // 头像

    @Column(name = "status")
    private Integer status = 1; // 1: 在读, 2: 休学, 3: 退学, 4: 毕业

    @Column(name = "remark", length = 500)
    private String remark; // 备注

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // 关联登录账号
}
