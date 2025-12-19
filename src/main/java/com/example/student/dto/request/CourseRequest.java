package com.example.student.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 课程请求DTO
 */
@Data
public class CourseRequest {

    @NotBlank(message = "课程编号不能为空")
    private String courseCode;

    @NotBlank(message = "课程名称不能为空")
    private String courseName;

    private BigDecimal credit;

    private Integer hours;

    private Integer courseType;

    private Long teacherId;

    private String department;

    private String semester;

    private Integer maxStudents;

    private String description;

    private Integer status;
}
