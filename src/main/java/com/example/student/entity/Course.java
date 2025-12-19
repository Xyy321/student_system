package com.example.student.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 课程实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "edu_course")
public class Course extends BaseEntity {

    @Column(name = "course_code", nullable = false, unique = true, length = 30)
    private String courseCode; // 课程编号

    @Column(name = "course_name", nullable = false, length = 100)
    private String courseName; // 课程名称

    @Column(name = "credit", precision = 3, scale = 1)
    private BigDecimal credit; // 学分

    @Column(name = "hours")
    private Integer hours; // 学时

    @Column(name = "course_type")
    private Integer courseType; // 1: 必修, 2: 选修, 3: 公选

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    private Teacher teacher; // 授课教师

    @Column(name = "department", length = 100)
    private String department; // 开课院系

    @Column(name = "semester", length = 30)
    private String semester; // 学期，如：2024-2025-1

    @Column(name = "max_students")
    private Integer maxStudents; // 最大选课人数

    @Column(name = "current_students")
    private Integer currentStudents = 0; // 当前选课人数

    @Column(name = "description", length = 1000)
    private String description; // 课程描述

    @Column(name = "status")
    private Integer status = 1; // 1: 开课中, 0: 已结课
}
