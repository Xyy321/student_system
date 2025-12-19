package com.example.student.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 课程响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseResponse {

    private Long id;
    private String courseCode;
    private String courseName;
    private BigDecimal credit;
    private Integer hours;
    private Integer courseType;
    private Long teacherId;
    private String teacherName;
    private String department;
    private String semester;
    private Integer maxStudents;
    private Integer currentStudents;
    private String description;
    private Integer status;
    private LocalDateTime createdAt;
}
