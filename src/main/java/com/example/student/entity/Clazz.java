package com.example.student.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;

/**
 * 班级实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "edu_class")
public class Clazz extends BaseEntity {

    @Column(name = "class_name", nullable = false, length = 50)
    private String className; // 班级名称

    @Column(name = "class_code", nullable = false, unique = true, length = 30)
    private String classCode; // 班级编号

    @Column(name = "grade", length = 20)
    private String grade; // 年级，如：2024级

    @Column(name = "major", length = 100)
    private String major; // 专业

    @Column(name = "department", length = 100)
    private String department; // 院系

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "head_teacher_id")
    private Teacher headTeacher; // 班主任

    @Column(name = "student_count")
    private Integer studentCount = 0; // 学生人数

    @Column(name = "description", length = 500)
    private String description; // 班级描述

    @Column(name = "status")
    private Integer status = 1; // 1: 正常, 0: 已毕业
}
